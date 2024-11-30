package com.example.proyectofinaldam.ui.citas_finalizadas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.citas_finalizadas.adapter.CitasFinalizadasAdapter
import com.example.proyectofinaldam.ui.historialcitas.adapter.HistorialCitasAdapter
import com.example.proyectofinaldam.ui.historialcitas.model.Cita
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [CitasFinalizadasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CitasFinalizadasFragment : Fragment() {
    private lateinit var recyclerCitasFinalizadas: RecyclerView
    private val citas = mutableListOf<Cita>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_citas_finalizadas, container, false)

        // Configura el RecyclerView
        recyclerCitasFinalizadas = view.findViewById(R.id.recyclerCitasFinalizadas)
        recyclerCitasFinalizadas.layoutManager = LinearLayoutManager(context)
        recyclerCitasFinalizadas.adapter = CitasFinalizadasAdapter(citas) { cita ->
            // Crear Bundle con el ID de la cita
            val bundle = Bundle().apply {
                putString("citaId", cita.id) // Pasa el ID de la cita
            }

            // Navegar al DetalleCitaFragment con el Bundle
            findNavController().navigate(
                R.id.action_citasFinalizadasFragment_to_notasDoctorFragment,
                bundle
            )
        }

        // Cargar los datos desde Firebase
        cargarCitas()

        return view
    }

    private fun cargarCitas() {
        db.collection("citas")
            .get()
            .addOnSuccessListener { result ->
                citas.clear() // Limpia la lista antes de agregar nuevos datos
                for (document in result) {
                    // Convierte cada documento en un objeto Cita
                    val cita = document.toObject(Cita::class.java)
                    citas.add(cita)
                }
                // Notifica al adaptador que los datos han cambiado
                recyclerCitasFinalizadas.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Manejar errores
                exception.printStackTrace()
            }
    }
}