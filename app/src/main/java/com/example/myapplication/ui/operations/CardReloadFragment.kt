package com.example.payflowapp.ui.operations

import android.os.Bundle
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
import com.example.payflowapp.data.viewmodel.AuthViewModel
import com.example.payflowapp.data.viewmodel.TransacctionsViewModel
import com.example.payflowapp.utils.Validations
import com.google.android.material.appbar.MaterialToolbar

class CardReloadFragment : Fragment() {
    private lateinit var viewModel: TransacctionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_reload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TransacctionsViewModel::class.java]

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val ctForm = view.findViewById<ScrollView>(R.id.svContenForm)
        val ctMessage = view.findViewById<LinearLayout>(R.id.ttSuccesView)
        val btnAccept = view.findViewById<Button>(R.id.btnHome)
        //
        val btnTransfer = view.findViewById<Button>(R.id.btnContinuar)
        val nroTarjeta = view.findViewById<EditText>(R.id.etNroTarjeta)
        val cvv = view.findViewById<EditText>(R.id.etCvv)
        val montoRecargar = view.findViewById<EditText>(R.id.etMonto)
        // vista succes
        val uuid = view.findViewById<TextView>(R.id.tvUuidS)
        val date = view.findViewById<TextView>(R.id.tvHoraS)
        val nro = view.findViewById<TextView>(R.id.tvNroCuentaS)
        val amout = view.findViewById<TextView>(R.id.tvMontoS)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        btnTransfer.setOnClickListener {
            val monto = montoRecargar.text.toString()
            val cardNumber = nroTarjeta.text.toString()
            val cvvCode = cvv.text.toString()

            when {
                monto.isEmpty() || monto.toDoubleOrNull() == null || monto.toDouble() <= 0.0 -> {
                    Toast.makeText(requireContext(), "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
                }

                !Validations.isValidCardNumber(cardNumber) -> {
                    Toast.makeText(requireContext(), "Número de tarjeta inválido", Toast.LENGTH_SHORT).show()
                }

                !Validations.isValidCVV(cvvCode) -> {
                    Toast.makeText(requireContext(), "CVV inválido", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    viewModel.insertTransactionCardReload(requireContext(), monto)
                }
            }
        }


        btnAccept.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.insertResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { transacction ->
                ctForm.visibility = View.GONE
                ctMessage.visibility = View.VISIBLE
                val cardNumber = nroTarjeta.text.toString()
                val lastDigits = if (cardNumber.length >= 4) cardNumber.takeLast(4) else cardNumber
                val maskedCard = "**$lastDigits"
                uuid.setText("Nro. Operación: ${transacction.uuid}")
                date.setText("Hora: ${transacction.date}")
                nro.setText("Nro. Cuenta: $maskedCard")
                amout.setText("S/. ${transacction.amount}")
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }


        /*viewModel.insertResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                ctForm.visibility = View.GONE
                ctMessage.visibility = View.VISIBLE
                //Toast.makeText(requireContext(), "Recarga registrada", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }*/


    }



}