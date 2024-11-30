package com.example.proyectofinaldam.ui.comentarios.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.comentarios.model.Comentario

class ComentariosAdapter(private var comentarios: List<Comentario>) :
    RecyclerView.Adapter<ComentariosAdapter.ComentarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarios[position]
        holder.bind(comentario)
    }

    override fun getItemCount(): Int = comentarios.size

    fun setComentarios(comentarios: List<Comentario>) {
        this.comentarios = comentarios
        notifyDataSetChanged()
    }

    inner class ComentarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsuario: TextView = itemView.findViewById(R.id.tvCommentUserName)
        private val tvComentario: TextView = itemView.findViewById(R.id.tvCommentContent)

        fun bind(comentario: Comentario) {
            tvUsuario.text = "Calificación dada: ${comentario.calificacion} estrellas"
            tvComentario.text = "Comentario: ${comentario.comentarios}"
        }
    }
}