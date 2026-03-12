package com.example.gitpulse

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

enum class Vibe {
    CYBERPUNK, COSMIC, SAKURA, HACKER, GENZ, TROPHY
}

class ShareCardView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var username: String = "octocat"
    var totalCommits: String = "328"
    var longestStreak: String = "14"
    var topLanguage: String = "Kotlin"
    var currentVibe: Vibe = Vibe.COSMIC

    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#0D1117")
        style = Paint.Style.FILL
    }

    private val cardPaint = Paint().apply {
        color = Color.parseColor("#161B22")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val usernamePaint = Paint().apply {
        color = Color.WHITE
        textSize = 52f
        isFakeBoldText = true
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
    }

    private val subtitlePaint = Paint().apply {
        color = Color.parseColor("#8B949E")
        textSize = 28f
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
    }

    private val statNumberPaint = Paint().apply {
        color = Color.parseColor("#39D353")
        textSize = 48f
        isFakeBoldText = true
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
    }

    private val statLabelPaint = Paint().apply {
        color = Color.parseColor("#8B949E")
        textSize = 24f
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
    }

    private val starPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val matrixPaint = Paint().apply {
        color = Color.parseColor("#39D353")
        textSize = 28f
        isAntiAlias = true
        typeface = Typeface.MONOSPACE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        val cardLeft = width * 0.05f
        val cardTop = height * 0.10f
        val cardRight = width * 0.95f
        val cardBottom = height * 0.90f
        val cornerRadius = 32f

        canvas.drawRoundRect(cardLeft, cardTop, cardRight, cardBottom, cornerRadius, cornerRadius, cardPaint)

        when (currentVibe) {
            Vibe.CYBERPUNK -> drawCyberpunk(canvas)
            Vibe.COSMIC -> drawCosmic(canvas)
            Vibe.SAKURA -> drawSakura(canvas)
            Vibe.HACKER -> drawHacker(canvas)
            Vibe.GENZ -> drawGenZ(canvas)
            Vibe.TROPHY -> drawTrophy(canvas)
        }

        val textX = width * 0.10f
        val textY = height * 0.25f

        canvas.drawText("@$username", textX, textY, usernamePaint)
        canvas.drawText("GitHub Developer", textX, textY + 50f, subtitlePaint)

        val stat1X = width * 0.10f
        val stat1Y = height * 0.45f

        canvas.drawText(totalCommits, stat1X, stat1Y, statNumberPaint)
        canvas.drawText("commits", stat1X, stat1Y + 35f, statLabelPaint)

        val stat2X = width * 0.42f
        canvas.drawText(longestStreak, stat2X, stat1Y, statNumberPaint)
        canvas.drawText("longest streak", stat2X, stat1Y + 35f, statLabelPaint)

        val stat3X = width * 0.72f
        canvas.drawText(topLanguage, stat3X, stat1Y, statNumberPaint)
        canvas.drawText("top language", stat3X, stat1Y + 35f, statLabelPaint)
    }

    private fun drawCosmic(canvas: Canvas) {
        val random = Random(42)
        repeat(200) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * 3f + 1f
            starPaint.alpha = random.nextInt(100, 255)
            canvas.drawCircle(x, y, radius, starPaint)
        }

        val glowPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(80f, BlurMaskFilter.Blur.NORMAL)
        }

        glowPaint.color = Color.parseColor("#1A0050")
        canvas.drawCircle(width * 0.8f, height * 0.2f, 200f, glowPaint)

        glowPaint.color = Color.parseColor("#001A40")
        canvas.drawCircle(width * 0.2f, height * 0.7f, 150f, glowPaint)
    }

    private fun drawCyberpunk(canvas: Canvas) {
        val linePaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
        }

        linePaint.color = Color.parseColor("#1AFF9C")
        linePaint.alpha = 40

        var y = 0f
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, linePaint)
            y += 40f
        }

        var x = 0f
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), linePaint)
            x += 40f
        }

        linePaint.color = Color.parseColor("#FF2079")
        linePaint.alpha = 180
        linePaint.strokeWidth = 3f
        canvas.drawLine(0f, height * 0.6f, width * 0.4f, height * 0.6f, linePaint)

        linePaint.color = Color.parseColor("#1AFF9C")
        canvas.drawLine(width * 0.6f, height * 0.3f, width.toFloat(), height * 0.3f, linePaint)
    }

    private fun drawHacker(canvas: Canvas) {
        val chars = "01アイウエオカキクケコ10"
        val random = Random(99)
        matrixPaint.alpha = 60

        var x = width * 0.05f
        while (x < width * 0.95f) {
            var y = height * 0.10f
            while (y < height * 0.90f) {
                val char = chars[random.nextInt(chars.length)].toString()
                canvas.drawText(char, x, y, matrixPaint)
                y += 35f
            }
            x += 35f
        }
    }

    private fun drawSakura(canvas: Canvas) {
        val petalPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val random = Random(77)
        val colors = listOf("#FFB7C5", "#FFC0CB", "#FF91A4", "#FFAABB", "#FF6B8A")

        repeat(60) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val radius = random.nextFloat() * 18f + 6f
            petalPaint.color = Color.parseColor(colors[random.nextInt(colors.size)])
            petalPaint.alpha = random.nextInt(60, 160)
            canvas.drawCircle(x, y, radius, petalPaint)
        }
    }

    private fun drawGenZ(canvas: Canvas) {
        val shapePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        shapePaint.color = Color.parseColor("#8AFF00")
        shapePaint.alpha = 80
        canvas.drawRect(width * 0.7f, height * 0.05f, width * 0.95f, height * 0.15f, shapePaint)

        shapePaint.color = Color.parseColor("#FF006E")
        shapePaint.alpha = 60
        canvas.drawCircle(width * 0.15f, height * 0.85f, 80f, shapePaint)

        shapePaint.color = Color.parseColor("#FFBE0B")
        shapePaint.alpha = 70
        canvas.drawRect(width * 0.05f, height * 0.40f, width * 0.15f, height * 0.60f, shapePaint)

        val strikePaint = Paint().apply {
            color = Color.parseColor("#8AFF00")
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        canvas.drawLine(width * 0.6f, height * 0.75f, width * 0.95f, height * 0.85f, strikePaint)
    }

    private fun drawTrophy(canvas: Canvas) {
        val confettiPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val random = Random(55)
        val colors = listOf("#FFD700", "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7")

        repeat(80) {
            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            val size = random.nextFloat() * 16f + 4f
            confettiPaint.color = Color.parseColor(colors[random.nextInt(colors.size)])
            confettiPaint.alpha = random.nextInt(80, 200)
            canvas.drawRect(x, y, x + size, y + size * 0.4f, confettiPaint)
        }
    }

    fun toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}