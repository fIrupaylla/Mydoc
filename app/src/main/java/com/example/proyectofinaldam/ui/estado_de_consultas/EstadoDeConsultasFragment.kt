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
}