
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
import java.time.LocalDate

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
                    .userAgent("Mozilla/5.0")
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
                    val date = element.attr("data-date")

                    val cellId = element.attr("id")
                    val tooltipText = tooltipMap[cellId] ?: ""

                    val count = when {
                        tooltipText.isEmpty() -> 0
                        tooltipText.contains("No contributions") -> 0
                        else -> Regex("^(\\d+)").find(tooltipText.trim())
                            ?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    }

                    contributions.add(ContributionDay(count, level, date))
                }

                // ✅ SORT BY DATE (CRITICAL)
                val sorted = contributions.sortedBy { it.date }

                val total = sorted.sumOf { it.count }

                // ✅ CURRENT STREAK (REAL LOGIC)
                var currentStreak = 0
                var today = LocalDate.now()

                for (i in sorted.size - 1 downTo 0) {
                    val day = sorted[i]
                    val date = LocalDate.parse(day.date)

                    if (day.count > 0 && date == today) {
                        currentStreak++
                        today = today.minusDays(1)
                    } else if (day.count > 0 && date == today.minusDays(1)) {
                        currentStreak++
                        today = today.minusDays(1)
                    } else {
                        break
                    }
                }

                // ✅ LONGEST STREAK (DATE BASED)
                var longestStreak = 0
                var temp = 0

                for (i in sorted.indices) {
                    if (i == 0) {
                        temp = if (sorted[i].count > 0) 1 else 0
                    } else {
                        val prev = LocalDate.parse(sorted[i - 1].date)
                        val curr = LocalDate.parse(sorted[i].date)

                        if (sorted[i].count > 0 && prev.plusDays(1) == curr) {
                            temp++
                        } else if (sorted[i].count > 0) {
                            temp = 1
                        } else {
                            temp = 0
                        }
                    }
                    longestStreak = maxOf(longestStreak, temp)
                }

                Log.d("GitPulse", "Current: $currentStreak")
                Log.d("GitPulse", "Longest: $longestStreak")
                Log.d("GitPulse", "Last 10: ${sorted.takeLast(10).map { it.date + ":" + it.count }}")

                val prefs = getSharedPreferences("gitpulse", MODE_PRIVATE)

                runOnUiThread {
                    binding.recyclerContribution.layoutManager =
                        GridLayoutManager(this, 7, GridLayoutManager.HORIZONTAL, false)
                    binding.recyclerContribution.adapter = ContributionAdapter(sorted)

                    binding.tvTotalCommits.text = total.toString()

                    binding.tvCurrentStreak.text = currentStreak.toString()
                    binding.tvLongestStreak.text = longestStreak.toString()

                    binding.tvCurrentStreakStat.text = "$currentStreak days"
                    binding.tvLongestStreakStat.text = "$longestStreak days"

                    setupStreakGuardian(currentStreak, longestStreak)

                    prefs.edit()
                        .putString("currentStreak", currentStreak.toString())
                        .putString("longestStreak", longestStreak.toString())
                        .putBoolean("committedToday", sorted.last().count > 0)
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

