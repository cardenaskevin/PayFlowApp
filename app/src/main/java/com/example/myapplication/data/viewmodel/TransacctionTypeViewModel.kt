package com.example.payflowapp.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.payflowapp.data.repository.TransacctionTypeRepository

class TransactionTypeViewModel : ViewModel() {

    private val repository = TransacctionTypeRepository()

    fun insertTypes() {
        repository.insertTransactionTypes()
    }
}
