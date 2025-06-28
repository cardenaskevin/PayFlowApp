package com.example.payflowapp.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.payflowapp.R
import com.example.payflowapp.data.viewmodel.AuthViewModel
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar
import com.example.payflowapp.utils.Validations
import android.provider.Settings


class SignupFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var formContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar layout
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val btnContinuar = view.findViewById<Button>(R.id.btnContinuar)
        val names = view.findViewById<EditText>(R.id.etName)
        val firstLastname = view.findViewById<EditText>(R.id.etFirstLastname)
        val secondLastname = view.findViewById<EditText>(R.id.etSecondLastname)
        val birthday = view.findViewById<EditText>(R.id.etFechaNacimiento)
        val dni = view.findViewById<EditText>(R.id.etNroDoc)
        val phone = view.findViewById<EditText>(R.id.etNroCelular)
        val password = view.findViewById<EditText>(R.id.etPassword)
        val confirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        //
        formContainer = view.findViewById(R.id.formContainer)
        progressBar = view.findViewById(R.id.progressBar)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // retrocede
        }

        birthday.setOnClickListener {
            val calendar = Calendar.getInstance()

            // Obtener fecha actual
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Restar 18 años a la fecha actual
            calendar.set(year - 18, month, day)
            val fechaLimite = calendar.timeInMillis

            // Crear el DatePicker con límite máximo
            val datePickerDialog = DatePickerDialog(requireContext(), { _, y, m, d ->
                birthday.setText(String.format("%02d/%02d/%04d", d, m + 1, y))
            }, year - 18, month, day)

            datePickerDialog.datePicker.maxDate = fechaLimite
            datePickerDialog.show()

        }

        btnContinuar.setOnClickListener {
            val androidId = Settings.Secure.getString(
                requireContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
            Log.d("AndroidID", "ID del dispositivo: $androidId")


            setFormEnabled(false) // Desactiva UI y muestra loading

            val nombres = names.text.toString().trim()
            val apellido1 = firstLastname.text.toString().trim()
            val apellido2 = secondLastname.text.toString().trim()
            val nacimiento = birthday.text.toString().trim()
            val dniTexto = dni.text.toString().trim()
            val celularTexto = phone.text.toString().trim()
            val clave = password.text.toString()
            val claveConfirmacion = confirmPassword.text.toString()

            // Validaciones
            if (!Validations.isOnlyLetters(nombres)) {
                names.error = "Solo letras"
                return@setOnClickListener
            }

            if (!Validations.isOnlyLetters(apellido1)) {
                firstLastname.error = "Solo letras"
                return@setOnClickListener
            }

            if (!Validations.isValidDNI(dniTexto)) {
                dni.error = "DNI debe tener 8 dígitos"
                return@setOnClickListener
            }

            if (!Validations.isValidPhone(celularTexto)) {
                phone.error = "Celular debe tener 9 dígitos"
                return@setOnClickListener
            }

            if (!Validations.isValidPassword(clave)) {
                password.error = "Contraseña débil"
                return@setOnClickListener
            }

            if (!Validations.isSamePassword(clave, claveConfirmacion)) {
                confirmPassword.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }


            viewModel.registerUserWithWallet(
                name = nombres,
                lastname = "$apellido1 $apellido2",
                dni = dniTexto,
                phone = "+51$celularTexto",
                birthDate = nacimiento,
                password = clave,
                confirmPassword = claveConfirmacion,
                androidId = androidId,
            )

            viewModel.registerResult.observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    setFormEnabled(true)

                    // Navegar al login
                    findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                }

                result.onFailure {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
                    setFormEnabled(true)
                }
            }


            // Si todo está ok, pasamos los datos a OTPFragment
            /*val action = SignupFragmentDirections.actionSignupFragmentToOtpFragment(
                mode = "signup",
                names = nombres,
                firstLastname = apellido1 ,
                secondLastname = apellido2,
                phone = celularTexto,
                password = clave,
                confirmPassword = claveConfirmacion,
                dni = dniTexto,
                birthday = nacimiento
            )*/


        }


    }

    private fun setFormEnabled(enabled: Boolean) {
        for (i in 0 until formContainer.childCount) {
            formContainer.getChildAt(i).isEnabled = enabled
        }
        progressBar.visibility = if (enabled) View.GONE else View.VISIBLE
    }

}
