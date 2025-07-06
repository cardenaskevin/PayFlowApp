package com.example.payflowapp.ui.movimientos

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.payflowapp.R
import com.example.payflowapp.data.viewmodel.TransacctionsViewModel


class TransactionsFragment : Fragment() {
    private lateinit var viewModel: TransacctionsViewModel


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


        var btnCopy = view.findViewById<ImageButton>(R.id.btnCopy)

        saldoText = view.findViewById(R.id.saldoText)
        saldoTitleText  = view.findViewById(R.id.saldoTitleText)
        var tvCardNumber = view.findViewById<TextView>(R.id.tvCardNumber)
        var cardGone = view.findViewById<LinearLayout>(R.id.cardGone)
        var cardView = view.findViewById<LinearLayout>(R.id.cardView)
        var tvViewData = view.findViewById<TextView>(R.id.tvViewData)
        var tvGoneData = view.findViewById<TextView>(R.id.tvGoneData)
        val transactionList = view.findViewById<RecyclerView>(R.id.transactionList)
        transactionList.layoutManager = LinearLayoutManager(requireContext())

        btnCopy.setOnClickListener {

            var copiedText = tvCardNumber.text.toString()

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("CardNumber", copiedText)
            clipboard.setPrimaryClip(clip)

            android.widget.Toast.makeText(requireContext(), "Número de tarjeta copiado", android.widget.Toast.LENGTH_SHORT).show()

        }

        tvViewData.setOnClickListener {
            cardGone.visibility = View.GONE
            cardView.visibility = View.VISIBLE
        }

        tvGoneData.setOnClickListener {
            cardGone.visibility = View.VISIBLE
            cardView.visibility = View.GONE
        }

        val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", "") ?: return

        val viewModel = ViewModelProvider(this)[TransacctionsViewModel::class.java]
        viewModel.fetchWallet(userId)
        viewModel.fetchTransactions(userId)

        viewModel.wallet.observe(viewLifecycleOwner) { wallet ->
            wallet?.let { w ->
                view.findViewById<TextView>(R.id.tvMaskedCard).text = "**** **** **** ${w.cardNumber.takeLast(4)}"
                view.findViewById<TextView>(R.id.tvCardNumber).text = w.cardNumber.chunked(4).joinToString(" ")
                view.findViewById<TextView>(R.id.tvCardCVV).text = "CVV: ${w.cvv}"
                view.findViewById<TextView>(R.id.tvCardExpiry).text = "Expira: ${w.expiryDate}"

                saldoTitleText.setOnClickListener {
                    isSaldoVisible = !isSaldoVisible
                    saldoText.text = if (isSaldoVisible) "S/. ${w.balance}" else ""
                    saldoTitleText.text = if (isSaldoVisible) "Ocultar saldo" else "Ver Saldo"
                }
            }
        }


        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            Log.d("DEBUG_TRANS", "Lista de transacciones: $list")

            list.forEach { transac ->
                Log.d("DEBUG_TRANS", "UUID: ${transac.uuid}, Monto: ${transac.amount}, Fecha: ${transac.date}")
            }
            transactionList.adapter = TransactionAdapter(list.map {
                Transaction(it.uuid, "S/. ${it.amount}")
            })
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            val lat = user?.latitud ?: 0.0
            val lng = user?.longitud ?: 0.0

            if (lat == 0.0 || lng == 0.0) {
                showZonaSeguraDialog()
            }
        }

    }


    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saldoText = view.findViewById(R.id.saldoText)
        saldoTitleText  = view.findViewById(R.id.saldoTitleText)
        var cardGone = view.findViewById<LinearLayout>(R.id.cardGone)
        var cardView = view.findViewById<LinearLayout>(R.id.cardView)
        var tvViewData = view.findViewById<TextView>(R.id.tvViewData)
        var tvGoneData = view.findViewById<TextView>(R.id.tvGoneData)
        //
        val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val cardNumber = prefs.getString("card_number", "") ?: ""
        val cvv = prefs.getString("cvv", "") ?: ""
        val expiry = prefs.getString("expiry", "") ?: ""
        val balance = prefs.getFloat("balance", 0f)
        val latitud = prefs.getFloat("latitud", 0f)
        val longitud = prefs.getFloat("longitud", 0f)

        // Mostrar últimos 4 dígitos
        val masked = "**** **** **** ${cardNumber.takeLast(4)}"
        view.findViewById<TextView>(R.id.tvMaskedCard).text = masked

        // Mostrar datos completos si se activa "Ver Datos"
        view.findViewById<TextView>(R.id.tvCardNumber).text = cardNumber.chunked(4).joinToString(" ")
        view.findViewById<TextView>(R.id.tvCardCVV).text = "CVV: $cvv"
        view.findViewById<TextView>(R.id.tvCardExpiry).text = "Expira: $expiry"


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
            saldoText.text = if (isSaldoVisible) "S/. $balance" else ""
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

        if (latitud == 0f || longitud == 0f) {
            showZonaSeguraDialog()
        }
    }
*/

    private fun showZonaSeguraDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Zona segura requerida")
            .setMessage("Para poder realizar transacciones, debes registrar una zona segura. Solo podrás operar dentro de un radio de 20 metros.")
            .setPositiveButton("Registrar ahora") { _, _ ->
                // TODO: Navega a la pantalla para registrar ubicación segura
                findNavController().navigate(R.id.action_transactionsFragment_to_safeZoneFragment)
            }
            // .setNegativeButton("Cancelar", null)
            .setCancelable(false)
            .show()
        }



}