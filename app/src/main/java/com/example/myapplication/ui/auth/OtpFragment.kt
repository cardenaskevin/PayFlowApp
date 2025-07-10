package com.example.payflowapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.payflowapp.R
import com.example.payflowapp.utils.OtpHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class OtpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val btnVerify = view.findViewById<Button>(R.id.btnVerify)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        var verificationId: String? = null


        val args = OtpFragmentArgs.fromBundle(requireArguments())
        val phone = args.phone

        btnVerify.setOnClickListener {
            // val phone = "922820049" // número fijo de prueba
            sendTestOtpFromFragment("922820049", this)
            // sendOtp(phone) { id ->
            //      verificationId = id
            //      Toast.makeText(requireContext(), "Código reenviado", Toast.LENGTH_SHORT).show()
            //   }
        }


        /*   btnVerify.setOnClickListener{
               sendOtp(phone) { id ->
                   verificationId = id
                   Toast.makeText(requireContext(), "Código reenviado", Toast.LENGTH_SHORT).show()
               }
               /*val origin = args.mode
               when (origin) {
                   "recover" -> {
                       findNavController().navigate(R.id.action_otpFragment_to_newPasswordFragment)
                   }
                   "signup" -> {
                       findNavController().navigate(R.id.action_otpFragment_to_loginFragment)
                   }
               }*/

           }*/
    }

    fun sendTestOtpFromFragment(phoneNumber: String, fragment: Fragment) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+51$phoneNumber") // Asegúrate que es el número de prueba de Firebase
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(fragment.requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("OTP_SIMPLE", "Auto-verificado con: ${credential.smsCode}")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("OTP_SIMPLE", "Fallo OTP: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    Log.d("OTP_SIMPLE", "OTP enviado. ID: $verificationId")
                    Toast.makeText(fragment.requireContext(), "OTP enviado a +51$phoneNumber", Toast.LENGTH_SHORT).show()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }



    private fun sendOtp(phone: String, onCodeSent: (String) -> Unit) {
        OtpHelper.sendOtp(phone, requireActivity(), object : OtpHelper.OtpCallback {
            override fun onCodeSent(verificationId: String) {
                onCodeSent(verificationId)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Puedes autocompletar el campo si quieres:
                val smsCode = credential.smsCode
                if (!smsCode.isNullOrEmpty()) {
                    //  view?.findViewById<EditText>(R.id.)?.setText(smsCode)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}