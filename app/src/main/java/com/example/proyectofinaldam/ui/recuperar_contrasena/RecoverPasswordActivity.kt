package com.example.proyectofinaldam.ui.recuperar_contrasena

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinaldam.R
import com.example.proyectofinaldam.util.SendGridMail
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var etDniRecover: EditText
    private lateinit var btnRecoverPassword: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        etDniRecover = findViewById(R.id.etDniRecover)
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword)
        btnCancel = findViewById(R.id.btnCancel)

        btnRecoverPassword.setOnClickListener {
            val dni = etDniRecover.text.toString()
            if (dni.isNotEmpty()) {
                recoverPassword(dni)
            } else {
                Toast.makeText(this, "Por favor ingrese el DNI", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            finish() // Cierra la actividad y regresa a la anterior
        }
    }

    private fun recoverPassword(dni: String) {
        val db = FirebaseFirestore.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = db.collection("pacientes").whereEqualTo("DNI", dni).get().await()

                if (snapshot.documents.isNotEmpty()) {
                    val document = snapshot.documents.first()
                    val email = document.getString("Correo") ?: ""
                    val password = document.getString("Contraseña") ?: ""

                    if (email.isNotEmpty() && password.isNotEmpty()) {

                        val sendGridMail = SendGridMail()
                        sendGridMail.sendEmail(
                            email,
                            "Recuperación de contraseña",
                            "Su contraseña es: $password"
                        ) { isSuccess ->
                            if (isSuccess) {
                                showToast("El correo se envió correctamente.")
                                finish()
                            } else {
                                showToast("Hubo un error al enviar el correo.")
                            }
                        }
                    } else {
                        showToast("El usuario no tiene correo o contraseña registrados.")
                    }
                } else {
                    showToast("DNI no encontrado.")
                }
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
