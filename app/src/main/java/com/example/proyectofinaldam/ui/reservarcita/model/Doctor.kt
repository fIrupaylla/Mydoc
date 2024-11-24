package com.example.proyectofinaldam.ui.reservarcita.model

data class Doctor(
    val name: String,
    val specialty: String,
    val officeNumber: String,
    val imageResId: Int,
    val availability: Map<String, Boolean>
)
