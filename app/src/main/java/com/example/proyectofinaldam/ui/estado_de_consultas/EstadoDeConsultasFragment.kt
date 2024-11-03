package com.example.proyectofinaldam.ui.estado_de_consultas

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
        val view1: View =  inflater.inflate(R.layout.item_consulta_estado_clie, container, false)

        val spEstado: Spinner = view1.findViewById(R.id.spEstado)
        ArrayAdapter
            .createFromResource(requireContext(),R.array.estado_array,
                android.R.layout.simple_spinner_item).also {
                    adapter ->
                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item)
                spEstado.adapter = adapter
            }
        //spCountry on ItemSelectedListener
        var spEstadoValue = ""

        spEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                spEstadoValue = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spEstado.setSelection(0, false)


        val rvActEstCliente: RecyclerView = view.findViewById(R.id.rvActEstCliente)
        rvActEstCliente.layoutManager = LinearLayoutManager(requireContext())
        rvActEstCliente.adapter = EstadoDeConsultasAdapter(ListPaciente())

        return view
    }

    private fun ListPaciente(): List<EstadoDeConsultasModel>{
        var lstSong: ArrayList<EstadoDeConsultasModel> = ArrayList()


        lstSong.add(
                EstadoDeConsultasModel(
                    "Sab"
                    ,"02"
                    ,"10:00 am"
                    ,"2024-10001"
                    ,"Melissa Luyo Cárdenas"
                    , "FINALIZADO"))
        lstSong.add(
            EstadoDeConsultasModel(
                "Dom"
                ,"03"
                ,"01:00 pm"
                ,"2024-10002"
                ,"Henry Paul Gomez Esquivez"
                , "EN PROCESO"))
        lstSong.add(
            EstadoDeConsultasModel(//3,R.drawable.susuki
                //,"Jorge Fossati"
                "Mie"
                ,"06"
                ,"10:00 am"
                ,"2024-10003…"
                ,"Joan Chipana Cárdenas"
                , "PROGRAMADO"))
        lstSong.add(
            EstadoDeConsultasModel(//1,R.drawable.mercedes
                //,"Edison Flores"
                "Mar"
                ,"05"
                ,"12:00 pm"
                ,"2024-10004"
                ,"Lucinda Diaz Melendez "
                , "PROGRAMADO"))
        lstSong.add(
            EstadoDeConsultasModel(//1,R.drawable.mercedes
                //,"Edison Flores"
                "Jue"
                ,"07"
                ,"04:00 pm"
                ,"2024-10005"
                ,"Melissa Luyo Cárdenas "
                , "PROGRAMADO"))
        return lstSong
    }
}