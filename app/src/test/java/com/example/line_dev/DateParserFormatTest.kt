package com.example.line_dev

import com.example.line_dev.data.model.DateParser
import org.junit.Assert.*
import org.junit.Test

/**
 * 測試日期格式解析：斜線、破折號、點號格式
 */
class DateParserFormatTest {

    @Test
    fun `slash format YYYY_MM_DD returns correct date`() {
        val result = DateParser.extractDate("1995/06/20")
        assertEquals("1995-06-20", result)
    }

    @Test
    fun `dash format YYYY-MM-DD returns correct date`() {
        val result = DateParser.extractDate("1995-06-20")
        assertEquals("1995-06-20", result)
    }

    @Test
    fun `dot format YYYY_MM_DD returns correct date`() {
        val result = DateParser.extractDate("1995.06.20")
        assertEquals("1995-06-20", result)
    }

    @Test
    fun `single digit month and day are padded correctly`() {
        val result = DateParser.extractDate("2000/1/5")
        assertEquals("2000-01-05", result)
    }
}