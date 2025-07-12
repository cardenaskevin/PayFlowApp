package com.example.payflowapp.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.payflowapp.data.model.ReportSummary
import com.example.payflowapp.data.model.Transacction
import com.example.payflowapp.data.model.TransferResult
import com.example.payflowapp.data.model.User
import com.example.payflowapp.data.model.Wallet
import com.example.payflowapp.data.repository.TransacctionsRepository


class TransacctionsViewModel : ViewModel() {
    private val repository = TransacctionsRepository()

    private val _insertResult = MutableLiveData<Result<Transacction>>()
    val insertResult: LiveData<Result<Transacction>> get() = _insertResult

    private val _insertTransferResult = MutableLiveData<Result<TransferResult>>()
    val insertTransferResult: LiveData<Result<TransferResult>> get() = _insertTransferResult

    private val _wallet = MutableLiveData<Wallet?>()
    val wallet: LiveData<Wallet?> get() = _wallet

    private val _transactions = MutableLiveData<List<Transacction>>()
    val transactions: LiveData<List<Transacction>> get() = _transactions

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _reportData = MutableLiveData<Map<Int, ReportSummary>>()
    val reportData: LiveData<Map<Int, ReportSummary>> get() = _reportData

    fun fetchReport(userId: String, month: Int, year: Int) {
        repository.getMonthlyReportData(userId, month, year)
            .addOnSuccessListener { reportMap ->
                _reportData.postValue(reportMap)
            }
            .addOnFailureListener {
                _reportData.postValue(emptyMap())
            }
    }

    /*
    fun fetchUser(userId: String) {
        repository.getUserById(userId)
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                _user.postValue(user)
            }
            .addOnFailureListener {
                _user.postValue(null)
            }
    }
*/
    fun transferPointsByPhoneNumber(context: Context, phoneNumber: String, amount: Double) {
        repository.transferPointsByPhoneNumber(context, amount, phoneNumber)
            .addOnSuccessListener { result ->
                _insertTransferResult.postValue(Result.success(result))
            }
            .addOnFailureListener { e ->
                _insertTransferResult.postValue(Result.failure(e))
            }
    }

    fun transferPoints(context: Context, accountNumber: String, amount: Double) {
        repository.transferPointsByAccountNumber(context, amount, accountNumber)
            .addOnSuccessListener { result ->
                _insertTransferResult.postValue(Result.success(result))
            }
            .addOnFailureListener { e ->
                _insertTransferResult.postValue(Result.failure(e))
            }
    }

    fun insertTransactionCashReload(context: Context, amount: String) {
        repository.insertTransactionCashReload(context, amount)
            .addOnSuccessListener { transacction ->
                _insertResult.postValue(Result.success(transacction))
            }
            .addOnFailureListener { e ->
                _insertResult.postValue(Result.failure(e))
            }
    }



    fun insertTransactionCardReload(context: Context, amount: String) {
        repository.insertTransactionCardReload(context, amount)
            .addOnSuccessListener { transacction ->
                _insertResult.postValue(Result.success(transacction))
            }
            .addOnFailureListener { e ->
                _insertResult.postValue(Result.failure(e))
            }
    }



    fun fetchWallet(userId: String) {
        repository.getWalletByUserId(userId)
            .addOnSuccessListener { snapshot ->
                val wallet = snapshot.documents.firstOrNull()?.toObject(Wallet::class.java)
                _wallet.postValue(wallet)
            }
            .addOnFailureListener {
                _wallet.postValue(null)
            }
    }

    fun fetchTransactions(userId: String) {
        repository.getTransacctionsByUserId(userId)
            .addOnSuccessListener { combinedList ->
                _transactions.postValue(combinedList)
            }
            .addOnFailureListener {
                _transactions.postValue(emptyList())
            }
    }


    fun fetchTransactionsOld(userId: String) {
        repository.getTransacctionsByUserIdOld(userId)
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject(Transacction::class.java) }
                _transactions.postValue(list)
            }
            .addOnFailureListener {
                _transactions.postValue(emptyList())
            }
    }

}