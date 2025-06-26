package com.example.payflowapp.utils

object Validations {

    fun isOnlyLetters(input: String): Boolean {
        return input.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+\$"))
    }

    fun isValidDNI(dni: String): Boolean {
        return dni.length == 8 && dni.all { it.isDigit() }
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.length == 9 && phone.all { it.isDigit() }
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6 && password.all { it.isDigit() }
    }

    fun isSamePassword(p1: String, p2: String): Boolean {
        return p1 == p2
    }
}
