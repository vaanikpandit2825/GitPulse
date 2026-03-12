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

                // Build tooltip map: "for" attribute → tooltip text
                val tooltipMap = mutableMapOf<String, String>()
                for (tip in doc.select("tool-tip")) {
                    val forId = tip.attr("for")
                    val text = tip.text()
                    if (forId.isNotEmpty()) {
                        tooltipMap[forId] = text
                    }
                }

                // Log first few to verify matching
                tooltipMap.entries.take(3).forEach { (k, v) ->
                    Log.d("GitPulse_TIP", "for='$k' → '$v'")
                }

                val tdElements = doc.select("td.ContributionCalendar-day")
                tdElements.take(3).forEach {
                    Log.d("GitPulse_TD", "id='${it.attr("id")}' level='${it.attr("data-level")}'")
                }

                val contributions = mutableListOf<ContributionDay>()

                for (element in tdElements) {
                    val level = element.attr("data-level").toIntOrNull() ?: 0
                    val cellId = element.attr("id")
                    val tooltipText = tooltipMap[cellId] ?: ""

                    val count = when {
                        tooltipText.isEmpty() -> 0
                        tooltipText.contains("No contributions") -> 0
                        else -> Regex("^(\\d+)").find(tooltipText.trim())
                            ?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    }

                    contributions.add(ContributionDay(count, level))
                }

                val total = contributions.sumOf { it.count }
                Log.d("GitPulse", "Total contributions parsed: $total")


                var currentStreak = 0
                for (i in contributions.indices.reversed()) {
                    if (contributions[i].count > 0) currentStreak++
                    else break
                }

                var longestStreak = 0
                var streak = 0
                for (day in contributions) {
                    if (day.count > 0) {
                        streak++
                        if (streak > longestStreak) longestStreak = streak
                    } else {
                        streak = 0
                    }
                }

                runOnUiThread {
                    binding.recyclerContribution.layoutManager =
                        GridLayoutManager(this, 7, GridLayoutManager.HORIZONTAL, false)
                    binding.recyclerContribution.adapter = ContributionAdapter(contributions)

                    binding.tvUsername.text = username
                    binding.tvTotalCommits.text = total.toString()
                    binding.tvCurrentStreak.text = "$currentStreak days"
                    binding.tvLongestStreak.text = "$longestStreak days"
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