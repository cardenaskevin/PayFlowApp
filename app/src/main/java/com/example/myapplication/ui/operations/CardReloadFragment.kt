package com.example.myapplication.ui.operations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.google.android.material.appbar.MaterialToolbar

class CardReloadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_reload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val btnTransfer = view.findViewById<Button>(R.id.btnContinuar)
        val ctForm = view.findViewById<ScrollView>(R.id.svContenForm)
        val ctMessage = view.findViewById<LinearLayout>(R.id.ttSuccesView)
        val btnAccept = view.findViewById<Button>(R.id.btnHome)

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        btnTransfer.setOnClickListener {
            println("click aqui")
            ctForm.visibility = View.GONE
            ctMessage.visibility = View.VISIBLE
        }

        btnAccept.setOnClickListener {
            findNavController().navigateUp()
        }


    }

}