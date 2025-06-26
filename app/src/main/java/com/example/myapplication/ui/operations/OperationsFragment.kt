package com.example.payflowapp.ui.operations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.payflowapp.R
import com.google.android.material.appbar.MaterialToolbar

class OperationsFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_operations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)


        val cvPhoneTransfer = view.findViewById<CardView>(R.id.cvPhoneTransfer)
        val cvCardReload = view.findViewById<CardView>(R.id.cvCardReload)
        val cvCashReload = view.findViewById<CardView>(R.id.cvCashReload)
        val cvAccountTransfer = view.findViewById<CardView>(R.id.cvAccountTransfer)


        cvAccountTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_operationsFragment_to_accountTransferFragment)
        }

        cvCashReload.setOnClickListener {
            findNavController().navigate(R.id.action_operationsFragment_to_cashReloadFragment)
        }

        cvCardReload.setOnClickListener {
            findNavController().navigate(R.id.action_operationsFragment_to_cardReloadFragment)
        }

        cvPhoneTransfer.setOnClickListener {
            findNavController().navigate(R.id.action_operationsFragment_to_phoneTransferFragment)
        }


    }
}