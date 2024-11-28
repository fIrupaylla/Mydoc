package com.example.proyectofinaldam.ui.nuevasede

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.proyectofinaldam.R
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarSedeFragment : Fragment(R.layout.fragment_registrar_sede) {

    private val db = FirebaseFirestore.getInstance() // Inicializar Firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_registrar_sede, container, false)

        val btnGuardar = rootView.findViewById<Button>(R.id.btnGuardarSede)
        btnGuardar.setOnClickListener {
            val nombre = rootView.findViewById<EditText>(R.id.etNombreSede).text.toString()
            val direccion = rootView.findViewById<EditText>(R.id.etDireccionSede).text.toString()

            // Validar los campos
            if (nombre.isEmpty() || direccion.isEmpty()) {
                Toast.makeText(context, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {
                saveSedeToFirestore(nombre, direccion)
            }
        }

        return rootView
    }

    private fun saveSedeToFirestore(nombre: String, direccion: String) {
        val sede = hashMapOf(
            "nombre" to nombre,
            "direccion" to direccion
        )

        // Guardar en Firestore
        db.collection("sedes")
            .add(sede)
            .addOnSuccessListener {
                Toast.makeText(context, "Sede registrada correctamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()  // Volver al fragmento anterior
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al registrar sede: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
