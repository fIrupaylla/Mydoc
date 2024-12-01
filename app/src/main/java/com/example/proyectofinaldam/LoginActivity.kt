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
import com.example.proyectofinaldam.ui.recuperar_contrasena.RecoverPasswordActivity
import com.example.proyectofinaldam.ui.registrar_paciente.RegistrarPacienteActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userData = UserDataStore.getUserData(this)

        if (currentUser != null && userData.isNotEmpty()) {
            // Redirige al usuario directamente a PrincipalActivity si ya está autenticado
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
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
        val tvRecoveryPassword: TextView = findViewById(R.id.tvRecoveryPassword)

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
        tvRecoveryPassword.setOnClickListener {
            startActivity(Intent(this, RecoverPasswordActivity::class.java))
        }
    }

    private fun performLogin(dni: String, password: String) {
        val db = FirebaseFirestore.getInstance()

        // Validar en la colección de pacientes
        db.collection("pacientes")
            .whereEqualTo("DNI", dni)
            .whereEqualTo("Contraseña", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Si existe en pacientes
                    val document = documents.first()
                    val nombre = document.getString("Nombres") ?: ""
                    val apellido = document.getString("Apellidos") ?: ""
                    val email = document.getString("Correo") ?: ""
                    val celular = document.getString("Celular") ?: ""

                    guardarDatosEnDataStore(dni, nombre, apellido, email, celular, "Paciente")
                } else {
                    // Si no está en pacientes, validamos en doctores
                    db.collection("doctores")
                        .whereEqualTo("DNI", dni)
                        .whereEqualTo("Contraseña", password)
                        .get()
                        .addOnSuccessListener { doctorDocuments ->
                            if (!doctorDocuments.isEmpty) {
                                // Si existe en doctores
                                val document = doctorDocuments.first()
                                val nombre = document.getString("Nombres") ?: ""
                                val apellido = document.getString("Apellidos") ?: ""
                                val email = document.getString("Correo") ?: ""
                                val celular = document.getString("Celular") ?: ""

                                guardarDatosEnDataStore(dni, nombre, apellido, email, celular, "Doctor")
                            } else {
                                // Si no existe en ninguna de las colecciones
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
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para guardar datos en DataStore
    private fun guardarDatosEnDataStore(
        dni: String,
        nombre: String,
        apellido: String,
        email: String,
        celular: String,
        role: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            UserDataStore.saveUserData(
                this@LoginActivity, // Usamos el contexto de LoginActivity
                dni,
                nombre,
                apellido,
                email,
                celular,
                role // Guardamos el rol
            )
        }

        Snackbar.make(
            findViewById(android.R.id.content),
            "Autenticación exitosa como $role.",
            Snackbar.LENGTH_SHORT
        ).show()

        startActivity(Intent(this, PrincipalActivity::class.java))
        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
        finish()
    }

}