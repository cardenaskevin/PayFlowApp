package com.example.payflowapp.data.repository

import android.content.Context
import com.example.payflowapp.data.firebase.FirebaseHelper
import com.example.payflowapp.data.model.Transacction
import com.example.payflowapp.data.model.Wallet
import com.example.payflowapp.utils.GeneratorUtils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class TransacctionsRepository {

    fun insertTransactionCardReload(context: Context, amount: String): Task<Transacction> {
        val docRef = FirebaseHelper.transactionRef.document()
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", "") ?: ""

        val transaction = Transacction(
            id = docRef.id,
            uuid = GeneratorUtils.generateUserId(),
            userCreate = userId,
            amount = amount.toDoubleOrNull() ?: 0.0,
            type = 1,
            userSend = "card",
            userReceive = userId,
            date = GeneratorUtils.getCurrentDateTime()
        )

        return docRef.set(transaction).continueWithTask { task ->
            if (task.isSuccessful) {
                // Actualiza wallet balance...
                FirebaseHelper.walletRef
                    .whereEqualTo("userId", userId)
                    .get()
                    .continueWithTask { walletTask ->
                        if (walletTask.isSuccessful && !walletTask.result.isEmpty) {
                            val walletDoc = walletTask.result.documents.first()
                            val currentBalance = walletDoc.getDouble("balance") ?: 0.0
                            val newBalance = currentBalance + transaction.amount
                            FirebaseHelper.walletRef.document(walletDoc.id)
                                .update("balance", newBalance)
                                .continueWith { transaction } // devolvemos la transacción
                        } else {
                            Tasks.forException(Exception("Wallet no encontrada"))
                        }
                    }
            } else {
                Tasks.forException(task.exception ?: Exception("Error al registrar transacción"))
            }
        }
    }

    fun getWalletByUserId(userId: String): Task<QuerySnapshot> {
        return FirebaseHelper.walletRef.whereEqualTo("userId", userId).get()
    }

    fun getUserById(userId: String): Task<DocumentSnapshot> {
        return FirebaseHelper.userRef.document(userId).get()
    }


    fun getTransacctionsByUserId(userId: String): Task<QuerySnapshot> {
        return FirebaseHelper.transactionRef
            .whereEqualTo("userCreate", userId) // o "userCreate"
            .get()
    }
}