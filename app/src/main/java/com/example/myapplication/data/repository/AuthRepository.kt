package com.example.payflowapp.data.repository

import com.example.payflowapp.data.firebase.FirebaseHelper
import com.example.payflowapp.data.model.User
import com.example.payflowapp.data.model.Wallet
import com.example.payflowapp.utils.GeneratorUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.SetOptions


class AuthRepository {

    fun registerUser(user: User): Task<Void> {
        return FirebaseHelper.userRef.document(user.id).set(user)
    }

    fun updateUserLocation(userId: String, latitud: Double, longitud: Double): Task<Void> {
        return FirebaseHelper.userRef.document(userId).update(
            mapOf(
                "latitud" to latitud,
                "longitud" to longitud
            )
        )
    }

    fun registerWallet(wallet: Wallet): Task<Void> {
        return FirebaseHelper.walletRef.document(wallet.id).set(wallet, SetOptions.merge())
    }

    fun registerUserAndWallet(user: User, wallet: Wallet, onResult: (Result<Boolean>) -> Unit) {
        registerUser(user)
            .addOnSuccessListener {
                registerWallet(wallet)
                    .addOnSuccessListener {
                        onResult(Result.success(true))
                    }
                    .addOnFailureListener { e ->
                        onResult(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                onResult(Result.failure(e))
            }
    }

    fun checkUserExists(dni: String, androidId: String, callback: (String?) -> Unit) {
        FirebaseHelper.userRef
            .whereEqualTo("dni", dni)
            .get()
            .addOnSuccessListener { dniDocs ->
                if (!dniDocs.isEmpty) {
                    callback("El número de DNI ya está registrado.")
                    return@addOnSuccessListener
                }

                FirebaseHelper.userRef
                    .whereEqualTo("androidId", androidId)
                    .get()
                    .addOnSuccessListener { androidDocs ->
                        if (!androidDocs.isEmpty) {
                            callback("Este dispositivo ya está asociado a una cuenta.")
                        } else {
                            callback(null) // Todo OK
                        }
                    }
                    .addOnFailureListener {
                        callback("Error al validar el dispositivo.")
                    }

            }
            .addOnFailureListener {
                callback("Error al validar el DNI.")
            }
    }

    fun loginWithAndroidIdAndPassword(
        androidId: String,
        password: String,
        callback: (Result<User>) -> Unit
    ) {
        FirebaseHelper.userRef
            .whereEqualTo("androidId", androidId)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.documents[0].toObject(User::class.java)
                    if (user != null) {
                        callback(Result.success(user))
                    } else {
                        callback(Result.failure(Exception("Error al obtener datos del usuario.")))
                    }
                } else {
                    callback(Result.failure(Exception("Credenciales incorrectas o usuario no registrado.")))
                }
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    fun getUserById(userId: String, callback: (User?) -> Unit) {
        FirebaseHelper.userRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                callback(null)
            }
    }

    fun checkUserExists00(dni: String, androidId: String, callback: (Boolean) -> Unit) {
        FirebaseHelper.userRef
            .whereEqualTo("dni", dni)
            .get()
            .addOnSuccessListener { dniDocs ->
                if (!dniDocs.isEmpty) {
                    callback(true) // DNI ya registrado
                    return@addOnSuccessListener
                }

                FirebaseHelper.userRef
                    .whereEqualTo("androidId", androidId)
                    .get()
                    .addOnSuccessListener { androidDocs ->
                        callback(!androidDocs.isEmpty) // true si Android ID ya existe
                    }
                    .addOnFailureListener {
                        callback(true) // Por seguridad, si falla, consideramos que ya existe
                    }

            }
            .addOnFailureListener {
                callback(true) // También en error, devolvemos true
            }
    }


}