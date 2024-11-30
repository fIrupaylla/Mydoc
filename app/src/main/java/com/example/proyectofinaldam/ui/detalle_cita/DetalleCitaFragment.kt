package com.example.proyectofinaldam.ui.detalle_cita

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.comentarios.adapter.ComentariosAdapter
import com.example.proyectofinaldam.ui.comentarios.model.Comentario
import com.example.proyectofinaldam.ui.historialcitas.model.Cita
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetalleCitaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetalleCitaFragment : Fragment() {

    private lateinit var tvDoctorName: TextView
    private lateinit var tvAppointmentTime: TextView
    private lateinit var tvSpecialty: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvEstado: TextView
    private lateinit var avisarmeButton: LinearLayout
    private lateinit var descargarButton: LinearLayout
    private lateinit var comentarioButton: LinearLayout
    private lateinit var cancelarButton: Button

    private val db = FirebaseFirestore.getInstance() // Referencia a Firebase Firestore
    private lateinit var comentariosAdapter: ComentariosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_cita, container, false)

        // Inicializar vistas
        tvDoctorName = view.findViewById(R.id.tvDoctorName)
        tvAppointmentTime = view.findViewById(R.id.tvAppointmentTime)
        tvSpecialty = view.findViewById(R.id.tvSpecialty)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvAddress = view.findViewById(R.id.tvAddress)
        tvEstado = view.findViewById(R.id.tvEstado)
        avisarmeButton = view.findViewById(R.id.avisarmeButton)
        descargarButton = view.findViewById(R.id.descargarButton)
        cancelarButton = view.findViewById(R.id.cancelarButton)
        comentarioButton = view.findViewById(R.id.comentarioButton)

        // Obtener el ID de la cita desde el Bundle
        val citaId = arguments?.getString("citaId") ?: ""

        // Cargar los datos desde Firebase
        cargarDatosDesdeFirebase(citaId)

        // Configurar el RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerComentarios)
        recyclerView.layoutManager = LinearLayoutManager(context)
        comentariosAdapter = ComentariosAdapter(emptyList())
        recyclerView.adapter = comentariosAdapter

        // Cargar comentarios desde Firebase
        cargarComentarios(citaId)

        return view
    }

    private fun cargarDatosDesdeFirebase(citaId: String) {
        if (citaId.isNotEmpty()) {
            db.collection("citas").document(citaId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cita = document.toObject(Cita::class.java) // Convierte el documento a un objeto `Cita`
                        if (cita != null) {
                            // Actualiza las vistas con los datos de la cita
                            tvDoctorName.text = cita.doctor
                            tvAppointmentTime.text = "${cita.hora} - ${cita.fecha}"
                            tvSpecialty.text = cita.especialidad
                            tvDescription.text = cita.descripcion
                            tvAddress.text = cita.direccion
                            tvEstado.text = cita.estado.capitalize()

                            // Configurar los botones según el estado de la cita
                            if (cita.estado == "cancelada") {
                                // Cambiar el color y deshabilitar los botones
                                tvEstado.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGray))

                                cancelarButton.apply {
                                    text = "Cita Cancelada"
                                    isEnabled = false
                                    backgroundTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), R.color.colorGrayLight)
                                    )
                                }

                                avisarmeButton.apply {
                                    isEnabled = false
                                    backgroundTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), R.color.colorGrayLight)
                                    )
                                }

                                descargarButton.apply {
                                    isEnabled = false
                                    backgroundTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), R.color.colorGrayLight)
                                    )
                                }

                                comentarioButton.apply {
                                    isEnabled = false
                                    backgroundTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), R.color.colorGrayLight)
                                    )
                                }

                                Toast.makeText(context, "Esta cita ha sido cancelada", Toast.LENGTH_SHORT).show()
                            } else {
                                // Configurar botones normalmente si la cita no está cancelada
                                configurarBotonAvisarme(cita.fecha, cita.hora)
                                configurarBotonDescargar(cita)
                                configurarBotonCancelar(citaId)
                                configurarBotonComentario(cita)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Cita no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    Toast.makeText(context, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "ID de cita inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarBotonDescargar(cita: Cita) {

        descargarButton.setOnClickListener {
            try {
                // 1. Generar el archivo PDF
                val pdfFile = generarPDF(cita)

                if (pdfFile != null) {
                    // 2. Compartir el archivo PDF
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.provider",
                            pdfFile
                        ))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(intent, "Compartir PDF de la cita"))
                } else {
                    Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al descargar o compartir el PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBotonAvisarme(fecha: String, hora: String) {
        avisarmeButton.setOnClickListener {
            try {
                val date = convertirFechaHoraFlexible(fecha, hora)

                if (date != null) {
                    // Crear el Intent para agregar al calendario
                    val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, "Cita médica")
                        putExtra(CalendarContract.Events.DESCRIPTION, "Consulta con el doctor en MyDoc")
                        putExtra(CalendarContract.Events.EVENT_LOCATION, "MyDoc")
                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.time)
                        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.time + (60 * 60 * 1000)) // Duración de 1 hora
                        putExtra(CalendarContract.Events.ALL_DAY, false)
                    }

                    // Iniciar el Intent
                    val chooser = Intent.createChooser(calendarIntent, "Seleccione una aplicación de calendario")
                    startActivity(chooser)
                } else {
                    Toast.makeText(context, "Error en la fecha y hora", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "No se pudo abrir el calendario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarBotonCancelar(citaId: String) {

        cancelarButton.setOnClickListener {
            // Mostrar el diálogo de confirmación
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmar Cancelación")
            builder.setMessage("¿Está seguro de que desea cancelar esta cita?")

            // Botón "Sí"
            builder.setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                cancelarCitaEnFirebase(citaId) // Llamar a la función para cancelar la cita
            }

            // Botón "No"
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun cancelarCitaEnFirebase(citaId: String) {
        if (citaId.isNotEmpty()) {
            val citaRef = db.collection("citas").document(citaId)

            // Actualizar el estado de la cita a "cancelada"
            citaRef.update("estado", "cancelada")
                .addOnSuccessListener {
                    Toast.makeText(context, "Cita cancelada exitosamente", Toast.LENGTH_SHORT).show()

                    // Recargar los datos del fragmento
                    cargarDatosDesdeFirebase(citaId)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    Toast.makeText(context, "Error al cancelar la cita", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "ID de cita inválido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generarPDF(cita: Cita): File? {
        return try {
            // Crear un archivo temporal para el PDF
            val pdfFile = File(requireContext().cacheDir, "Cita_${cita.id}.pdf")

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = document.startPage(pageInfo)

            val canvas = page.canvas
            val paint = Paint()

            // Escribir los datos en el PDF
            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("Detalles de la Cita", 80f, 50f, paint)

            paint.textSize = 14f
            paint.isFakeBoldText = false
            canvas.drawText("Doctor: ${cita.doctor}", 10f, 100f, paint)
            canvas.drawText("Fecha: ${cita.fecha}", 10f, 130f, paint)
            canvas.drawText("Hora: ${cita.hora}", 10f, 160f, paint)
            canvas.drawText("Clínica: MyDoc", 10f, 190f, paint)
            canvas.drawText("Descripción: ${cita.descripcion}", 10f, 220f, paint)

            document.finishPage(page)

            // Guardar el documento
            pdfFile.outputStream().use {
                document.writeTo(it)
            }
            document.close()

            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun convertirFechaHoraFlexible(fecha: String, hora: String): Date? {
        return try {
            // Lista de formatos válidos para la fecha
            val formatosFecha = listOf("dd/MM/yyyy", "yyyy-MM-dd")
            // Lista de formatos válidos para la hora
            val formatosHora = listOf("hh:mm a", "HH:mm")

            var fechaConvertida: Date? = null
            var horaConvertida: Date? = null

            // Intentar convertir la fecha con los formatos disponibles
            for (formato in formatosFecha) {
                try {
                    val formatter = SimpleDateFormat(formato, Locale.getDefault())
                    fechaConvertida = formatter.parse(fecha)
                    if (fechaConvertida != null) break
                } catch (e: Exception) {
                    // Ignorar errores y probar el siguiente formato
                }
            }

            // Intentar convertir la hora con los formatos disponibles
            for (formato in formatosHora) {
                try {
                    val formatter = SimpleDateFormat(formato, Locale.getDefault())
                    horaConvertida = formatter.parse(hora)
                    if (horaConvertida != null) break
                } catch (e: Exception) {
                    // Ignorar errores y probar el siguiente formato
                }
            }

            // Si no se pudieron convertir ambos, retornar null
            if (fechaConvertida == null || horaConvertida == null) return null

            // Combinar la fecha y la hora
            val calendar = Calendar.getInstance()
            calendar.time = fechaConvertida
            val horaCalendar = Calendar.getInstance()
            horaCalendar.time = horaConvertida

            calendar.set(Calendar.HOUR_OF_DAY, horaCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, horaCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)

            calendar.time
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun configurarBotonComentario(cita: Cita) {
        val comentarioButton: LinearLayout = requireView().findViewById(R.id.comentarioButton)

        comentarioButton.setOnClickListener {
            // Crear un Bundle con el ID de la cita
            val bundle = Bundle().apply {
                putString("citaId", cita.id) // Asegúrate de que `cita.id` contenga el identificador único
            }

            // Navegar al ComentariosFragment con el ID de la cita
            findNavController().navigate(
                R.id.action_detalleCitaFragment_to_comentariosFragment,
                bundle
            )
        }
    }

    private fun cargarComentarios(citaId: String) {
        db.collection("comentarios").whereEqualTo("citaId", citaId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val comentarios = querySnapshot.documents.mapNotNull { it.toObject(Comentario::class.java) }
                Log.d("ComentariosFragment", "Comentarios obtenidos: ${comentarios.size}")
                comentariosAdapter.setComentarios(comentarios)
            }
            .addOnFailureListener { exception ->
                Log.e("ComentariosFragment", "Error al cargar comentarios", exception)
                Toast.makeText(context, "Error al cargar comentarios.", Toast.LENGTH_SHORT).show()
            }
    }
}