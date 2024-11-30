package com.example.proyectofinaldam.ui.comentarios

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.historialcitas.model.Cita
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [ComentariosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComentariosFragment : Fragment() {

    private lateinit var citaId: String
    private lateinit var stars: List<ImageView>
    private var calificacion: Int = 0 // Calificación inicial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comentarios, container, false)

        // Recuperar el ID de la cita desde el Bundle
        citaId = arguments?.getString("citaId") ?: throw IllegalArgumentException("ID de cita no encontrado")

        // Inicializar estrellas
        stars = listOf(
            view.findViewById(R.id.star1),
            view.findViewById(R.id.star2),
            view.findViewById(R.id.star3),
            view.findViewById(R.id.star4),
            view.findViewById(R.id.star5)
        )
        configurarEstrellas()

        // Configurar botón "Enviar"
        val btnEnviar: Button = view.findViewById(R.id.btnEnviar)
        btnEnviar.setOnClickListener {
            enviarCalificacionAFirebase(view)
        }

        // Cargar los datos de Firebase con el ID
        cargarDatosDesdeFirebase(citaId)

        return view
    }

    private fun cargarDatosDesdeFirebase(citaId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("citas").document(citaId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val cita = document.toObject(Cita::class.java)
                    if (cita != null) {
                        // Actualiza la UI con los datos de la cita
                        mostrarInformacionCita(cita)
                    }
                } else {
                    Toast.makeText(context, "Cita no encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarInformacionCita(cita: Cita) {
        val tvDoctor = view?.findViewById<TextView>(R.id.tvDetalleDoctor)
        val tvFecha = view?.findViewById<TextView>(R.id.tvDetalleFecha)
        val tvHora = view?.findViewById<TextView>(R.id.tvDetalleHora)
        val tvDireccion = view?.findViewById<TextView>(R.id.tvDetalleDireccion)
        val tvEspecialidad = view?.findViewById<TextView>(R.id.tvDetalleEspecialidad)

        tvDoctor?.text = "Doctor: ${cita.doctor}"
        tvFecha?.text = "Fecha: ${cita.fecha}"
        tvHora?.text = "Hora: ${cita.hora}"
        tvDireccion?.text = "Dirección: ${cita.direccion}"
        tvEspecialidad?.text = "Especialidad: ${cita.especialidad}"
    }

    private fun configurarEstrellas() {
        for ((index, star) in stars.withIndex()) {
            star.setOnClickListener {
                calificacion = index + 1
                actualizarEstrellas(calificacion)
            }
        }
    }

    private fun actualizarEstrellas(calificacion: Int) {
        for (i in stars.indices) {
            stars[i].setImageResource(
                if (i < calificacion) R.drawable.ic_star_filled else R.drawable.ic_star_empty
            )
        }
    }

    private fun enviarCalificacionAFirebase(view: View) {
        val db = FirebaseFirestore.getInstance()

        val recomendaciones = view.findViewById<EditText>(R.id.etRecomendaciones).text.toString()
        val comentarios = view.findViewById<EditText>(R.id.etComentarios).text.toString()

        // Crear el objeto de datos para almacenar en la colección `comentarios`
        val data = mapOf(
            "citaId" to citaId,
            "calificacion" to calificacion,
            "recomendaciones" to recomendaciones,
            "comentarios" to comentarios,
            "timestamp" to System.currentTimeMillis() // Para ordenar por fecha si es necesario
        )

        // Agregar el documento a la colección `comentarios`
        db.collection("comentarios")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Comentario enviado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al enviar el comentario", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }
}