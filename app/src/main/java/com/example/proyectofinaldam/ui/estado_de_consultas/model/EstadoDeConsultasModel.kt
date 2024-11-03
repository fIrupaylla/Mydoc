package com.example.proyectofinaldam.ui.estado_de_consultas.model

data class EstadoDeConsultasModel(
    val diaSem : String,
    val dia:String,
    val hora:String,
    val cita: String,
    val paciente: String,
    var estado: String
)
