/*package com.example.proyectofinaldam.ui.estado_de_consultas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.estado_de_consultas.adapter.EstadoDeConsultasAdapter
import com.example.proyectofinaldam.ui.estado_de_consultas.model.EstadoDeConsultasModel

class EstadoDeConsultasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_estadodeconsultas, container, false)
        //val view1: View =  inflater.inflate(R.layout.item_consulta_estado_clie, container, false)


        val rvActEstCliente: RecyclerView = view.findViewById(R.id.rvActEstCliente)
        rvActEstCliente.layoutManager = LinearLayoutManager(requireContext())
        rvActEstCliente.adapter = EstadoDeConsultasAdapter(ListPaciente())

        //val recyclerView: RecyclerView = findViewById(R.id.rvActEstCliente)
        //val items = listOf(
        //    EstadoDeConsultasModel(1, "Elemento 1"),
        //    EstadoDeConsultasModel(2, "Elemento 2"),
        //    EstadoDeConsultasModel(3, "Elemento 3")
        //)
        //val adapter = MiAdapter(items)
        //recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = adapter

        return view
        //return view1
    }

    private fun ListPaciente(): List<EstadoDeConsultasModel>{
        var lstPaciente: ArrayList<EstadoDeConsultasModel> = ArrayList()


        lstPaciente.add(
                EstadoDeConsultasModel(
                    "Sab"
                    ,"02"
                    ,"10:00 am"
                    ,"2024-10001"
                    ,"Melissa Luyo Cárdenas"
                    , "FINALIZADO"))
        lstPaciente.add(
            EstadoDeConsultasModel(
                "Dom"
                ,"03"
                ,"01:00 pm"
                ,"2024-10002"
                ,"Henry Paul Gomez Esquivez"
                , "EN PROCESO"))
        lstPaciente.add(
            EstadoDeConsultasModel(//3,R.drawable.susuki
                //,"Jorge Fossati"
                "Mie"
                ,"06"
                ,"10:00 am"
                ,"2024-10003…"
                ,"Joan Chipana Cárdenas"
                , "PROGRAMADO"))
        lstPaciente.add(
            EstadoDeConsultasModel(//1,R.drawable.mercedes
                //,"Edison Flores"
                "Mar"
                ,"05"
                ,"12:00 pm"
                ,"2024-10004"
                ,"Lucinda Diaz Melendez "
                , "PROGRAMADO"))
        lstPaciente.add(
            EstadoDeConsultasModel(//1,R.drawable.mercedes
                //,"Edison Flores"
                "Jue"
                ,"07"
                ,"04:00 pm"
                ,"2024-10005"
                ,"Melissa Luyo Cárdenas "
                , "PROGRAMADO"))
        return lstPaciente
    }
}*/

package com.example.proyectofinaldam.ui.estado_de_consultas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.estado_de_consultas.adapter.EstadoDeConsultasAdapter
import com.example.proyectofinaldam.ui.estado_de_consultas.model.EstadoDeConsultasModel
import com.google.firebase.database.*
//import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EstadoDeConsultasFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: EstadoDeConsultasAdapter
    private lateinit var lstPaciente: ArrayList<EstadoDeConsultasModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_estadodeconsultas, container, false)

        lstPaciente = ArrayList()
        adapter = EstadoDeConsultasAdapter(lstPaciente)

        val rvActEstCliente: RecyclerView = view.findViewById(R.id.rvActEstCliente)
        rvActEstCliente.layoutManager = LinearLayoutManager(requireContext())
        rvActEstCliente.adapter = adapter

        // Cargar datos simulados
        //cargarDatosSimulados() error!!

        // Inicializa Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("citas")

        // Llama a la función para obtener datos
        obtenerCitas()

        return view
    }

    private fun obtenerCitas() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lstPaciente.clear() // Limpia la lista antes de llenarla
                for (snapshot in dataSnapshot.children) {
                    val cita = snapshot.getValue(EstadoDeConsultasModel::class.java)
                    if (cita != null) {
                        cita.diaSem = getDayOfWeek(cita.fecha) // Calcula el día de la semana
                        lstPaciente.add(cita) // Agrega cada cita a la lista
                        Log.d("EstadoDeConsultasFragment", "Cita cargada: ${cita.paciente}, diaSem: ${cita.diaSem}")
                    }
                }
                adapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("EstadoDeConsultasFragment", "Error al cargar datos: ${databaseError.message}")
            }
        })
    }

    private fun cargarDatosSimulados() {
        lstPaciente.add(EstadoDeConsultasModel(
            descripcion = "Cita de ejemplo creada automáticamente.",
            direccion = "Av. Javier Prado 2445, SAN BORJA",
            doctor = "Dr. Daniel Delgado",
            edad = "25 años",
            especialidad = "Medicina General",
            estado = "programada",
            fecha = "01/01/2024", // Esta es la fecha de la cita
            genero = "Femenino",
            hora = "10:00 AM",
            id = "1",
            paciente = "Brigite Tarazona",
            pacienteId = "1",
            diaSem = getDayOfWeek("01/01/2024") // Calcula el día de la semana para la fecha proporcionada
        ))
        adapter.notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }

    // Método para obtener el día de la semana
    private fun getDayOfWeek(fecha: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date = sdf.parse(fecha) ?: return "Desconocido" // Manejo de null
        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> "Unknown"
        }
    }
}