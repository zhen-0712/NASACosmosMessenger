package com.example.line_dev.parser

import com.example.line_dev.data.model.DateParser
import org.junit.Assert.*
import org.junit.Test

/**
 * 測試無效輸入處理
 */
class DateParserInvalidInputTest {

    @Test
    fun `empty string returns null`() {
        val result = DateParser.extractDate("")
        assertNull(result)
    }

    @Test
    fun `no date in input returns null`() {
        val result = DateParser.extractDate("你好，今天天氣很好")
        assertNull(result)
    }

    @Test
    fun `invalid date numbers returns null`() {
        val result = DateParser.extractDate("9999/99/99")
        assertNull(result)
    }

    @Test
    fun `random numbers not date format returns null`() {
        val result = DateParser.extractDate("12345")
        assertNull(result)
    }
}