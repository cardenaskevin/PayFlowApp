package com.example.payflowapp.utils

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

object OtpHelper {

    interface OtpCallback {
        fun onCodeSent(verificationId: String)
        fun onVerificationCompleted(credential: PhoneAuthCredential)
        fun onVerificationFailed(e: FirebaseException)
    }

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        callback: OtpCallback
    ) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+51$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("OtpHelper", "Auto-verificado.")
                    callback.onVerificationCompleted(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OtpHelper", "Falló la verificación: ${e.message}")
                    callback.onVerificationFailed(e)
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("OtpHelper", "OTP enviado correctamente.")
                    Log.d("OtpHelper", "nro celulare")
                    callback.onCodeSent(verificationId)
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}
