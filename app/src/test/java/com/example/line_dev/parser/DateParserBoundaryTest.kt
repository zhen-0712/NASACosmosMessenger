package com.example.line_dev.parser

import com.example.line_dev.data.model.DateParser
import org.junit.Assert.*
import org.junit.Test

/**
 * 測試 APOD 日期邊界條件（最早日期 1995-06-16）
 */
class DateParserBoundaryTest {

    @Test
    fun `earliest valid APOD date returns correctly`() {
        val result = DateParser.extractDate("1995/06/16")
        assertEquals("1995-06-16", result)
    }

    @Test
    fun `date before APOD start returns null`() {
        val result = DateParser.extractDate("1995/06/15")
        assertNull(result)
    }

    @Test
    fun `date way before APOD start returns null`() {
        val result = DateParser.extractDate("1990/08/08")
        assertNull(result)
    }
}
