package com.example.payflowapp.data.model

data class Transacction(val id: String = "",
                        val uuid: String = "",
                        val amount: Double = 0.0,
                        val type: Int = 0,
                        val userSend: String = "",
                        val userReceive: String = "",
                        val userCreate: String = "",
                        val date: String = ""
)