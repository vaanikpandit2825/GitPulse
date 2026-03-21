package com.example.gitpulse

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gitpulse.databinding.ActivityMarketValueBinding

class MarketValueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketValueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketValueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topLanguage = intent.getStringExtra("TOP_LANGUAGE") ?: "Unknown"
        val totalStars = intent.getIntExtra("TOTAL_STARS", 0)
        val followers = intent.getIntExtra("FOLLOWERS", 0)
        val repos = intent.getIntExtra("REPOS", 0)

        val prefs = getSharedPreferences("gitpulse", MODE_PRIVATE)
        val longestStreak = prefs.getString("longestStreak", "0")?.toIntOrNull() ?: 0

        binding.btnBack.setOnClickListener { finish() }
        val streakBonus = when {
            longestStreak > 100 -> 8
            longestStreak > 30 -> 5
            longestStreak > 7 -> 2
            else -> 0
        }

        val starsBonus = when {
            totalStars > 1000 -> 10
            totalStars > 100 -> 5
            totalStars > 10 -> 2
            else -> 0
        }

        binding.tvMarketLanguage.text = topLanguage
        binding.tvMarketStreak.text = "+$streakBonus LPA"
        binding.tvMarketStars.text = "+$starsBonus LPA"

        val countries = listOf(
            "🇮🇳 India",
            "🇺🇸 USA",
            "🇬🇧 UK",
            "🇦🇺 Australia",
            "🇨🇦 Canada",
            "🇩🇪 Germany",
            "🇸🇬 Singapore"
        )

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            countries
        ) {
            // getView controls how the SELECTED item looks (the collapsed spinner)
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                // cast to TextView so we can change text styling
                (view as TextView).apply {
                    setTextColor(Color.WHITE)
                    textSize = 15f
                    setPadding(16, 0, 16, 0)
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).apply {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#161B22"))
                    textSize = 15f
                    setPadding(16, 24, 16, 24)
                }
                return view
            }
        }

        binding.spinnerCountry.adapter = adapter

        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val country = countries[position]
                updateSalary(country, topLanguage, longestStreak, totalStars, followers, repos)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateSalary(
        country: String,
        topLanguage: String,
        longestStreak: Int,
        totalStars: Int,
        followers: Int,
        repos: Int
    ) {
        val languageBase = when (topLanguage) {
            "Rust" -> 25
            "Kotlin" -> 22
            "Swift" -> 22
            "Go" -> 22
            "TypeScript" -> 20
            "Python" -> 18
            "Java" -> 17
            "JavaScript" -> 16
            "Dart" -> 15
            "C++" -> 20
            "C#" -> 17
            else -> 12
        }
        val streakBonus = when {
            longestStreak > 100 -> 8
            longestStreak > 30 -> 5
            longestStreak > 7 -> 2
            else -> 0
        }

        val starsBonus = when {
            totalStars > 1000 -> 10
            totalStars > 100 -> 5
            totalStars > 10 -> 2
            else -> 0
        }

        val followersBonus = when {
            followers > 1000 -> 8
            followers > 100 -> 4
            followers > 10 -> 1
            else -> 0
        }

        val reposBonus = when {
            repos > 50 -> 4
            repos > 20 -> 2
            repos > 5 -> 1
            else -> 0
        }
        val minLPA = languageBase + streakBonus + starsBonus + followersBonus + reposBonus
        val maxLPA = minLPA + 6
        binding.tvSalaryRange.text = when {
            country.contains("India") ->      "₹$minLPA - ₹$maxLPA LPA"
            country.contains("USA") ->        "$${minLPA * 9}k - $${maxLPA * 9}k"
            country.contains("UK") ->         "£${minLPA * 7}k - £${maxLPA * 7}k"
            country.contains("Australia") ->  "A$${minLPA * 8}k - A$${maxLPA * 8}k"
            country.contains("Canada") ->     "C$${minLPA * 8}k - C$${maxLPA * 8}k"
            country.contains("Germany") ->    "€${minLPA * 7}k - €${maxLPA * 7}k"
            country.contains("Singapore") ->  "S$${minLPA * 9}k - S$${maxLPA * 9}k"
            else ->                           "$${minLPA * 9}k - $${maxLPA * 9}k"
        }

        binding.tvBreakdown.text = """
            Base ($topLanguage):       +$languageBase LPA
            Streak bonus:              +$streakBonus LPA
            Stars bonus:               +$starsBonus LPA
            Followers bonus:           +$followersBonus LPA
            Repos bonus:               +$reposBonus LPA
            ──────────────────────────
            Total:                      $minLPA - $maxLPA LPA
        """.trimIndent()
    }
}