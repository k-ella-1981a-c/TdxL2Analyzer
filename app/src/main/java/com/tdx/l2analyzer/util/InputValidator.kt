package com.tdx.l2analyzer.util

object InputValidator {
    private val CODE_PATTERN = Regex("^\\d{6}$")

    fun isValidStockCode(code: String): Boolean = CODE_PATTERN.matches(code)
    fun isValidHost(host: String): Boolean = host.isNotBlank() && host.contains(".")
    fun sanitize(input: String): String = input.replace(Regex("[<>\"'&]"), "")
}
