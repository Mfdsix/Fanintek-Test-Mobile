package com.zgenit.fanintek.utils

import java.util.regex.Pattern

class RegexHelper {

    val EMAIL_ADDRESS = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    val PASSWORD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d~!@#\$%^&*()]{8,}\$")

    fun isValidEmail(email: String): Boolean {
        val pattern: Pattern = EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        val pattern: Pattern = PASSWORD
        return pattern.matcher(password).matches()
    }
}