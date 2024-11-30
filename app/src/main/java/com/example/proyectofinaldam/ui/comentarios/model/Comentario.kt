package com.example.proyectofinaldam.ui.comentarios.model

data class Comentario(
    val calificacion: Int = 0,
    val citaId: String = "",
    val comentarios: String = "",
    val recomendaciones: String = "",
    val timestamp: Long = 0L
)
