package com.example.payflowapp.ui.report

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.payflowapp.R
import com.example.payflowapp.data.model.ReportSummary
import com.example.payflowapp.data.viewmodel.TransacctionsViewModel


class ReportFragment : Fragment() {
    private lateinit var viewModel: TransacctionsViewModel

    private lateinit var monthDropdown: AutoCompleteTextView
    private lateinit var yearDropdown: AutoCompleteTextView

    val months = listOf(
        "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(this)[TransacctionsViewModel::class.java]

        monthDropdown = view.findViewById(R.id.monthDropdown)
        yearDropdown = view.findViewById(R.id.yearDropdown)

        // Configurar dropdown
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, months)
        monthDropdown.setAdapter(monthAdapter)
        monthDropdown.inputType = InputType.TYPE_NULL
        monthDropdown.setText("Todos", false)

        yearDropdown.setText("2025", false)
        yearDropdown.isEnabled = false

        val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", "") ?: ""

        // ✅ Cargar resumen general al iniciar
        viewModel.fetchReport(userId, month = 0, year = 2025)

        //

        monthDropdown.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, months)
        )

        monthDropdown.setText("Todos", false)

        monthDropdown.setOnClickListener {
            monthDropdown.showDropDown()
        }


        // ✅ Escuchar cambios en el mes seleccionado
        monthDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedMonth = position // 0 = Todos, 1 = Enero, ...
            Log.d("mes seleccionado", "nr. $selectedMonth")
            viewModel.fetchReport(userId, month = selectedMonth, year = 2025)
        }

        viewModel.reportData.observe(viewLifecycleOwner) { reportMap ->
            val tarjetaSummary = reportMap[1] ?: ReportSummary(0, 0.0)
            val efectivoSummary = reportMap[2] ?: ReportSummary(0, 0.0)
            val celularSummary = reportMap[3] ?: ReportSummary(0, 0.0)
            val cuentaSummary = reportMap[4] ?: ReportSummary(0, 0.0)

            val totalCount = tarjetaSummary.count + efectivoSummary.count + celularSummary.count + cuentaSummary.count

            if (totalCount == 0) {
                Toast.makeText(requireContext(), "No hay registros para ese mes", Toast.LENGTH_SHORT).show()
            }

            view.findViewById<TextView>(R.id.txtCantidadTarjeta).text = "Cantidad: ${tarjetaSummary.count}"
            view.findViewById<TextView>(R.id.txtTotalTarjeta).text = "Total: S/. ${tarjetaSummary.totalAmount}"

            view.findViewById<TextView>(R.id.txtCantidadEfectivo).text = "Cantidad: ${efectivoSummary.count}"
            view.findViewById<TextView>(R.id.txtTotalEfectivo).text = "Total: S/. ${efectivoSummary.totalAmount}"

            view.findViewById<TextView>(R.id.txtCantidadCelular).text = "Cantidad: ${celularSummary.count}"
            view.findViewById<TextView>(R.id.txtTotalCelular).text = "Total: S/. ${celularSummary.totalAmount}"

            view.findViewById<TextView>(R.id.txtCantidadCuenta).text = "Cantidad: ${cuentaSummary.count}"
            view.findViewById<TextView>(R.id.txtTotalCuenta).text = "Total: S/. ${cuentaSummary.totalAmount}"
        }



    }


}