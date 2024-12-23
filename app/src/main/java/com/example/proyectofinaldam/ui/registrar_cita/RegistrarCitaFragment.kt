package com.example.proyectofinaldam.ui.registrar_cita

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.proyectofinaldam.PrincipalActivity
import com.example.proyectofinaldam.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.example.proyectofinaldam.datastore.UserDataStore
class RegistrarCitaFragment : Fragment() {

    private lateinit var etFechaCita: EditText
    private lateinit var etHoraCita: EditText
    private lateinit var actvDoctor: AutoCompleteTextView
    private lateinit var btnRegistrarCita: Button

    private val db = FirebaseFirestore.getInstance()
    private val listaDoctores = mutableListOf<String>()
    private var doctorSeleccionado: String? = null
    private var pacienteId: String? = null
    private var pacienteNombre: String? = null
    private var pacienteEdad: String? = null
    private var pacienteGenero: String? = null
    private var direccionCita: String = "Av. La Encalada 452" // Dirección estática por ahora

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registrar_cita, container, false)

        etFechaCita = view.findViewById(R.id.et_fecha_cita)
        etHoraCita = view.findViewById(R.id.et_hora_cita)
        actvDoctor = view.findViewById(R.id.actv_doctor)
        btnRegistrarCita = view.findViewById(R.id.btn_registrar_cita)

        cargarDatosPaciente()
        configurarSelectorFecha()
        configurarSelectorHora()
        cargarDoctoresDesdeFirebase()
        configurarBotonRegistrar()

        actvDoctor.setOnClickListener {
            actvDoctor.showDropDown()
        }

        return view
    }

    private fun cargarDatosPaciente() {
        // Suponiendo que el paciente está autenticado y almacenado en DataStore
        val currentUser = UserDataStore.getUserData(requireContext())
        pacienteId = currentUser["dni"] ?: ""
        pacienteNombre = "${currentUser["nombre"]} ${currentUser["apellido"]}"
        pacienteEdad = currentUser["edad"] ?: "N/A"
        pacienteGenero = currentUser["genero"] ?: "N/A"
    }

    private fun configurarSelectorFecha() {
        etFechaCita.setOnClickListener {
            val calendario = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val fechaSeleccionada = Calendar.getInstance()
                    fechaSeleccionada.set(year, month, dayOfMonth)
                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    etFechaCita.setText(formatoFecha.format(fechaSeleccionada.time))
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun configurarSelectorHora() {
        etHoraCita.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val hora = String.format("%02d:%02d", hourOfDay, minute)
                    etHoraCita.setText("$hora ${if (hourOfDay < 12) "AM" else "PM"}")
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            )
            timePicker.show()
        }
    }

    private fun cargarDoctoresDesdeFirebase() {
        db.collection("doctores")
            .get()
            .addOnSuccessListener { documents ->
                listaDoctores.clear() // Limpia la lista antes de llenarla
                for (document in documents) {
                    val nombreCompleto = "${document.getString("Nombres")} ${document.getString("Apellidos")}".trim()
                    listaDoctores.add(nombreCompleto)
                }

                if (listaDoctores.isNotEmpty()) {
                    // Configurar adaptador
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        listaDoctores
                    )
                    actvDoctor.setAdapter(adapter)

                    // Asegúrate de que el AutoCompleteTextView responde al clic
                    actvDoctor.setOnClickListener {
                        actvDoctor.showDropDown()
                    }

                    // Configurar evento de selección
                    actvDoctor.setOnItemClickListener { _, _, position, _ ->
                        doctorSeleccionado = listaDoctores[position]
                        Toast.makeText(requireContext(), "Seleccionaste: $doctorSeleccionado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "No se encontraron doctores", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al cargar doctores: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun configurarBotonRegistrar() {
        btnRegistrarCita.setOnClickListener {
            val fechaCita = etFechaCita.text.toString()
            val horaCita = etHoraCita.text.toString()

            if (fechaCita.isEmpty() || horaCita.isEmpty() || doctorSeleccionado == null) {
                Toast.makeText(requireContext(), "Debe completar todos los campos.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val cita = mapOf(
                "id" to UUID.randomUUID().toString(),
                "description" to "Cita Programada",
                "direction" to direccionCita,
                "doctor" to doctorSeleccionado,
                "pacienteId" to pacienteId,
                "paciente" to pacienteNombre,
                "edad" to pacienteEdad,
                "genero" to pacienteGenero,
                "especialidad" to "Medicina General",
                "estado" to "programada",
                "fecha" to fechaCita,
                "hora" to horaCita
            )

            db.collection("citas")
                .add(cita)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Cita registrada exitosamente.", Toast.LENGTH_SHORT).show()
                    mostrarNotificacionCitaRegistrada(fechaCita,horaCita)
                    limpiarFormulario()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al registrar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun limpiarFormulario() {
        etFechaCita.text.clear()
        etHoraCita.text.clear()
        actvDoctor.text.clear()
        doctorSeleccionado = null
    }

    private fun mostrarNotificacionCitaRegistrada(fechaCita: String, horaCita: String) {
        // Verifica el permiso para Android 13 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
            return
        }

        val channelId = "cita_registrada_channel"
        val channelName = "Registro de Citas"
        val notificationId = 1

        val intent = Intent(requireContext(), PrincipalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificación de cita registrada con éxito"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_calendar_rosa) // Asegúrate de usar un ícono existente
            .setContentTitle("Cita registrada")
            .setContentText("Tu cita para $fechaCita a las $horaCita ha sido registrada con éxito.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }



}
