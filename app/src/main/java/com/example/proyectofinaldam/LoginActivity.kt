package com.example.proyectofinaldam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinaldam.datastore.UserDataStore
import com.example.proyectofinaldam.ui.registrar_paciente.RegistrarPacienteActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etDniLogin: EditText = findViewById(R.id.etDniLogin)
        val etPasswordLogin: EditText = findViewById(R.id.etPasswordLogin)
        val btLogin: Button = findViewById(R.id.btLogin)
        val tvNewAccount: TextView = findViewById(R.id.tvNewAccount)

        btLogin.setOnClickListener {
            val dni = etDniLogin.text.toString()
            val password = etPasswordLogin.text.toString()
            if (dni.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese las credenciales", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(dni = dni, password = password)
            }

        }

        tvNewAccount.setOnClickListener {
            startActivity(Intent(this, RegistrarPacienteActivity::class.java))
        }
    }

    private fun performLogin(dni: String, password: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("pacientes")
            .whereEqualTo("DNI", dni)
            .whereEqualTo("Contraseña", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val nombre = document.getString("Nombres") ?: ""
                    val apellido = document.getString("Apellidos") ?: ""
                    val email = document.getString("Correo") ?: ""
                    val celular = document.getString("Celular") ?: ""

                    // Guardamos en DataStore
                    CoroutineScope(Dispatchers.IO).launch {
                        UserDataStore.saveUserData(
                            this@LoginActivity, // Usamos el contexto de LoginActivity
                            dni,
                            nombre,
                            apellido,
                            email,
                            celular
                        )
                    }

                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Authentication success.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this, PrincipalActivity::class.java))
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Credenciales incorrectas.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}