package com.example.payflowapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.payflowapp.data.model.User
import com.example.payflowapp.data.model.Wallet
import com.example.payflowapp.data.repository.AuthRepository
import com.example.payflowapp.utils.GeneratorUtils

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> get() = _registerResult
    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> get() = _loginResult
    private val _locationUpdateResult = MutableLiveData<Result<Boolean>>()
    val locationUpdateResult: LiveData<Result<Boolean>> get() = _locationUpdateResult

    fun updateUserLocation(userId: String, latitud: Double, longitud: Double) {
        repository.updateUserLocation(userId, latitud, longitud)
            .addOnSuccessListener {
                _locationUpdateResult.postValue(Result.success(true))
            }
            .addOnFailureListener { e ->
                _locationUpdateResult.postValue(Result.failure(e))
            }
    }

    fun registerUserWithWallet(
        name: String,
        lastname: String,
        dni: String,
        phone: String,
        birthDate: String,
        password: String,
        confirmPassword: String,
        androidId: String,
    ) {
        repository.checkUserExists(dni, androidId) { errorMessage ->
            if (errorMessage != null) {
                _registerResult.postValue(Result.failure(Exception(errorMessage)))
                return@checkUserExists
            }

            val userId = GeneratorUtils.generateUserId()
            val walletId = GeneratorUtils.generateUserId()

            val wallet = Wallet(
                id = walletId,
                userId = userId,
                cardNumber = GeneratorUtils.generateCardNumber(),
                cvv = GeneratorUtils.generateCVV(),
                expiryDate = GeneratorUtils.generateExpiryDate(),
                balance = 0.0
            )

            val user = User(
                id = userId,
                name = name,
                lastname = lastname,
                dni = dni,
                phone = phone,
                birthDate = birthDate,
                password = password,
                confirmPassword = confirmPassword,
                latitud = 0.0,
                longitud = 0.0,
                androidId = androidId,
                wallet = wallet
            )

            repository.registerUserAndWallet(user, wallet) { result ->
                _registerResult.postValue(result)
            }
        }
    }

    fun loginUser(androidId: String, password: String) {
        repository.loginWithAndroidIdAndPassword(androidId, password) { result ->
            _loginResult.postValue(result)
        }
    }

}