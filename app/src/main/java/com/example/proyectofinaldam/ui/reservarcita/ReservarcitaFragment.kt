package com.example.proyectofinaldam.ui.reservarcita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinaldam.databinding.FragmentReservarcitaBinding
import com.example.proyectofinaldam.ui.home.ReservarcitaViewModel

class ReservarcitaFragment : Fragment() {

    private var _binding: FragmentReservarcitaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reservarcitaViewModel =
            ViewModelProvider(this).get(ReservarcitaViewModel::class.java)

        _binding = FragmentReservarcitaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textReservarcita
        reservarcitaViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}