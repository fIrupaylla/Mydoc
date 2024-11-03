package com.example.proyectofinaldam


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView

class DetalleCitaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_cita)

        // Configurar el Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Habilitar el botón de "Atrás"

        toolbar.setNavigationOnClickListener {
            finish() // Regresar a la actividad anterior
        }

        val imgDoctorDetalle: ImageView = findViewById(R.id.imgDoctorDetalle)
        val tvNombreDetalle: TextView = findViewById(R.id.tvNombreDetalle)
        val tvFechaDetalle: TextView = findViewById(R.id.tvFechaDetalle)
        val tvHoraDetalle: TextView = findViewById(R.id.tvHoraDetalle)
        val tvSedeDetalle: TextView = findViewById(R.id.tvSedeDetalle)
        val tvNotasDetalle: TextView = findViewById(R.id.tvNotasDetalle)

        // Obtener datos del Intent
        val nombre = intent.getStringExtra("nombre")
        val fecha = intent.getStringExtra("fecha")
        val hora = intent.getStringExtra("hora")
        val sede = intent.getStringExtra("sede")
        val notas = intent.getStringExtra("notas") ?: "No hay notas adicionales."

        // Configurar los datos en la vista
        tvNombreDetalle.text = nombre
        tvFechaDetalle.text = "Fecha: $fecha"
        tvHoraDetalle.text = "Hora: $hora"
        tvSedeDetalle.text = "Sede: $sede"
        tvNotasDetalle.text = notas

        // Puedes cambiar la imagen si es necesario
        imgDoctorDetalle.setImageResource(R.drawable.ic_doctor)
    }
}


