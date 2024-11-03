package com.example.proyectofinaldam.ui.visualizarcita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyectofinaldam.databinding.FragmentVisualizarcitaBinding

class VisualizarcitaFragment : Fragment() {

    private var _binding: FragmentVisualizarcitaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val visualizarcitaViewModel =
            ViewModelProvider(this).get(VisualizarcitaViewModel::class.java)

        _binding = FragmentVisualizarcitaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textVisualizarcita
        visualizarcitaViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}