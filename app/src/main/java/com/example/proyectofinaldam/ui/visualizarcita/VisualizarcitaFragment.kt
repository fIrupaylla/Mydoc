package com.example.proyectofinaldam.ui.visualizarcita

import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.visualizarcita.adapter.AppointmentAdapter
import com.example.proyectofinaldam.ui.visualizarcita.model.Appointment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VisualizarcitaFragment : Fragment(R.layout.fragment_visualizarcita) {

    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvAppointments: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendarView)
        tvSelectedDate = view.findViewById(R.id.tv_selected_date)
        rvAppointments = view.findViewById(R.id.rv_appointments)

        rvAppointments.layoutManager = LinearLayoutManager(requireContext())

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
            tvSelectedDate.text = dateFormat.format(calendar.time)
            loadAppointmentsForDate(dateFormat.format(calendar.time))
        }

        val today = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
        tvSelectedDate.text = today
        loadAppointmentsForDate(today)
    }

    private fun loadAppointmentsForDate(date: String) {
        val appointments = listOf(
            Appointment("9:00 AM", "Luis Gomez Esquivez", "Programado"),
            Appointment("10:00 AM", "Andersson Farfan Carbajal", "Programado"),
            Appointment("11:00 AM", "Sandra Rios Gordillo", "Programado"),
            Appointment("12:00 PM", "Junior Silva Calderon", "Programado")
        )

        rvAppointments.adapter = AppointmentAdapter(appointments)
    }
}
