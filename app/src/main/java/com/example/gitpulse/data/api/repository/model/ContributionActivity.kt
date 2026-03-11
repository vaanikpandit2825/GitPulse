package com.example.gitpulse

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gitpulse.databinding.ActivityContributionBinding
import org.jsoup.Jsoup

class ContributionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContributionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContributionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("USERNAME")

        if (username != null) {
            fetchContributions(username)
        } else {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchContributions(username: String) {
        Thread {
            try {
                val url = "https://github.com/users/$username/contributions"

                val doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .timeout(10000)
                    .get()

                // ✅ Correct selector confirmed from logs
                val elements = doc.select("td.ContributionCalendar-day")

                val contributions = mutableListOf<ContributionDay>()

                for (element in elements) {
                    val level = element.attr("data-level").toIntOrNull() ?: 0
                    val tooltipText = element.select("tool-tip").text()
                    val count = Regex("(\\d+) contribution").find(tooltipText)
                        ?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    contributions.add(ContributionDay(count, level))
                }

                Log.d("GitPulse", "✅ Parsed ${contributions.size} days")

                runOnUiThread {
                    binding.recyclerContribution.layoutManager =
                        GridLayoutManager(this, 7)
                    binding.recyclerContribution.adapter =
                        ContributionAdapter(contributions)
                }

            } catch (e: Exception) {
                Log.e("GitPulse", "Error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}