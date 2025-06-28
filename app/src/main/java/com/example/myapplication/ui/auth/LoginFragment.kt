package com.example.payflowapp.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.payflowapp.HomeActivity
import com.example.payflowapp.R
import com.example.payflowapp.data.viewmodel.AuthViewModel
import android.provider.Settings
import com.example.payflowapp.data.viewmodel.TransactionTypeViewModel

class LoginFragment : Fragment() {

    private lateinit var passwordTextView: TextView
    private val currentPassword = StringBuilder()
    private val MAX_PASSWORD_LENGTH = 6
    private lateinit var viewModel: AuthViewModel
    private lateinit var viewModel_2: TransactionTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        viewModel_2 = ViewModelProvider(this)[TransactionTypeViewModel::class.java]
        // Llamar al método que inserta los tipos de transacción
        //viewModel.insertTypes()

        passwordTextView = view.findViewById(R.id.password)

        val btnForgotPsw = view.findViewById<TextView>(R.id.forgotPassword)
        val btnSignup = view.findViewById<TextView>(R.id.tvRegister)

        btnForgotPsw.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }

        btnSignup.setOnClickListener {
            //viewModel_2.insertTypes()
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        setupKeypad(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showExitConfirmationDialog()
        }


    }

    private fun showExitConfirmationDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Salir de la app")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ ->
                requireActivity().finish()
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

        val androidId =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        val password = currentPassword.toString()

        // Llamar al login
        viewModel.loginUser(androidId, password)

        // Observar resultado
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { user ->
                Toast.makeText(requireContext(), "Bienvenido ${user.name}", Toast.LENGTH_SHORT)
                    .show()
                // GUARDAR EN SHARED PREFERENCES
                val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                with(prefs.edit()) {
                    putString("user_id", user.id)
                    putString("name", user.name)
                    putString("card_number", user.wallet?.cardNumber)
                    putString("cvv", user.wallet?.cvv)
                    putString("expiry", user.wallet?.expiryDate)
                    user.wallet?.balance?.let { putFloat("balance", it.toFloat()) }
                    putFloat("latitud", user.latitud.toFloat())
                    putFloat("longitud", user.longitud.toFloat())
                    apply()
                }
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }.onFailure {
                Toast.makeText(
                    requireContext(),
                    it.message ?: "Error al iniciar sesión",
                    Toast.LENGTH_LONG
                ).show()
                currentPassword.clear()
                updatePasswordDisplay()
            }
        }


    }
}