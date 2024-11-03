package com.example.proyectofinaldam.ui.slideshow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinaldam.databinding.FragmentSlideshowBinding


class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val view = binding.root

        // Configurar RecyclerView con múltiples elementos
        val citasList = listOf(
            Cita("Dr. Carrillo", "05/10", "10:00 a.m.", "La Molina"),
            Cita("Julienne Mark", "05/10", "11:00 a.m.", "San Isidro"),
            Cita("Dr. López", "06/10", "9:00 a.m.", "Miraflores")
        )

        val adapter = CitasAdapter(citasList)
        binding.rvCitas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCitas.adapter = adapter

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

