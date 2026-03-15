package com.example.gitpulse

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.gitpulse.databinding.ActivityShareCardBinding
import java.io.File
import java.io.FileOutputStream

class ShareCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("USERNAME") ?: ""
        val longestStreak = intent.getStringExtra("STREAK") ?: "0"
        val topLanguage = intent.getStringExtra("LANGUAGE") ?: "Unknown"
        val devScore = intent.getStringExtra("DEV_SCORE") ?: "0"

        binding.shareCardView.username = username
        binding.shareCardView.longestStreak = longestStreak
        binding.shareCardView.topLanguage = topLanguage
        binding.shareCardView.devScore = devScore
        binding.shareCardView.currentVibe = Vibe.COSMIC

        binding.btnBack.setOnClickListener { finish() }

        binding.btnVibeGradient.setOnClickListener { setVibe(Vibe.GRADIENT) }
        binding.btnVibeSakura.setOnClickListener { setVibe(Vibe.SAKURA) }
        binding.btnVibeTerminal.setOnClickListener { setVibe(Vibe.TERMINAL) }
        binding.btnVibeCosmic.setOnClickListener { setVibe(Vibe.COSMIC) }

        binding.btnShare.setOnClickListener { shareCard() }
    }

    private fun setVibe(vibe: Vibe) {
        binding.shareCardView.currentVibe = vibe
        binding.shareCardView.invalidate()
    }

    private fun shareCard() {
        val card = binding.shareCardView
        card.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )
        card.layout(0, 0, 1080, 1920)
        val bitmap = card.toBitmap()
        val file = File(cacheDir, "sharecard.png")
        val stream = FileOutputStream(file)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share your GitPulse card!"))
    }
}