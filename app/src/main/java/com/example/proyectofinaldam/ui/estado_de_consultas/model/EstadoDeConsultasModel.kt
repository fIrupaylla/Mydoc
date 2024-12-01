package com.example.proyectofinaldam.ui.estado_de_consultas.model
/*
data class EstadoDeConsultasModel(
    val diaSem : String,
    val dia:String,
    val hora:String,
    val cita: String,
    val paciente: String,
    var estado: String
)*/

data class EstadoDeConsultasModel(
    val descripcion: String = "",
    val direccion: String = "",
    val doctor: String = "",
    val edad: String = "",
    val especialidad: String = "",
    var estado: String = "",
    val fecha: String = "",
    val genero: String = "",
    val hora: String = "",
    val id: String = "",
    val paciente: String = "",
    val pacienteId: String = "",
    var diaSem: String = ""
)
