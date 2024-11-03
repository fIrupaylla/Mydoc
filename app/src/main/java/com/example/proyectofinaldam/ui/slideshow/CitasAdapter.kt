package com.example.proyectofinaldam.ui.slideshow

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.DetalleCitaActivity
import com.example.proyectofinaldam.R



class CitasAdapter(private val citasList: List<Cita>) :
    RecyclerView.Adapter<CitasAdapter.CitaViewHolder>() {

    class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgDoctor: ImageView = itemView.findViewById(R.id.imgDoctor)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        val tvSede: TextView = itemView.findViewById(R.id.tvSede)
        val tvVerDetalle: TextView = itemView.findViewById(R.id.tvVerDetalle)
        val iconVerDetalle: ImageView = itemView.findViewById(R.id.iconVerDetalle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citasList[position]
        holder.tvNombre.text = cita.nombre
        holder.tvFecha.text = cita.fecha
        holder.tvHora.text = cita.hora
        holder.tvSede.text = cita.sede
        holder.imgDoctor.setImageResource(R.drawable.ic_doctor)

        // Manejar el clic en "Ver Detalle" y el icono
        val clickListener = View.OnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetalleCitaActivity::class.java)
            intent.putExtra("nombre", cita.nombre)
            intent.putExtra("fecha", cita.fecha)
            intent.putExtra("hora", cita.hora)
            intent.putExtra("sede", cita.sede)
            context.startActivity(intent)
        }

        holder.tvVerDetalle.setOnClickListener(clickListener)
        holder.iconVerDetalle.setOnClickListener(clickListener)
    }

    override fun getItemCount(): Int {
        return citasList.size
    }
}


