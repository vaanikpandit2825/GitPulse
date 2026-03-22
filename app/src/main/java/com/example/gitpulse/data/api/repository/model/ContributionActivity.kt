package com.example.gitpulse

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gitpulse.databinding.ActivityContributionBinding
import org.jsoup.Jsoup
import android.graphics.Color
import java.util.Calendar
import android.os.Build

class ContributionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContributionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContributionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("USERNAME")

        binding.btnBack.setOnClickListener { finish() }

        if (username != null) {
            fetchContributions(username)
        } else {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    private fun setupStreakGuardian(currentStreak: Int, longestStreak: Int) {
        binding.tvGuardianCurrentStreak.text = currentStreak.toString()
        binding.tvGuardianBestStreak.text = longestStreak.toString()

        val calendar = Calendar.getInstance()
        val hoursLeft = 23 - calendar.get(Calendar.HOUR_OF_DAY)
        val minutesLeft = 59 - calendar.get(Calendar.MINUTE)

        binding.tvTimeRemaining.text = "${hoursLeft}h ${minutesLeft}m left today"

        when {
            currentStreak == 0 -> {
                binding.tvStreakStatus.text = "💀 No Streak"
                binding.tvStreakStatus.setTextColor(Color.parseColor("#8B949E"))
                binding.tvTimeRemaining.setTextColor(Color.parseColor("#8B949E"))
                binding.tvStreakMessage.text = "Start your streak by committing today!"
            }
            hoursLeft < 2 -> {
                binding.tvStreakStatus.text = "🔴 Danger!"
                binding.tvStreakStatus.setTextColor(Color.parseColor("#FF4444"))
                binding.tvTimeRemaining.setTextColor(Color.parseColor("#FF4444"))
                binding.tvStreakMessage.text = "URGENT! Commit now to save your streak! 🚨"
            }
            hoursLeft < 6 -> {
                binding.tvStreakStatus.text = "🟡 Warning"
                binding.tvStreakStatus.setTextColor(Color.parseColor("#F0B429"))
                binding.tvTimeRemaining.setTextColor(Color.parseColor("#F0B429"))
                binding.tvStreakMessage.text = "Less than 6 hours left — don't forget to commit!"
            }
            else -> {
                binding.tvStreakStatus.text = "🟢 Safe"
                binding.tvStreakStatus.setTextColor(Color.parseColor("#39D353"))
                binding.tvTimeRemaining.setTextColor(Color.parseColor("#39D353"))
                binding.tvStreakMessage.text = "You're safe! Keep the streak alive 💪"
            }
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

                val tooltipMap = mutableMapOf<String, String>()
                for (tip in doc.select("tool-tip")) {
                    val forId = tip.attr("for")
                    val text = tip.text()
                    if (forId.isNotEmpty()) tooltipMap[forId] = text
                }

                val tdElements = doc.select("td.ContributionCalendar-day")
                val contributions = mutableListOf<ContributionDay>()

                for (element in tdElements) {
                    val level = element.attr("data-level").toIntOrNull() ?: 0
                    val cellId = element.attr("id")
                    val tooltipText = tooltipMap[cellId] ?: ""
                    val count = when {
                        tooltipText.isEmpty() -> if (level > 0) 1 else 0
                        tooltipText.contains("No contributions") -> 0
                        else -> Regex("^(\\d+)").find(tooltipText.trim())
                            ?.groupValues?.get(1)?.toIntOrNull() ?: if (level > 0) 1 else 0
                    }
                    contributions.add(ContributionDay(count, level))
                }

                val total = contributions.sumOf { it.count }

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

                var currentStreak = 0
                val streakList = contributions.dropLast(1)
                for (i in streakList.indices.reversed()) {
                    if (streakList[i].count > 0) currentStreak++
                    else break
                }

                // debug logs — placed AFTER contributions and streaks are calculated
                Log.d("GitPulse_STREAK", "Total days parsed: ${contributions.size}")
                Log.d("GitPulse_STREAK", "Longest streak: $longestStreak")
                Log.d("GitPulse_STREAK", "Current streak: $currentStreak")
                Log.d("GitPulse_STREAK", "Last 10 days: ${contributions.takeLast(10).map { it.count }}")

                val prefs = getSharedPreferences("gitpulse", MODE_PRIVATE)
                prefs.edit().putString("longestStreak", longestStreak.toString()).apply()

                runOnUiThread {
                    binding.recyclerContribution.layoutManager =
                        GridLayoutManager(this, 7, GridLayoutManager.HORIZONTAL, false)
                    binding.recyclerContribution.adapter = ContributionAdapter(contributions)

                    binding.tvTotalCommits.text = total.toString()
                    binding.tvLongestStreak.text = longestStreak.toString()
                    binding.tvCurrentStreak.text = currentStreak.toString()
                    binding.tvLongestStreakStat.text = "$longestStreak days"
                    binding.tvCurrentStreakStat.text = "$currentStreak days"

                    setupStreakGuardian(currentStreak, longestStreak)

                    prefs.edit()
                        .putString("currentStreak", currentStreak.toString())
                        .putBoolean("committedToday", currentStreak > 0)
                        .apply()

                    StreakScheduler.scheduleDailyNotification(this)
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