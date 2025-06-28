package com.example.payflowapp.data.model

data class Wallet(val id: String = "",
                    val userId: String = "",
                  val cardNumber: String = "",
                  val cvv: String = "",
                  val expiryDate: String = "",
                  val balance: Double = 0.0)
