package com.example.payflowapp.data.repository

import android.util.Log
import com.example.payflowapp.data.firebase.FirebaseHelper
import com.example.payflowapp.data.model.TransactionType

class TransacctionTypeRepository {
    fun insertTransactionTypes() {
        val types = listOf(
            TransactionType(id = "1", description = "Tarjeta de Débito"),
            TransactionType(id = "2", description = "Pago Efectivo"),
            TransactionType(id = "3", description = "Transferencia por teléfono"),
            TransactionType(id = "4", description = "Transferencia CCI")
        )

        types.forEach { type ->
            FirebaseHelper.transactionTypesRef.document(type.id)
                .set(type)
                .addOnSuccessListener {
                    Log.d("Firestore", "Tipo de transacción guardado: ${type.description}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al guardar tipo: ${type.description}", e)
                }
        }
    }
}