/*package com.example.proyectofinaldam.ui.estado_de_consultas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.estado_de_consultas.model.EstadoDeConsultasModel

class EstadoDeConsultasAdapter(private var lstPaciente: List<EstadoDeConsultasModel>)
    : RecyclerView.Adapter<EstadoDeConsultasAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        //val spEstado:Spinner = itemView.findViewById(R.id.spEstado)
        val tvDiaSem = itemView.findViewById<TextView>(R.id.tvDiaSem)
        val tvDia = itemView.findViewById<TextView>(R.id.tvDia)
        val tvHora = itemView.findViewById<TextView>(R.id.tvHora)
        val tvCita = itemView.findViewById<TextView>(R.id.tvCita)
        val tvPaciente = itemView.findViewById<TextView>(R.id.tvPaciente)
        //val tvModelo = itemView.findViewById<TextView>(R.id.txtModelo)
        //val tvPlaca = itemView.findViewById<TextView>(R.id.txtPlaca)

        val spEstado: Spinner = itemView.findViewById(R.id.spEstado)

        private val spinner: Spinner = itemView.findViewById(R.id.spEstado)
        //private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)

        fun bind(item: EstadoDeConsultasModel) {

            tvDiaSem.text = item.diaSem
            tvDia.text = item.dia
            tvHora.text = item.hora
            tvCita.text = item.cita
            tvPaciente.text=item.paciente
            // Carga el array desde strings.xml para el Spinner
            val opcionesArray = itemView.context.resources.getStringArray(R.array.estado_array)
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, opcionesArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            // Configura el Spinner para seleccionar el valor almacenado en el Modelo
            val posicionSeleccionada = opcionesArray.indexOf(item.estado)
            if (posicionSeleccionada >= 0) {
                spinner.setSelection(posicionSeleccionada)
            }

            // Escucha los cambios de selección y los guarda en el Modelo
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    item.estado = opcionesArray[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No hacer nada si no hay selección
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_consulta_estado_clie, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lstPaciente.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ////val itemPaciente = lstPaciente[position]
        //holder.ivCarro.setImageResource(itemCliente.image)
        //holder.tvDiaSem.text = itemPaciente.diaSem
        //holder.tvDia.text = itemPaciente.dia
        //holder.tvHora.text = itemPaciente.hora
        //holder.tvCita.text = itemPaciente.cita
        //holder.tvPaciente.text = itemPaciente.paciente
        holder.bind(lstPaciente[position])
        //holder.tvModelo.text = itemCliente.modelo
        //holder.tvPlaca.text = itemCliente.placa
    }
}*/

package com.example.proyectofinaldam.ui.estado_de_consultas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.ui.estado_de_consultas.model.EstadoDeConsultasModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EstadoDeConsultasAdapter(private var lstPaciente: List<EstadoDeConsultasModel>) :
    RecyclerView.Adapter<EstadoDeConsultasAdapter.ViewHolder>() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("citas")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPaciente: TextView = itemView.findViewById(R.id.tvPaciente)
        private val tvDia: TextView = itemView.findViewById(R.id.tvDia)
        private val tvDiaSem: TextView = itemView.findViewById(R.id.tvDiaSem)
        private val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvCita: TextView = itemView.findViewById(R.id.tvCita)
        private val spinner: Spinner = itemView.findViewById(R.id.spEstado)

        fun bind(item: EstadoDeConsultasModel) {
            tvPaciente.text = item.paciente
            tvDiaSem.text = item.diaSem
            tvHora.text = item.hora
            tvCita.text = item.descripcion

            // Extraer solo el día de la fecha
            val día = item.fecha.split("/")[0] // Suponiendo que item.fecha está en "dd/MM/yyyy"
            tvFecha.text = día // Asignar solo el día a tvFecha

            // Carga el array desde strings.xml para el Spinner
            val opcionesArray = itemView.context.resources.getStringArray(R.array.estado_array)
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, opcionesArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            // Configura el Spinner para seleccionar el valor almacenado en el Modelo
            val posicionSeleccionada = opcionesArray.indexOf(item.estado)
            if (posicionSeleccionada >= 0) {
                spinner.setSelection(posicionSeleccionada)
            }

            // Escucha los cambios de selección y actualiza el estado en Firebase
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val nuevoEstado = opcionesArray[position]
                    if (item.estado != nuevoEstado) {
                        item.estado = nuevoEstado
                        actualizarEstado(item.id, nuevoEstado)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No hacer nada si no hay selección
                }
            }
        }

        private fun actualizarEstado(citaId: String, nuevoEstado: String) {
            val citaRef = database.child(citaId).child("estado")
            citaRef.setValue(nuevoEstado)
                .addOnSuccessListener {
                    // Actualización exitosa
                }
                .addOnFailureListener {
                    // Error al actualizar
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_consulta_estado_clie, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lstPaciente.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lstPaciente[position])
    }
}
