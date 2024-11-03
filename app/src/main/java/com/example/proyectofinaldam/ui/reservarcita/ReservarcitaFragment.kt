package com.example.proyectofinaldam.ui.reservarcita

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservarcitaFragment : Fragment(R.layout.fragment_reservarcita) {

    private lateinit var calendarView: CalendarView
    private lateinit var btnSelectTime: View
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var rvDoctors: RecyclerView
    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        btnSelectTime = view.findViewById(R.id.btn_select_time)
        tvSelectedDateTime = view.findViewById(R.id.tv_selected_datetime)
        rvDoctors = view.findViewById(R.id.rv_doctors)

        // Handle date selection from CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)
            updateDateTimeDisplay()
        }

        // Handle time selection from TimePickerDialog
        btnSelectTime.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                updateDateTimeDisplay()
            }, hour, minute, true)

            timePicker.show()
        }

        // Sample list of doctors
        val doctors = listOf(
            Doctor("Dr. John Doe", "Cardiología", "101", R.drawable.ic_doctor_placeholder, true),
            Doctor("Dr. Jane Smith", "Neurología", "102", R.drawable.ic_doctor_placeholder, false),
            Doctor("Dr. Sarah Lee", "Pediatría", "103", R.drawable.ic_doctor_placeholder, true)
        )

        rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        rvDoctors.adapter = DoctorAdapter(doctors)
    }

    private fun updateDateTimeDisplay() {
        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
            tvSelectedDateTime.text = "Fecha y Hora Seleccionada: $selectedDate a las $selectedTime"
        } else if (selectedDate.isNotEmpty()) {
            tvSelectedDateTime.text = "Fecha Seleccionada: $selectedDate"
        }
    }
}