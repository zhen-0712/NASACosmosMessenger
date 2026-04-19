package com.example.line_dev.data.model

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

object DateParser {

    // 支援格式：1995/06/20、1995-06-20、1995.06.20
    private val patterns = listOf(
        Pattern.compile("""(\d{4})[/\-.](\d{1,2})[/\-.](\d{1,2})""")
    )

    fun extractDate(input: String): String? {
        for (pattern in patterns) {
            val matcher = pattern.matcher(input)
            if (matcher.find()) {
                val year = matcher.group(1)!!
                val month = matcher.group(2)!!.padStart(2, '0')
                val day = matcher.group(3)!!.padStart(2, '0')
                val dateStr = "$year-$month-$day"
                if (isValidApodDate(dateStr)) return dateStr
            }
        }
        return null
    }

    private fun isValidApodDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.isLenient = false
            val parsed = sdf.parse(date) ?: return false
            val earliest = sdf.parse("1995-06-16") ?: return false
            parsed >= earliest
        } catch (e: Exception) {
            false
        }
    }
}