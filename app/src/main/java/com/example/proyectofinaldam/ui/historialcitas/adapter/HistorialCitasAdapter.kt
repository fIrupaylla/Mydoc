package com.example.proyectofinaldam.ui.historialcitas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.historialcitas.model.Cita

class HistorialCitasAdapter(private val listaCitas: List<Cita>,private val onItemClick: (Cita) -> Unit) :
    RecyclerView.Adapter<HistorialCitasAdapter.CitaViewHolder>() {

    class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        val tvDoctor: TextView = itemView.findViewById(R.id.tvDoctor)
        val tvEspecialidad: TextView = itemView.findViewById(R.id.tvEspecialidad)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = listaCitas[position]
        holder.tvFecha.text = "${cita.fecha}"
        holder.tvHora.text = "${cita.hora}"
        holder.tvDoctor.text = "${cita.doctor}"
        holder.tvEspecialidad.text = "${cita.especialidad}"
        holder.tvDescripcion.text = "${cita.descripcion}"

        // Configurar clic para redirigir al DetalleCitaFragment
        holder.itemView.setOnClickListener {
            onItemClick(cita)
        }
    }

    override fun getItemCount(): Int {
        return listaCitas.size
    }
}