package com.example.gitpulse

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

enum class Vibe {
    GRADIENT, SAKURA, TERMINAL, COSMIC
}

class ShareCardView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var username: String = "octocat"
    var longestStreak: String = "14"
    var topLanguage: String = "Kotlin"
    var devScore: String = "420"
    var avatarBitmap: Bitmap? = null
    var currentVibe: Vibe = Vibe.COSMIC

    private val bgPaint = Paint().apply { style = Paint.Style.FILL }
    private val cardPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true }
    private val avatarRingPaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 4f; isAntiAlias = true }
    private val avatarFillPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true }
    private val dividerPaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 1.5f; isAntiAlias = true }
    private val starPaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true; color = Color.WHITE }
    private val matrixPaint = Paint().apply { isAntiAlias = true; typeface = Typeface.MONOSPACE }

    private fun textPaint(color: String, size: Float, bold: Boolean = false, mono: Boolean = false) = Paint().apply {
        this.color = Color.parseColor(color)
        textSize = size
        isAntiAlias = true
        isFakeBoldText = bold
        if (mono) typeface = Typeface.MONOSPACE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        when (currentVibe) {
            Vibe.GRADIENT -> drawGradientBg(canvas, w, h)
            Vibe.SAKURA -> drawSakuraBg(canvas, w, h)
            Vibe.TERMINAL -> drawTerminalBg(canvas, w, h)
            Vibe.COSMIC -> drawCosmicBg(canvas, w, h)
        }

        drawFrostedCard(canvas, w, h)
        drawContent(canvas, w, h)
    }

    private fun drawFrostedCard(canvas: Canvas, w: Float, h: Float) {
        val isLight = currentVibe == Vibe.GRADIENT || currentVibe == Vibe.SAKURA
        cardPaint.color = if (isLight) Color.argb(180, 255, 255, 255) else Color.argb(160, 20, 27, 34)
        val left = w * 0.06f
        val top = h * 0.07f
        val right = w * 0.94f
        val bottom = h * 0.93f
        canvas.drawRoundRect(left, top, right, bottom, 36f, 36f, cardPaint)
        val borderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
            color = if (isLight) Color.argb(80, 255, 255, 255) else Color.argb(60, 255, 255, 255)
        }
        canvas.drawRoundRect(left, top, right, bottom, 36f, 36f, borderPaint)
    }

    private fun drawContent(canvas: Canvas, w: Float, h: Float) {
        val isLight = currentVibe == Vibe.GRADIENT || currentVibe == Vibe.SAKURA
        val textDark = "#1a1a2e"
        val textLight = "#FFFFFF"
        val textMuted = if (isLight) "#666688" else "#8B949E"
        val accentColor = when (currentVibe) {
            Vibe.GRADIENT -> "#7C6FF7"
            Vibe.SAKURA -> "#E8739A"
            Vibe.TERMINAL -> "#39D353"
            Vibe.COSMIC -> "#A78BFA"
        }
        val mainText = if (isLight) textDark else textLight

        canvas.drawText("GitPulse", w * 0.12f, h * 0.14f, textPaint(accentColor, w * 0.034f, mono = true))

        val vibeLabel = when (currentVibe) {
            Vibe.GRADIENT -> "Soft Gradient"
            Vibe.SAKURA -> "Sakura Calm"
            Vibe.TERMINAL -> "Dark Terminal"
            Vibe.COSMIC -> "Cosmic Minimal"
        }
        val vibePaint = textPaint(textMuted, w * 0.030f, mono = true)
        vibePaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(vibeLabel, w * 0.88f, h * 0.14f, vibePaint)

        val avatarCx = w * 0.22f
        val avatarCy = h * 0.28f
        val avatarR = w * 0.11f

        avatarFillPaint.color = Color.parseColor(accentColor)
        canvas.drawCircle(avatarCx, avatarCy, avatarR, avatarFillPaint)
        avatarRingPaint.color = Color.parseColor(accentColor)
        canvas.drawCircle(avatarCx, avatarCy, avatarR + 5f, avatarRingPaint)

        if (avatarBitmap != null) {
            val scaled = Bitmap.createScaledBitmap(avatarBitmap!!, (avatarR * 2).toInt(), (avatarR * 2).toInt(), true)
            canvas.drawBitmap(scaled, avatarCx - avatarR, avatarCy - avatarR, Paint().apply { isAntiAlias = true })
        } else {
            canvas.drawText(
                username.first().uppercaseChar().toString(),
                avatarCx, avatarCy + avatarR * 0.38f,
                textPaint("#FFFFFF", avatarR * 1.1f, bold = true).also { it.textAlign = Paint.Align.CENTER }
            )
        }

        val maxUsernameWidth = w * 0.55f
        var usernameSize = w * 0.062f
        val usernamePaintTemp = textPaint(mainText, usernameSize, bold = true, mono = true)
        while (usernamePaintTemp.measureText("@$username") > maxUsernameWidth) {
            usernameSize -= 2f
            usernamePaintTemp.textSize = usernameSize
        }
        canvas.drawText("@$username", w * 0.38f, h * 0.25f, usernamePaintTemp)

        canvas.drawText("Developer", w * 0.38f, h * 0.25f + w * 0.075f, textPaint(textMuted, w * 0.034f, mono = true))

        dividerPaint.color = Color.parseColor(accentColor)
        dividerPaint.alpha = 100
        canvas.drawLine(w * 0.12f, h * 0.42f, w * 0.88f, h * 0.42f, dividerPaint)

        val statY = h * 0.54f
        val statNumSize = w * 0.082f
        val statLabelSize = w * 0.030f

        canvas.drawText(longestStreak, w * 0.12f, statY, textPaint(accentColor, statNumSize, bold = true, mono = true))
        canvas.drawText("streak 🔥", w * 0.12f, statY + statNumSize * 0.7f, textPaint(textMuted, statLabelSize, mono = true))

        canvas.drawText(topLanguage, w * 0.45f, statY, textPaint(accentColor, statNumSize, bold = true, mono = true))
        canvas.drawText("top language", w * 0.45f, statY + statNumSize * 0.7f, textPaint(textMuted, statLabelSize, mono = true))

        dividerPaint.alpha = 60
        canvas.drawLine(w * 0.12f, h * 0.66f, w * 0.88f, h * 0.66f, dividerPaint)

        canvas.drawText(devScore, w * 0.12f, h * 0.80f, textPaint(mainText, w * 0.18f, bold = true, mono = true))
        canvas.drawText("dev score", w * 0.12f, h * 0.80f + w * 0.055f, textPaint(accentColor, w * 0.036f, mono = true))

        val brandPaint = textPaint(textMuted, w * 0.028f, mono = true)
        brandPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("⚡ made with GitPulse", w * 0.88f, h * 0.89f, brandPaint)
    }

    private fun drawGradientBg(canvas: Canvas, w: Float, h: Float) {
        val gradient = LinearGradient(
            0f, 0f, w, h,
            intArrayOf(Color.parseColor("#C8B6FF"), Color.parseColor("#B8C0FF"), Color.parseColor("#FFD6E0")),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = gradient
        canvas.drawRect(0f, 0f, w, h, bgPaint)
        bgPaint.shader = null
        val bubblePaint = Paint().apply { style = Paint.Style.FILL; isAntiAlias = true; maskFilter = BlurMaskFilter(100f, BlurMaskFilter.Blur.NORMAL) }
        bubblePaint.color = Color.parseColor("#E0AAFF")
        bubblePaint.alpha = 120
        canvas.drawCircle(w * 0.8f, h * 0.15f, 180f, bubblePaint)
        bubblePaint.color = Color.parseColor("#BDE0FE")
        bubblePaint.alpha = 100
        canvas.drawCircle(w * 0.1f, h * 0.75f, 160f, bubblePaint)
    }

    private fun drawSakuraBg(canvas: Canvas, w: Float, h: Float) {
        val gradient = LinearGradient(
            0f, 0f, 0f, h,
            intArrayOf(Color.parseColor("#FFE4E8"), Color.parseColor("#FFCCD5"), Color.parseColor("#FDB8C4")),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = gradient
        canvas.drawRect(0f, 0f, w, h, bgPaint)
        bgPaint.shader = null
        val petalPaint = Paint().apply { isAntiAlias = true; style = Paint.Style.FILL }
        val random = Random(77)
        val colors = listOf("#FFB7C5", "#FF91A4", "#FFAABB", "#FF6B8A", "#FFC8D4")
        repeat(60) {
            val x = random.nextFloat() * w
            val y = random.nextFloat() * h
            val r = random.nextFloat() * 20f + 6f
            petalPaint.color = Color.parseColor(colors[random.nextInt(colors.size)])
            petalPaint.alpha = random.nextInt(40, 110)
            canvas.drawCircle(x, y, r, petalPaint)
        }
    }

    private fun drawTerminalBg(canvas: Canvas, w: Float, h: Float) {
        bgPaint.color = Color.parseColor("#0D1117")
        bgPaint.shader = null
        canvas.drawRect(0f, 0f, w, h, bgPaint)
        matrixPaint.textSize = 26f
        matrixPaint.color = Color.parseColor("#39D353")
        matrixPaint.alpha = 35
        val chars = "01{}[]()<>;//abcdef"
        val random = Random(99)
        var x = 0f
        while (x < w) {
            var y = 0f
            while (y < h) {
                canvas.drawText(chars[random.nextInt(chars.length)].toString(), x, y, matrixPaint)
                y += 32f
            }
            x += 32f
        }
    }

    private fun drawCosmicBg(canvas: Canvas, w: Float, h: Float) {
        val gradient = LinearGradient(
            0f, 0f, w, h,
            intArrayOf(Color.parseColor("#0a0015"), Color.parseColor("#0d0a2e"), Color.parseColor("#0a1628")),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        bgPaint.shader = gradient
        canvas.drawRect(0f, 0f, w, h, bgPaint)
        bgPaint.shader = null
        val random = Random(42)
        repeat(220) {
            starPaint.alpha = random.nextInt(80, 255)
            val r = random.nextFloat() * 2.5f + 0.5f
            canvas.drawCircle(random.nextFloat() * w, random.nextFloat() * h, r, starPaint)
        }
        val glowPaint = Paint().apply { isAntiAlias = true; style = Paint.Style.FILL; maskFilter = BlurMaskFilter(150f, BlurMaskFilter.Blur.NORMAL) }
        glowPaint.color = Color.parseColor("#2A0060")
        canvas.drawCircle(w * 0.75f, h * 0.18f, 220f, glowPaint)
        glowPaint.color = Color.parseColor("#001845")
        canvas.drawCircle(w * 0.25f, h * 0.78f, 180f, glowPaint)
        val linePaint = Paint().apply { color = Color.WHITE; alpha = 30; strokeWidth = 1f; isAntiAlias = true }
        canvas.drawLine(w * 0.2f, h * 0.15f, w * 0.5f, h * 0.25f, linePaint)
        canvas.drawLine(w * 0.5f, h * 0.25f, w * 0.7f, h * 0.18f, linePaint)
        canvas.drawLine(w * 0.7f, h * 0.18f, w * 0.85f, h * 0.30f, linePaint)
    }

    fun toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}