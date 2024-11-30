package com.example.proyectofinaldam.ui.registrar_horarios

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.proyectofinaldam.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RegistrarHorariosFragment : Fragment() {

    private lateinit var tvDiasSeleccionados: TextView
    private lateinit var etHoraInicio: EditText
    private lateinit var etHoraFin: EditText
    private lateinit var btnGuardarDisponibilidad: Button
    private lateinit var calendarView: CalendarView

    private val db = FirebaseFirestore.getInstance()
    private val diasSeleccionados = mutableSetOf<String>() // Usar Set para evitar duplicados

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrar_horarios, container, false)

        tvDiasSeleccionados = view.findViewById(R.id.tvDiasSeleccionados)
        etHoraInicio = view.findViewById(R.id.etHoraInicio)
        etHoraFin = view.findViewById(R.id.etHoraFin)
        btnGuardarDisponibilidad = view.findViewById(R.id.btnGuardarDisponibilidad)
        calendarView = view.findViewById(R.id.calendarView)

        configurarCalendarView()
        configurarSelectorDeHorarios()
        configurarBotonGuardar()
        cargarDatosExistentes()

        return view
    }

    private fun configurarCalendarView() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            if (diasSeleccionados.contains(date)) {
                diasSeleccionados.remove(date) // Eliminar de la lista local
                eliminarDiaDeFirestore(date) // Eliminar de Firestore
                Toast.makeText(context, "Día deseleccionado: $date", Toast.LENGTH_SHORT).show()
            } else {
                diasSeleccionados.add(date) // Agregar el día seleccionado
                Toast.makeText(context, "Día seleccionado: $date", Toast.LENGTH_SHORT).show()
            }

            // Actualizar la lista de días seleccionados en el TextView
            mostrarDiasSeleccionados()
        }
    }

    private fun eliminarDiaDeFirestore(dia: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "usuario_desconocido"

        db.collection("disponibilidades")
            .whereEqualTo("usuarioId", userId)
            .whereEqualTo("dia", dia)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("disponibilidades").document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Día eliminado de Firestore: $dia", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error al eliminar día: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al buscar día en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDiasSeleccionados() {
        val fechasAmigables = diasSeleccionados
            .sorted()
            .joinToString("\n") { fecha ->
                val formatoAmigable = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                formatoAmigable.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(fecha)!!)
            }

        tvDiasSeleccionados.text = "Días seleccionados:\n$fechasAmigables"
    }

    private fun configurarSelectorDeHorarios() {
        etHoraInicio.setOnClickListener {
            mostrarTimePicker { hora, minuto ->
                val horaFormateada = String.format("%02d:%02d", hora, minuto)
                etHoraInicio.setText(horaFormateada)
            }
        }

        etHoraFin.setOnClickListener {
            mostrarTimePicker { hora, minuto ->
                val horaFormateada = String.format("%02d:%02d", hora, minuto)
                etHoraFin.setText(horaFormateada)
            }
        }
    }

    private fun mostrarTimePicker(onTimeSelected: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val horaActual = calendar.get(Calendar.HOUR_OF_DAY)
        val minutoActual = calendar.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            { _, horaSeleccionada, minutoSeleccionado ->
                onTimeSelected(horaSeleccionada, minutoSeleccionado)
            },
            horaActual,
            minutoActual,
            true // Cambia a false si deseas formato AM/PM
        )

        timePicker.show()
    }

    private fun configurarBotonGuardar() {
        btnGuardarDisponibilidad.setOnClickListener {
            val horaInicio = etHoraInicio.text.toString()
            val horaFin = etHoraFin.text.toString()

            if (diasSeleccionados.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
                Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            guardarDatosEnFirestore(horaInicio, horaFin)
        }
    }

    private fun guardarDatosEnFirestore(horaInicio: String, horaFin: String) {
        // Obtener el ID del usuario logueado
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "usuario_desconocido"

        val data = diasSeleccionados.map { dia ->
            mapOf(
                "dia" to dia,
                "horaInicio" to horaInicio,
                "horaFin" to horaFin,
                "usuarioId" to userId // Guardar el ID del usuario logueado
            )
        }

        val batch = db.batch()
        data.forEach { diaData ->
            val docRef = db.collection("disponibilidades").document()
            batch.set(docRef, diaData)
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(context, "Disponibilidad guardada correctamente", Toast.LENGTH_SHORT).show()
                cargarDatosExistentes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarDatosExistentes() {
        // Obtener el ID del usuario logueado
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "usuario_desconocido"

        db.collection("disponibilidades")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val primerDocumento = documents.documents[0] // Tomamos el primer registro como referencia
                    val horaInicio = primerDocumento.getString("horaInicio") ?: ""
                    val horaFin = primerDocumento.getString("horaFin") ?: ""

                    // Mostrar horarios en los campos correspondientes
                    etHoraInicio.setText(horaInicio)
                    etHoraFin.setText(horaFin)

                    // Agregar los días existentes a la lista
                    for (document in documents) {
                        val dia = document.getString("dia") ?: continue
                        diasSeleccionados.add(dia)
                    }

                    // Mostrar las fechas en el TextView
                    mostrarDiasSeleccionados()
                } else {
                    Toast.makeText(context, "No hay disponibilidad guardada.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limpiarFormulario() {
        diasSeleccionados.clear()
        tvDiasSeleccionados.text = "Días seleccionados:"
        etHoraInicio.text.clear()
        etHoraFin.text.clear()
    }
}