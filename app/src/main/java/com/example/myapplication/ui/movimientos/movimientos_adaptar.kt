package com.example.payflowapp.ui.movimientos

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.payflowapp.R
import com.example.payflowapp.data.model.Transacction

data class Transaction(val title: String, val amount: String)

class TransactionAdapter(
    private val items: List<Transacction>,
    private val currentUserId: String
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.transactionTitle)
        val amount: TextView = view.findViewById(R.id.transactionAmount)
        val date: TextView = view.findViewById(R.id.transactionDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]

        // Tipo de transacción
        val typeTitle = when (item.type) {
            1 -> "Recarga Tarjeta"
            2 -> "Pago Efectivo"
            3 -> "Transferencia por Teléfono"
            4 -> "Transferencia por Cuenta"
            else -> "Otro"
        }

        // Determinar signo y color
        val (sign, color) = when {
            item.userCreate == item.userReceive -> "+" to Color.parseColor("#388E3C") // verde
            item.userCreate == currentUserId -> "-" to Color.parseColor("#D32F2F") // rojo
            item.userReceive == currentUserId -> "+" to Color.parseColor("#388E3C") // verde
            else -> "" to Color.BLACK
        }

        holder.title.text = typeTitle
        holder.amount.text = "$sign ${item.amount}"
        holder.amount.setTextColor(color)

        holder.date.text = item.date // ya está en formato legible, puedes formatear si quieres
    }

    override fun getItemCount(): Int = items.size
}


/*class TransactionAdapter000(private val items: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.transactionTitle)
        val amount: TextView = view.findViewById(R.id.transactionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.amount.text = item.amount
    }

    override fun getItemCount() = items.size
}
*/