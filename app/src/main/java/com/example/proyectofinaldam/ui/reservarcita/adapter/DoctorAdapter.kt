package com.example.proyectofinaldam.ui.reservarcita.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.reservarcita.model.Doctor

class DoctorAdapter(
    private val doctors: List<Doctor>,
    private val onDoctorSelected: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorImage: ImageView = view.findViewById(R.id.iv_doctor_image)
        val doctorName: TextView = view.findViewById(R.id.tv_doctor_name)
        val doctorSpecialtyOffice: TextView = view.findViewById(R.id.tv_doctor_specialty_office)
        val reserveButton: Button = view.findViewById(R.id.btn_reserve)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        holder.doctorImage.setImageResource(doctor.imageResId)
        holder.doctorName.text = doctor.name
        holder.doctorSpecialtyOffice.text = "${doctor.specialty} - Office ${doctor.officeNumber}"

        // Configurar botón de reservar
        holder.reserveButton.setOnClickListener {
            onDoctorSelected(doctor)
        }
    }

    override fun getItemCount(): Int = doctors.size
}
