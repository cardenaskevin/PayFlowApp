package com.example.payflowapp.data.firebase


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


//class FirebaseHelper {
//}

object FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val firestore: FirebaseFirestore = Firebase.firestore

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val userRef: CollectionReference
        get() = firestore.collection("user")

    val walletRef: CollectionReference
        get() = firestore.collection("wallet")

    val transactionTypesRef: CollectionReference
        get() = firestore.collection("transactionTypes")

    val transactionRef: CollectionReference
        get() = firestore.collection("transacction")
}

