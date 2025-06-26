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


class RecoverPasswordFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // retrocede
        }

        val btnVerify = view.findViewById<Button>(R.id.btnVerify)
        btnVerify.setOnClickListener {
            val action = RecoverPasswordFragmentDirections
                .actionRecoverPasswordFragmentToOtpFragment("recover")
            findNavController().navigate(action)

            //findNavController().navigate(R.id.action_recoverPasswordFragment_to_otpFragment)
        }
    }




}