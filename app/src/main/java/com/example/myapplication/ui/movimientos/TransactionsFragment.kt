package com.example.payflowapp.ui.movimientos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.payflowapp.R


class TransactionsFragment : Fragment() {

    private lateinit var saldoText: TextView
    private lateinit var saldoTitleText: TextView

    private var isSaldoVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saldoText = view.findViewById(R.id.saldoText)
        saldoTitleText  = view.findViewById(R.id.saldoTitleText)
        var cardGone = view.findViewById<LinearLayout>(R.id.cardGone)
        var cardView = view.findViewById<LinearLayout>(R.id.cardView)
        var tvViewData = view.findViewById<TextView>(R.id.tvViewData)
        var tvGoneData = view.findViewById<TextView>(R.id.tvGoneData)

        tvViewData.setOnClickListener {
            cardGone.visibility = View.GONE
            cardView.visibility = View.VISIBLE
        }

        tvGoneData.setOnClickListener {
            cardGone.visibility = View.VISIBLE
            cardView.visibility = View.GONE
        }

        saldoTitleText.setOnClickListener {
            isSaldoVisible = !isSaldoVisible
            saldoText.text = if (isSaldoVisible) "$ 1,250.00" else "****$"
            saldoTitleText.text = if (isSaldoVisible) "Ocultar saldo" else "Ver Saldo"
        }

        val transactionList = view.findViewById<RecyclerView>(R.id.transactionList)
        transactionList.layoutManager = LinearLayoutManager(requireContext())

        val fakeData = listOf(
            Transaction("Supermercado", "- $45.00"),
            Transaction("Recarga", "+ $10.00"),
            Transaction("Pago online", "- $20.00"),
            Transaction("Transferencia", "+ $100.00"),
            Transaction("Supermercado", "- $45.00"),
            Transaction("Recarga", "+ $10.00"),
            Transaction("Pago online", "- $20.00"),
            Transaction("Transferencia", "+ $100.00"),
            Transaction("Supermercado", "- $45.00"),
            Transaction("Recarga", "+ $10.00"),
            Transaction("Pago online", "- $20.00"),
            Transaction("Transferencia", "+ $100.00"),
            Transaction("Supermercado", "- $45.00"),
            Transaction("Recarga", "+ $10.00"),
            Transaction("Pago online", "- $20.00"),
            Transaction("Transferencia", "+ $100.00"),
            Transaction("Supermercado", "- $45.00"),
            Transaction("Recarga", "+ $10.00"),
            Transaction("Pago online", "- $20.00"),
            Transaction("Transferencia", "+ $100.00")

        )

        transactionList.adapter = TransactionAdapter(fakeData)
    }


}