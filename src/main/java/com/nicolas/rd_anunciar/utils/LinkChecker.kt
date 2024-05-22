package com.nicolas.rd_anunciar.utils

fun String.linkChecker(): Boolean {

    val linkRegex = """(https?://\S+|www\.\S+\.\S+|\b\S+\.\S+\b)""".toRegex()
    return linkRegex.containsMatchIn(this)
}