package com.example.line_dev.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.line_dev.ui.theme.IceBlue
import com.example.line_dev.ui.theme.SilverMilk
import com.example.line_dev.ui.theme.SoftWhite
import com.example.line_dev.ui.theme.White
import com.example.line_dev.ui.theme.WinterGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object StarCardGenerator {

    suspend fun generateStarCard(
        context: Context,
        imageUrl: String,
        title: String,
        date: String
    ): File? = withContext(Dispatchers.IO) {
        try {
            // 下載圖片
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()
            val result = loader.execute(request)
            val photoBitmap = (result as? SuccessResult)?.drawable
                ?.let { (it as? BitmapDrawable)?.bitmap } ?: return@withContext null

            // 建立星空卡畫布
            val cardWidth = 1080
            val cardHeight = 1350
            val bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 背景色 WinterGray
            canvas.drawColor(android.graphics.Color.rgb(57, 64, 69))

            // 繪製圖片（上方 70% 區域）
            val imageHeight = (cardHeight * 0.68f).toInt()
            val scaledPhoto = Bitmap.createScaledBitmap(photoBitmap, cardWidth, imageHeight, true)
            canvas.drawBitmap(scaledPhoto, 0f, 0f, null)

            // 圖片下方漸層遮罩
            val gradientPaint = Paint()
            val gradient = LinearGradient(
                0f, (imageHeight * 0.6f),
                0f, imageHeight.toFloat(),
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.rgb(57, 64, 69),
                Shader.TileMode.CLAMP
            )
            gradientPaint.shader = gradient
            canvas.drawRect(0f, (imageHeight * 0.6f), cardWidth.toFloat(), imageHeight.toFloat(), gradientPaint)

            // 分隔線
            val dividerPaint = Paint().apply {
                color = android.graphics.Color.rgb(160, 178, 199) // SilverMilk
                strokeWidth = 1.5f
                alpha = 120
            }
            canvas.drawLine(60f, imageHeight + 60f, cardWidth - 60f, imageHeight + 60f, dividerPaint)

            // 標題文字
            val titlePaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 52f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            drawMultilineText(canvas, title, titlePaint, 60f, imageHeight + 110f, cardWidth - 120f)

            // 日期文字
            val datePaint = Paint().apply {
                color = android.graphics.Color.rgb(160, 178, 199) // SilverMilk
                textSize = 38f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            canvas.drawText(date, 60f, cardHeight - 100f, datePaint)

            // 品牌標記
            val brandPaint = Paint().apply {
                color = android.graphics.Color.rgb(160, 178, 199)
                textSize = 30f
                isAntiAlias = true
                alpha = 160
            }
            val brandText = "NASA Cosmos Messenger"
            val brandWidth = brandPaint.measureText(brandText)
            canvas.drawText(brandText, cardWidth - brandWidth - 60f, cardHeight - 60f, brandPaint)

            // 存到暫存檔
            val cacheDir = File(context.cacheDir, "star_cards").also { it.mkdirs() }
            val file = File(cacheDir, "star_card_${date}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawMultilineText(
        canvas: Canvas,
        text: String,
        paint: Paint,
        x: Float,
        y: Float,
        maxWidth: Float
    ) {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)

        val lineHeight = paint.textSize * 1.4f
        lines.take(3).forEachIndexed { index, line ->
            canvas.drawText(line, x, y + index * lineHeight, paint)
        }
    }
}