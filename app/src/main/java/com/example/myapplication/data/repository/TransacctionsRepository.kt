package com.example.payflowapp.data.repository

import android.content.Context
import com.example.payflowapp.data.firebase.FirebaseHelper
import com.example.payflowapp.data.model.Transacction
import com.example.payflowapp.data.model.TransferResult
import com.example.payflowapp.utils.GeneratorUtils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class TransacctionsRepository {

    fun transferPointsByPhoneNumber(
        context: Context,
        amount: Double,
        phone: String
    ): Task<TransferResult> {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val senderUserId = prefs.getString("user_id", "") ?: ""
        val transactionRef = FirebaseHelper.transactionRef.document()

        // Buscar usuario receptor por número de teléfono
        return FirebaseHelper.userRef
            .whereEqualTo("phone", phone)
            .get()
            .continueWithTask { userTask ->
                if (!userTask.isSuccessful || userTask.result.isEmpty) {
                    return@continueWithTask Tasks.forException(Exception("Número de teléfono no registrado"))
                }

                val receiverDoc = userTask.result.documents.first()
                val receiverUserId = receiverDoc.id
                val receiverName = receiverDoc.getString("name") ?: ""
                val receiverLastName = receiverDoc.getString("lastname") ?: ""

                // Validar que no sea su propio número
                if (receiverUserId == senderUserId) {
                    return@continueWithTask Tasks.forException(Exception("No puedes transferirte puntos a ti mismo"))
                }

                // Obtener billetera del remitente
                FirebaseHelper.walletRef
                    .whereEqualTo("userId", senderUserId)
                    .get()
                    .continueWithTask { senderWalletTask ->
                        if (!senderWalletTask.isSuccessful || senderWalletTask.result.isEmpty) {
                            return@continueWithTask Tasks.forException(Exception("Tu billetera no fue encontrada"))
                        }

                        val senderWalletDoc = senderWalletTask.result.documents.first()
                        val senderBalance = senderWalletDoc.getDouble("balance") ?: 0.0

                        if (senderBalance < amount) {
                            return@continueWithTask Tasks.forException(Exception("Saldo insuficiente"))
                        }

                        // Obtener billetera del receptor
                        FirebaseHelper.walletRef
                            .whereEqualTo("userId", receiverUserId)
                            .get()
                            .continueWithTask { receiverWalletTask ->
                                if (!receiverWalletTask.isSuccessful || receiverWalletTask.result.isEmpty) {
                                    return@continueWithTask Tasks.forException(Exception("La billetera del receptor no fue encontrada"))
                                }

                                val receiverWalletDoc = receiverWalletTask.result.documents.first()

                                // Crear transacción
                                val transaction = Transacction(
                                    id = transactionRef.id,
                                    uuid = GeneratorUtils.generateUserId(),
                                    userCreate = senderUserId,
                                    userSend = senderUserId,
                                    userReceive = receiverUserId,
                                    amount = amount,
                                    type = 3, // transferencia por número de teléfono
                                    date = GeneratorUtils.getCurrentDateTime()
                                )

                                // Ejecutar actualizaciones
                                val updates = listOf(
                                    FirebaseHelper.walletRef.document(senderWalletDoc.id)
                                        .update("balance", senderBalance - amount),
                                    FirebaseHelper.walletRef.document(receiverWalletDoc.id)
                                        .update("balance", (receiverWalletDoc.getDouble("balance") ?: 0.0) + amount),
                                    transactionRef.set(transaction)
                                )

                                return@continueWithTask Tasks.whenAllSuccess<Any>(updates).continueWith {
                                    TransferResult(transaction, receiverName, receiverLastName)
                                }
                            }
                    }
            }
    }

    // Transferencia por número de cuenta
    fun transferPointsByAccountNumber(
        context: Context,
        amount: Double,
        accountNumber: String
    ): Task<TransferResult> {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val senderUserId = prefs.getString("user_id", "") ?: ""
        val transactionRef = FirebaseHelper.transactionRef.document()

        return FirebaseHelper.userRef
            .whereEqualTo("wallet.cardNumber", accountNumber)
            .get()
            .continueWithTask { userTask ->
                if (!userTask.isSuccessful || userTask.result.isEmpty) {
                    return@continueWithTask Tasks.forException(Exception("Cuenta destino no encontrada"))
                }

                val receiverDoc = userTask.result.documents.first()
                val receiverUserId = receiverDoc.id
                val receiverName = receiverDoc.getString("name") ?: ""
                val receiverLastName = receiverDoc.getString("lastname") ?: ""

                if (receiverUserId == senderUserId) {
                    return@continueWithTask Tasks.forException(Exception("No puedes transferirte puntos a ti mismo"))
                }

                FirebaseHelper.walletRef
                    .whereEqualTo("userId", senderUserId)
                    .get()
                    .continueWithTask { senderWalletTask ->
                        if (!senderWalletTask.isSuccessful || senderWalletTask.result.isEmpty) {
                            return@continueWithTask Tasks.forException(Exception("Tu billetera no fue encontrada"))
                        }

                        val senderWalletDoc = senderWalletTask.result.documents.first()
                        val senderBalance = senderWalletDoc.getDouble("balance") ?: 0.0

                        if (senderBalance < amount) {
                            return@continueWithTask Tasks.forException(Exception("Saldo insuficiente"))
                        }

                        FirebaseHelper.walletRef
                            .whereEqualTo("userId", receiverUserId)
                            .get()
                            .continueWithTask { receiverWalletTask ->
                                if (!receiverWalletTask.isSuccessful || receiverWalletTask.result.isEmpty) {
                                    return@continueWithTask Tasks.forException(Exception("La billetera del receptor no fue encontrada"))
                                }

                                val receiverWalletDoc = receiverWalletTask.result.documents.first()

                                val transaction = Transacction(
                                    id = transactionRef.id,
                                    uuid = GeneratorUtils.generateUserId(),
                                    userCreate = senderUserId,
                                    userSend = senderUserId,
                                    userReceive = receiverUserId,
                                    amount = amount,
                                    type = 4,
                                    date = GeneratorUtils.getCurrentDateTime()
                                )

                                val updates = listOf(
                                    FirebaseHelper.walletRef.document(senderWalletDoc.id)
                                        .update("balance", senderBalance - amount),
                                    FirebaseHelper.walletRef.document(receiverWalletDoc.id)
                                        .update("balance", (receiverWalletDoc.getDouble("balance") ?: 0.0) + amount),
                                    transactionRef.set(transaction)
                                )

                                return@continueWithTask Tasks.whenAllSuccess<Any>(updates).continueWith {
                                    TransferResult(transaction, receiverName, receiverLastName)
                                }
                            }
                    }
            }
    }


    fun transferPointsByAccountNumber01(
        context: Context,
        amount: Double,
        accountNumber: String
    ): Task<Transacction> {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val senderUserId = prefs.getString("user_id", "") ?: ""
        val transactionRef = FirebaseHelper.transactionRef.document()

        // Buscar el usuario receptor por número de cuenta
        return FirebaseHelper.userRef
            .whereEqualTo("wallet.cardNumber", accountNumber)
            .get()
            .continueWithTask { userTask ->
                if (!userTask.isSuccessful || userTask.result.isEmpty) {
                    return@continueWithTask Tasks.forException(Exception("Cuenta destino no encontrada"))
                }

                val receiverDoc = userTask.result.documents.first()
                val receiverUserId = receiverDoc.id

                if (receiverUserId == senderUserId) {
                    return@continueWithTask Tasks.forException(Exception("No puedes transferirte puntos a ti mismo"))
                }

                // Obtener billetera del remitente
                FirebaseHelper.walletRef
                    .whereEqualTo("userId", senderUserId)
                    .get()
                    .continueWithTask { senderWalletTask ->
                        if (!senderWalletTask.isSuccessful || senderWalletTask.result.isEmpty) {
                            return@continueWithTask Tasks.forException(Exception("Tu billetera no fue encontrada"))
                        }

                        val senderWalletDoc = senderWalletTask.result.documents.first()
                        val senderBalance = senderWalletDoc.getDouble("balance") ?: 0.0

                        if (senderBalance < amount) {
                            return@continueWithTask Tasks.forException(Exception("Saldo insuficiente"))
                        }

                        // Obtener billetera del receptor
                        FirebaseHelper.walletRef
                            .whereEqualTo("userId", receiverUserId)
                            .get()
                            .continueWithTask { receiverWalletTask ->
                                if (!receiverWalletTask.isSuccessful || receiverWalletTask.result.isEmpty) {
                                    return@continueWithTask Tasks.forException(Exception("La billetera del receptor no fue encontrada"))
                                }

                                val receiverWalletDoc = receiverWalletTask.result.documents.first()

                                // Crear transacción
                                val transaction = Transacction(
                                    id = transactionRef.id,
                                    uuid = GeneratorUtils.generateUserId(),
                                    userCreate = senderUserId,
                                    userSend = senderUserId,
                                    userReceive = receiverUserId,
                                    amount = amount,
                                    type = 4, // transferencia nro cuenta
                                    date = GeneratorUtils.getCurrentDateTime()
                                )

                                // Ejecutar operaciones en paralelo
                                val updates = listOf(
                                    FirebaseHelper.walletRef.document(senderWalletDoc.id)
                                        .update("balance", senderBalance - amount),
                                    FirebaseHelper.walletRef.document(receiverWalletDoc.id)
                                        .update("balance", (receiverWalletDoc.getDouble("balance") ?: 0.0) + amount),
                                    transactionRef.set(transaction)
                                )

                                return@continueWithTask Tasks.whenAllSuccess<Any>(updates).continueWith {
                                    transaction
                                }
                            }
                    }
            }
    }


    // Pago efectivo
    fun insertTransactionCashReload(context: Context, amount: String): Task<Transacction> {
        val docRef = FirebaseHelper.transactionRef.document()
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", "") ?: ""

        val transaction = Transacction(
            id = docRef.id,
            uuid = GeneratorUtils.generateUserId(),
            userCreate = userId,
            amount = amount.toDoubleOrNull() ?: 0.0,
            type = 2,
            userSend = "Pago Efectivo",
            userReceive = userId,
            date = GeneratorUtils.getCurrentDateTime()
        )

        return docRef.set(transaction).continueWithTask { task ->
            if (task.isSuccessful) {
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
                                .continueWith { transaction } // return the transaction object
                        } else {
                            Tasks.forException(Exception("Wallet no encontrada"))
                        }
                    }
            } else {
                Tasks.forException(task.exception ?: Exception("Error al registrar transacción"))
            }
        }
    }

    //

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
            userSend = "Tarjeta",
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

    fun getTransacctionsByUserId(userId: String): Task<List<Transacction>> {
        val sendQuery = FirebaseHelper.transactionRef
            .whereEqualTo("userCreate", userId)
            // .orderBy("date", "desc")
            .get()

        val receiveQuery = FirebaseHelper.transactionRef
            .whereEqualTo("userReceive", userId)
            // .orderBy("date", Query.Direction.DESCENDING)
            .get()

        return Tasks.whenAllSuccess<QuerySnapshot>(sendQuery, receiveQuery)
            .continueWith { task ->
                val sendResults = task.result[0].documents.mapNotNull { it.toObject(Transacction::class.java) }
                val receiveResults = task.result[1].documents.mapNotNull { it.toObject(Transacction::class.java) }

                // Combinar y ordenar manualmente por fecha descendente
                (sendResults + receiveResults)
                    .distinctBy { it.id }
                    .sortedByDescending { it.date }
            }
    }



    fun getTransacctionsByUserIdOld(userId: String): Task<QuerySnapshot> {
        return FirebaseHelper.transactionRef
            .whereEqualTo("userCreate", userId) // o "userCreate"
            .get()
    }
}



