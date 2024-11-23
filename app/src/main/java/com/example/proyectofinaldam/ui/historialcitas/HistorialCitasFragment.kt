package com.example.proyectofinaldam.ui.historialcitas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.historialcitas.adapter.HistorialCitasAdapter
import com.example.proyectofinaldam.ui.historialcitas.model.Cita
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistorialCitasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialCitasFragment : Fragment() {
    private lateinit var recyclerHistorialCitas: RecyclerView
    private val citas = mutableListOf<Cita>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_historial_citas, container, false)

        // Configura el RecyclerView
        recyclerHistorialCitas = view.findViewById(R.id.recyclerHistorialCitas)
        recyclerHistorialCitas.layoutManager = LinearLayoutManager(context)
        recyclerHistorialCitas.adapter = HistorialCitasAdapter(citas) { cita ->
            // Crear Bundle con el ID de la cita
            val bundle = Bundle().apply {
                putString("citaId", cita.id) // Pasa el ID de la cita
            }

            // Navegar al DetalleCitaFragment con el Bundle
            findNavController().navigate(
                R.id.action_historialCitasFragment_to_detalleCitaFragment,
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
                if (result.isEmpty) {
                    // Si la colección está vacía, crea un documento predeterminado
                    crearDocumentoPredeterminado()
                } else {
                    citas.clear() // Limpia la lista antes de agregar nuevos datos
                    for (document in result) {
                        // Convierte cada documento en un objeto Cita
                        val cita = document.toObject(Cita::class.java)
                        citas.add(cita)
                    }
                    // Notifica al adaptador que los datos han cambiado
                    recyclerHistorialCitas.adapter?.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores
                exception.printStackTrace()
            }
    }

    private fun crearDocumentoPredeterminado() {
        val citaPredeterminada = Cita(
            id = "1",
            fecha = "01/01/2024",
            hora = "10:00 AM",
            doctor = "Dr. Predeterminado",
            especialidad = "General",
            descripcion = "Cita de ejemplo creada automáticamente."
        )

        db.collection("citas").document(citaPredeterminada.id)
            .set(citaPredeterminada)
            .addOnSuccessListener {
                // Documento creado con éxito, vuelve a cargar la lista
                cargarCitas()
            }
            .addOnFailureListener { exception ->
                // Manejar errores al crear el documento
                exception.printStackTrace()
            }
    }
}