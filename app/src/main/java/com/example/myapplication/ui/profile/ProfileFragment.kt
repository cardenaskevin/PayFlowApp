package com.example.payflowapp.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.payflowapp.MainActivity
import com.example.payflowapp.R
import com.example.payflowapp.data.model.User
import com.example.payflowapp.data.viewmodel.AuthViewModel

class ProfileFragment : Fragment() {
    private lateinit var viewModel: AuthViewModel

    private lateinit var txtNombres: TextView
    private lateinit var txtPrimerApellido: TextView
    private lateinit var txtSegundoApellido: TextView
    private lateinit var txtFechaNacimiento: TextView
    private lateinit var txtCelular: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(this).get(AuthViewModel::class.java)
        txtNombres = view.findViewById(R.id.tvNombres)
        txtPrimerApellido = view.findViewById(R.id.tvPrimerApellido)
        txtSegundoApellido = view.findViewById(R.id.tvSegundoApellido)
        txtFechaNacimiento = view.findViewById(R.id.etFechaNacimiento)
        txtCelular = view.findViewById(R.id.tvCelular)

        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)
        val selectPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val id = selectPreferences.getString("user_id", "")

        viewModel.getUserById(id!!)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                showUserData(user)
            }
        }

        btnLogOut.setOnClickListener { onLogOut() }

    }



    private fun onLogOut() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // para que no vuelva al login

    }

    //mostrar datos en las cajas de texto
    private fun showUserData(user: User) {
        txtNombres.text = user.name
        txtPrimerApellido.text = user.lastname
        txtSegundoApellido.text = user.lastname
        txtFechaNacimiento.text = user.birthDate
        txtCelular.text = user.phone

    }

}