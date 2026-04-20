package com.example.line_dev

import com.example.line_dev.data.model.DateParser
import org.junit.Assert.*
import org.junit.Test

/**
 * 測試從句子中擷取日期
 */
class DateParserExtractionTest {

    @Test
    fun `date embedded in sentence is extracted`() {
        val result = DateParser.extractDate("我的生日是 1995/06/20 那天宇宙長什麼樣子")
        assertEquals("1995-06-20", result)
    }

    @Test
    fun `date at end of sentence is extracted`() {
        val result = DateParser.extractDate("請問 2000-01-01")
        assertEquals("2000-01-01", result)
    }

    @Test
    fun `date at beginning of sentence is extracted`() {
        val result = DateParser.extractDate("1998/12/25 那天")
        assertEquals("1998-12-25", result)
    }
}