package com.example.proyectofinaldam.ui.estado_de_consultas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.estado_de_consultas.model.EstadoDeConsultasModel

class EstadoDeConsultasAdapter(private var lstPaciente: List<EstadoDeConsultasModel>)
    : RecyclerView.Adapter<EstadoDeConsultasAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        //val ivCarro = itemView.findViewById<ImageView>(R.id.imgCarro)
        val tvDiaSem = itemView.findViewById<TextView>(R.id.tvDiaSem)
        val tvDia = itemView.findViewById<TextView>(R.id.tvDia)
        val tvHora = itemView.findViewById<TextView>(R.id.tvHora)
        val tvCita = itemView.findViewById<TextView>(R.id.tvCita)
        val tvPaciente = itemView.findViewById<TextView>(R.id.tvPaciente)
        //val tvModelo = itemView.findViewById<TextView>(R.id.txtModelo)
        //val tvPlaca = itemView.findViewById<TextView>(R.id.txtPlaca)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_consulta_estado_clie, parent, false))
    }

    override fun getItemCount(): Int {
        return lstPaciente.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPaciente = lstPaciente[position]
        //holder.ivCarro.setImageResource(itemCliente.image)
        holder.tvDiaSem.text = itemPaciente.diaSem
        holder.tvDia.text = itemPaciente.dia
        holder.tvHora.text = itemPaciente.hora
        holder.tvCita.text = itemPaciente.cita
        holder.tvPaciente.text = itemPaciente.paciente
        //holder.tvModelo.text = itemCliente.modelo
        //holder.tvPlaca.text = itemCliente.placa
    }
}
