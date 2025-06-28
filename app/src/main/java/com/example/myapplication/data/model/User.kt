package com.example.payflowapp.data.model

data class User(val id: String = "",
                val name: String = "",
                val lastname: String = "",
                val dni: String = "",
                val phone: String = "",
                val birthDate: String = "",
                val password: String = "",
                val confirmPassword: String = "",
                val latitud: Double = 0.0,
                val longitud: Double = 0.0,
                val androidId: String = "",
                val wallet: Wallet? = null)
