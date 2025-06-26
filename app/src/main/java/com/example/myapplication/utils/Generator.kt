package com.example.payflowapp.utils

import java.text.SimpleDateFormat
import java.util.*

object GeneratorUtils {

    fun generateCardNumber(): String {
        return (1..16)
            .map { (0..9).random() }
            .joinToString("")
    }

    fun generateCVV(): String {
        return (1..3)
            .map { (0..9).random() }
            .joinToString("")
    }

    fun generateExpiryDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, 4)  // 4 años de vigencia
        val format = SimpleDateFormat("MM/yy", Locale.getDefault())
        return format.format(calendar.time)
    }

    fun generateUserId(): String {
        return UUID.randomUUID().toString()
    }
}
