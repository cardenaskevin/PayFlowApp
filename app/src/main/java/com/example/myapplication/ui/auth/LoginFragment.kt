package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.myapplication.HomeActivity
import com.example.myapplication.R

class LoginFragment : Fragment() {

    private lateinit var passwordTextView: TextView
    private val currentPassword = StringBuilder()
    private val MAX_PASSWORD_LENGTH = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val btnFogotPsw = view.findViewById<TextView>(R.id.forgotPassword)
        val btnSignup= view.findViewById<TextView>(R.id.tvRegister)

        passwordTextView = view.findViewById(R.id.password)


        setupKeypad(view)

        btnFogotPsw.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }

        btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }

        return view
    }

    private fun showExitConfirmationDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Salir de la app")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ ->
                requireActivity().finish() // Cierra la app
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun setupKeypad(view: View) {
        val btnIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        btnIds.forEach { id ->
            view.findViewById<Button>(id)?.setOnClickListener {
                if (currentPassword.length < MAX_PASSWORD_LENGTH) {
                    val number = (it as Button).text.toString()
                    currentPassword.append(number)
                    updatePasswordDisplay()

                    if (currentPassword.length == MAX_PASSWORD_LENGTH) {
                        onPasswordComplete()
                    }
                }
            }
        }

        view.findViewById<ImageButton>(R.id.btnDelete)?.setOnClickListener {
            if (currentPassword.isNotEmpty()) {
                currentPassword.deleteCharAt(currentPassword.length - 1)
                updatePasswordDisplay()
            }
        }
    }

    private fun updatePasswordDisplay() {
        if (currentPassword.isNotEmpty()) {
            passwordTextView.visibility = View.VISIBLE
            passwordTextView.text = "•".repeat(currentPassword.length)
        } else {
            passwordTextView.visibility = View.GONE
        }
    }

    private fun onPasswordComplete() {
        Log.d("LoginFragment", "Contraseña ingresada: $currentPassword")
        Toast.makeText(requireContext(), "Contraseña completa", Toast.LENGTH_SHORT).show()
        //findNavController().navigate(R.id.action_loginFragment_to_transactionsFragment)
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // para que no vuelva al login

        // Aquí puedes navegar o validar contraseña
        // findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
}
