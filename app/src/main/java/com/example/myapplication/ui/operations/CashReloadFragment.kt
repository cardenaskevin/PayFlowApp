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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.payflowapp.R
import com.example.payflowapp.data.viewmodel.TransacctionsViewModel
import com.google.android.material.appbar.MaterialToolbar

class CashReloadFragment : Fragment() {
    private lateinit var viewModel: TransacctionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cash_reload, container, false)
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
        val amout = view.findViewById<EditText>(R.id.etAmout)

        viewModel.insertResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { transacction ->
                ctForm.visibility = View.GONE
                ctMessage.visibility = View.VISIBLE
            }.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }


        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        btnTransfer.setOnClickListener {
            val amoutText = amout.text.toString()
            if(amoutText.isEmpty() || amoutText.toDouble() <= 0.0){
                Toast.makeText(requireContext(), "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
            }

            else{

                viewModel.insertTransactionCashReload(requireContext(), amoutText)
            }

        }

        btnAccept.setOnClickListener {
            findNavController().navigateUp()
        }


    }
}