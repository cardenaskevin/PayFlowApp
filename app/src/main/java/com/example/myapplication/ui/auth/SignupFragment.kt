package com.example.myapplication.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.HomeActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar layout
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)
        val etFecha = view.findViewById<EditText>(R.id.etFechaNacimiento)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // retrocede
        }

        etFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, y, m, d ->
                etFecha.setText("$d/${m + 1}/$y")
            }, year, month, day)

            datePickerDialog.show()
        }


        btnContinuar.setOnClickListener {
            val action = SignupFragmentDirections.actionSignupFragmentToOtpFragment("signup")
            findNavController().navigate(action)

            //findNavController().navigate(R.id.action_signupFragment_to_otpFragment)
        }


    }




}

