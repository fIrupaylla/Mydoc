package com.example.proyectofinaldam.ui.reservarcita

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R

data class Doctor(
    val name: String,
    val specialty: String,
    val officeNumber: String,
    val imageResId: Int, // Drawable resource ID for doctor's image
    val isAvailable: Boolean // True if doctor is available
)

class DoctorAdapter(private val doctors: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorImage: ImageView = view.findViewById(R.id.iv_doctor_image)
        val doctorName: TextView = view.findViewById(R.id.tv_doctor_name)
        val doctorSpecialtyOffice: TextView = view.findViewById(R.id.tv_doctor_specialty_office)
        val availabilityIcon: ImageView = view.findViewById(R.id.iv_availability)
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
        holder.availabilityIcon.setImageResource(
            if (doctor.isAvailable) R.drawable.ic_available else R.drawable.ic_unavailable
        )
    }

    override fun getItemCount(): Int = doctors.size
}
