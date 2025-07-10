package com.example.payflowapp.ui.operations

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.example.payflowapp.R
import com.example.payflowapp.data.viewmodel.TransacctionsViewModel
import com.google.android.material.appbar.MaterialToolbar

class PhoneTransferFragment : Fragment() {

    private lateinit var viewModel: TransacctionsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_transfer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TransacctionsViewModel::class.java]

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val btnTransfer = view.findViewById<Button>(R.id.btnContinuar)
        val ctForm = view.findViewById<ScrollView>(R.id.svContenForm)
        val ctMessage = view.findViewById<LinearLayout>(R.id.ttSuccesView)
        val btnAccept = view.findViewById<Button>(R.id.btnHome)
        val phone = view.findViewById<EditText>(R.id.etPhone)
        val amount = view.findViewById<EditText>(R.id.etAmount)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
        val tvUuid = view.findViewById<TextView>(R.id.tvUuid)
        val tvReceiver = view.findViewById<TextView>(R.id.tvReceiver)

        viewModel.insertTransferResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { transferResult ->
                ctForm.visibility = View.GONE
                ctMessage.visibility = View.VISIBLE
                val nombre = transferResult.receiverName
                val apellido = transferResult.receiverLastName
                tvReceiver.setText("Enviado: $nombre $apellido")
                tvUuid.setText("Nro. Cuenta: ${transferResult.transaction.uuid}")
                tvAmount.setText("S/. ${transferResult.transaction.amount}")
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        btnTransfer.setOnClickListener {
            val phoneNumber = phone.text.toString().trim()

            if (phoneNumber.length != 9 ) {
                phone.error = "Número celular inválido"
                return@setOnClickListener
            }

            val numberFinal = "+51$phoneNumber"

            // Validar que el monto no esté vacío y sea mayor que 0
            val amountValue = amount.text.toString().trim().toDoubleOrNull()
            if (amountValue == null || amountValue <= 0.0) {
                Log.e("Transferencia", "El monto debe ser mayor que cero")
                amount.error = "Monto inválido"
                return@setOnClickListener // termina el proceso
            }


            viewModel.transferPointsByPhoneNumber(requireContext(),numberFinal, amountValue)

        }

        btnAccept.setOnClickListener {
            findNavController().navigateUp()
        }


    }
}