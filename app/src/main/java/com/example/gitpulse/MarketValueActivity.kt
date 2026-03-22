package com.example.gitpulse

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gitpulse.databinding.ActivityMarketValueBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MarketValueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketValueBinding
    private var topLanguage = "Unknown"
    private var totalStars = 0
    private var followers = 0
    private var repos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketValueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topLanguage = intent.getStringExtra("TOP_LANGUAGE") ?: "Unknown"
        totalStars = intent.getIntExtra("TOTAL_STARS", 0)
        followers = intent.getIntExtra("FOLLOWERS", 0)
        repos = intent.getIntExtra("REPOS", 0)

        binding.btnBack.setOnClickListener { finish() }

        binding.tvMarketLanguage.text = topLanguage
        binding.tvMarketRepos.text = "$repos repos"
        binding.tvMarketStars.text = "$totalStars stars"

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
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
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
                binding.tvSalaryRange.text = "Loading..."
                binding.tvBreakdown.text = "Fetching real job market data..."
                fetchSalary(topLanguage, country)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchSalary(topLanguage: String, country: String) {
        val countryCode = when {
            country.contains("India") -> "in"
            country.contains("USA") -> "us"
            country.contains("UK") -> "gb"
            country.contains("Australia") -> "au"
            country.contains("Canada") -> "ca"
            country.contains("Germany") -> "de"
            country.contains("Singapore") -> "sg"
            else -> "us"
        }

        val currencySymbol = when {
            country.contains("India") -> "₹"
            country.contains("USA") -> "$"
            country.contains("UK") -> "£"
            country.contains("Australia") -> "A$"
            country.contains("Canada") -> "C$"
            country.contains("Germany") -> "€"
            country.contains("Singapore") -> "S$"
            else -> "$"
        }

        val query = "$topLanguage developer"

        lifecycleScope.launch {
            try {
                val url = "https://api.adzuna.com/v1/api/jobs/$countryCode/search/1" +
                        "?app_id=${BuildConfig.ADZUNA_APP_ID}" +
                        "&app_key=${BuildConfig.ADZUNA_APP_KEY}" +
                        "&what=${query.replace(" ", "+")}" +
                        "&salary_include_unknown=1" +
                        "&results_per_page=20"

                val response = withContext(Dispatchers.IO) {
                    java.net.URL(url).readText()
                }

                val json = JSONObject(response)
                val results = json.getJSONArray("results")

                var totalSalary = 0.0
                var count = 0

                for (i in 0 until results.length()) {
                    val job = results.getJSONObject(i)
                    if (job.has("salary_min") && job.has("salary_max")) {
                        val min = job.getDouble("salary_min")
                        val max = job.getDouble("salary_max")
                        if (min > 0 && max > 0) {
                            totalSalary += (min + max) / 2
                            count++
                        }
                    }
                }

                if (count > 0) {
                    val avgSalary = (totalSalary / count).toInt()
                    val minSalary = (avgSalary * 0.85).toInt()
                    val maxSalary = (avgSalary * 1.15).toInt()

                    val countryName = country.drop(3)

                    binding.tvSalaryRange.text = "$currencySymbol${formatNumber(minSalary)} - $currencySymbol${formatNumber(maxSalary)}"
                    binding.tvBreakdown.text = "Based on $count real $topLanguage developer jobs in $countryName\n\nAverage market salary from live job listings powered by Adzuna."

                } else {
                    binding.tvSalaryRange.text = "No data found"
                    binding.tvBreakdown.text = "No salary data available for $topLanguage developer in ${country.drop(3)} right now. Try a different country!"
                }

            } catch (e: Exception) {
                binding.tvSalaryRange.text = "Failed to load"
                binding.tvBreakdown.text = "Could not fetch salary data. Check your internet connection.\n\nError: ${e.message}"
            }
        }
    }

    private fun formatNumber(number: Int): String {
        return when {
            number >= 10000000 -> "${number / 10000000}Cr"
            number >= 100000 -> "${number / 100000}.${(number % 100000) / 10000}L"
            number >= 1000 -> "${number / 1000}k"
            else -> number.toString()
        }
    }
}