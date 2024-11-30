package com.example.proyectofinaldam.ui.notas_doctor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.proyectofinaldam.R
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 * Use the [NotasDoctorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotasDoctorFragment : Fragment() {

    private lateinit var consultaId: String

    private lateinit var tvPatientId: TextView
    private lateinit var tvPatientName: TextView
    private lateinit var tvPatientDetails: TextView
    private lateinit var tvLastConsultation: TextView

    private lateinit var etRecomendaciones: EditText
    private lateinit var etSeguimiento: EditText
    private lateinit var btnGuardar: Button
    private val db = FirebaseFirestore.getInstance()
    private lateinit var citaId: String // ID único de la consulta médica

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notas_doctor, container, false)

        // Referenciar vistas
        tvPatientId = view.findViewById(R.id.tvPatientId)
        tvPatientName = view.findViewById(R.id.tvPatientName)
        tvPatientDetails = view.findViewById(R.id.tvPatientDetails)
        tvLastConsultation = view.findViewById(R.id.tvLastConsultation)

        etRecomendaciones = view.findViewById(R.id.etRecomendaciones)
        etSeguimiento = view.findViewById(R.id.etSeguimiento)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        // Recuperar el ID de la consulta desde el Bundle
        citaId = arguments?.getString("citaId") ?: throw IllegalArgumentException("ID de consulta no proporcionado")

        cargarDatosDesdeFirebase()

        btnGuardar.setOnClickListener {
            guardarCambiosEnFirebase()
        }

        return view
    }

    private fun cargarDatosDesdeFirebase() {
        // Primero, obtener datos del paciente desde la colección "citas"
        db.collection("citas").document(citaId)
            .get()
            .addOnSuccessListener { citaDocument ->
                if (citaDocument.exists()) {
                    val pacienteId = citaDocument.getString("pacienteId") ?: ""
                    val pacienteNombre = citaDocument.getString("paciente") ?: ""
                    val edad = citaDocument.getString("edad") ?: ""
                    val genero = citaDocument.getString("genero") ?: ""
                    val ultimaConsulta = citaDocument.getString("fecha") ?: ""

                    // Mostrar datos del paciente en los campos correspondientes
                    tvPatientId.text = "H00-${pacienteId}"
                    tvPatientName.text = pacienteNombre
                    tvPatientDetails.text = "Edad: ${edad}   Género: ${genero}"
                    tvLastConsultation.text = "Última consulta: ${ultimaConsulta}"

                    // Luego, obtener las notas desde la colección "citas_notas"
                    db.collection("citas_notas").whereEqualTo("citaId", citaId)
                        .get()
                        .addOnSuccessListener { notasQuerySnapshot ->
                            if (!notasQuerySnapshot.isEmpty) {
                                // Tomamos el primer documento coincidente
                                val notasDocument = notasQuerySnapshot.documents[0]
                                val recomendaciones = notasDocument.getString("recomendaciones") ?: ""
                                val seguimiento = notasDocument.getString("seguimiento") ?: ""

                                // Mostrar recomendaciones y seguimiento en los campos editables
                                etRecomendaciones.setText(recomendaciones)
                                etSeguimiento.setText(seguimiento)
                            } else {
                                Toast.makeText(context, "Notas no encontradas para la consulta", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Error al cargar notas", Toast.LENGTH_SHORT).show()
                            exception.printStackTrace()
                        }
                } else {
                    Toast.makeText(context, "Cita no encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al cargar datos de la cita", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
    }

    private fun guardarCambiosEnFirebase() {
        val nuevasRecomendaciones = etRecomendaciones.text.toString().trim()
        val nuevoSeguimiento = etSeguimiento.text.toString().trim()

        // Validar los campos antes de actualizar
        if (nuevasRecomendaciones.isEmpty() || nuevoSeguimiento.isEmpty()) {
            Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Actualizar en la colección "citas_notas"
        db.collection("citas_notas").document(citaId)
            .update(
                mapOf(
                    "recomendaciones" to nuevasRecomendaciones,
                    "seguimiento" to nuevoSeguimiento
                )
            )
            .addOnSuccessListener {
                Toast.makeText(context, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error al guardar cambios: ${exception.message}", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
    }
}