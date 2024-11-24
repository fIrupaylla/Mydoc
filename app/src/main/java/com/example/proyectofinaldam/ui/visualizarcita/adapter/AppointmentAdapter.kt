package com.example.proyectofinaldam.ui.visualizarcita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.visualizarcita.model.Appointment

class AppointmentAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appointmentTime: TextView = view.findViewById(R.id.tv_appointment_time)
        val patientName: TextView = view.findViewById(R.id.tv_patient_name)
        val appointmentStatus: TextView = view.findViewById(R.id.tv_appointment_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.appointmentTime.text = "Hora: ${appointment.time}"
        holder.patientName.text = "Paciente: ${appointment.patientName}"
        holder.appointmentStatus.text = "Estado: ${appointment.status}"
    }

    override fun getItemCount(): Int = appointments.size
}
