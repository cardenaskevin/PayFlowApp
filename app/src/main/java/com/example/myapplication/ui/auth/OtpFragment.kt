package com.example.payflowapp.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.payflowapp.R
import com.google.android.material.appbar.MaterialToolbar


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

        btnVerify.setOnClickListener{
            val args = OtpFragmentArgs.fromBundle(requireArguments())
            val origin = args.mode

            when (origin) {
                "recover" -> {
                    findNavController().navigate(R.id.action_otpFragment_to_newPasswordFragment)
                }
                "signup" -> {
                    findNavController().navigate(R.id.action_otpFragment_to_loginFragment)
                }
            }
            //
            //
        }
    }

}