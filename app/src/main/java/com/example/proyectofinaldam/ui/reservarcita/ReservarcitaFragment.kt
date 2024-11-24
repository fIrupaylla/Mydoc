package com.example.proyectofinaldam.ui.reservarcita

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.reservarcita.adapter.DoctorAdapter
import com.example.proyectofinaldam.ui.reservarcita.model.Doctor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class ReservarcitaFragment : Fragment(R.layout.fragment_reservarcita) {

    private lateinit var calendarView: CalendarView
    private lateinit var btnSelectTime: Button
    private lateinit var btnSearchDoctors: Button
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var rvDoctors: RecyclerView
    private var selectedDate: String = ""
    private var selectedTime: String = ""

    // Lista de doctores generada dinámicamente
    private var doctors: List<Doctor> = emptyList()

    private val allDoctors = List(20) { index ->
        Doctor(
            name = "Dr. Doctor $index",
            specialty = "Especialidad ${index + 1}",
            officeNumber = "10${index}",
            imageResId = R.drawable.ic_doctor_placeholder,
            availability = emptyMap() // Se generará dinámicamente
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        btnSelectTime = view.findViewById(R.id.btn_select_time)
        btnSearchDoctors = view.findViewById(R.id.btn_search_doctors)
        tvSelectedDateTime = view.findViewById(R.id.tv_selected_datetime)
        rvDoctors = view.findViewById(R.id.rv_doctors)

        // Configurar el RecyclerView
        rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        rvDoctors.adapter = DoctorAdapter(doctors) { doctor ->
            showConfirmationDialog(doctor)
        }

        // Manejar la selección de fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)

            // Verificar si es un día laboral (lunes a viernes)
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                Toast.makeText(requireContext(), "Seleccione un día laboral (lunes a viernes).", Toast.LENGTH_SHORT).show()
                selectedDate = ""
            }

            updateDateTimeDisplay()
        }

        // Manejar la selección de hora
        btnSelectTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                if (selectedHour !in 9..16 || (selectedHour == 16 && selectedMinute > 30)) {
                    Toast.makeText(requireContext(), "Seleccione un horario válido (9:00 AM - 5:00 PM).", Toast.LENGTH_SHORT).show()
                    selectedTime = ""
                }
                updateDateTimeDisplay()
            }, hour, minute, true)

            timePicker.show()
        }

        // Buscar doctores disponibles según la hora seleccionada
        btnSearchDoctors.setOnClickListener {
            if (selectedTime.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccione una fecha y hora válidas primero.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loadDoctorsForTime(selectedTime)
        }
    }

    private fun updateDateTimeDisplay() {
        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
            tvSelectedDateTime.text = "Fecha y Hora Seleccionada: $selectedDate a las $selectedTime"
        } else if (selectedDate.isNotEmpty()) {
            tvSelectedDateTime.text = "Fecha Seleccionada: $selectedDate"
        }
    }

    private fun loadDoctorsForTime(time: String) {
        // Generar disponibilidad aleatoria para los doctores
        doctors = allDoctors.map { doctor ->
            val isAvailable = Random.nextBoolean()
            doctor.copy(availability = mapOf(time to isAvailable))
        }

        // Actualizar el adaptador con la nueva lista de doctores
        rvDoctors.adapter = DoctorAdapter(doctors) { doctor ->
            showConfirmationDialog(doctor)
        }
    }

    private fun showConfirmationDialog(doctor: Doctor) {
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(requireContext(), "Seleccione una fecha y hora primero.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Cita")
            .setMessage("¿Deseas reservar una cita con ${doctor.name} el $selectedDate a las $selectedTime?")
            .setPositiveButton("Confirmar") { _, _ ->
                Toast.makeText(requireContext(), "Cita reservada con ${doctor.name}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
}
