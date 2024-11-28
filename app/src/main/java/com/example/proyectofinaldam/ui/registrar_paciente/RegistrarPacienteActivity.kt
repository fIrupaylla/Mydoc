package com.example.proyectofinaldam.ui.registrar_paciente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarPacienteActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance() // Inicializar Firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                UserForm { nombres, apellidos, dni, email, phone, password ->
                    checkAndSavePatient(
                        nombre = nombres,
                        apellido = apellidos,
                        dni = dni,
                        email = email,
                        phone = phone,
                        password = password
                    )
                }
            }
        }
    }

    private fun saveToFirestore(
        nombres: String,
        apellidos: String,
        dni: String,
        email: String,
        phone: String,
        password: String
    ) {
        val user = hashMapOf(
            "Nombres" to nombres,
            "Apellidos" to apellidos,
            "DNI" to dni,
            "Correo" to email,
            "Celular" to phone,
            "Contraseña" to password
        )

        db.collection("pacientes")
            .document(dni)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun checkAndSavePatient(
        dni: String, nombre: String, apellido: String, email: String, phone: String,
        password: String
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("pacientes")
            .whereEqualTo("DNI", dni)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "El DNI ya está registrado.", Toast.LENGTH_SHORT).show()
                } else {
                    saveToFirestore(
                        dni = dni,
                        nombres = nombre,
                        apellidos = apellido,
                        email = email,
                        phone = phone,
                        password = password
                    )
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar el DNI: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}

@Composable
fun UserForm(onSubmit: (String, String, String, String, String, String) -> Unit) {
    var dni by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var acceptSMSAndEmail by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),  // Agregar scroll
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Registrar Paciente",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
        OutlinedTextField(
            value = nombres,
            onValueChange = { nombres = it },
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text("DNI") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Celular") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptTerms,
                onCheckedChange = { acceptTerms = it }
            )
            Text(
                text = "Acepto los términos y condiciones",
                modifier = Modifier.clickable { acceptTerms = !acceptTerms }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptSMSAndEmail,
                onCheckedChange = { acceptSMSAndEmail = it }
            )
            Text(
                text = "Acepto recibir SMS y correos electrónicos",
                modifier = Modifier.clickable { acceptSMSAndEmail = !acceptSMSAndEmail }
            )
        }

        Button(
            onClick = {
                // Validación de campos y envío
                when {
                    nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                        errorMessage = "Todos los campos son obligatorios."
                        showError = true
                    }

                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        errorMessage = "El correo no tiene un formato válido."
                        showError = true
                    }

                    password != confirmPassword -> {
                        errorMessage = "Las contraseñas no coinciden."
                        showError = true
                    }

                    !acceptTerms || !acceptSMSAndEmail -> {
                        errorMessage =
                            "Debe aceptar los términos y condiciones y permitir recibir SMS y correos."
                        showError = true
                    }

                    else -> {
                        showError = false
                        onSubmit(nombres, apellidos, dni, email, phone, password)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF4CFE3)
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(
                text = "Registrar",
                color = Color.White
            )
        }
        if (showError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Button(
            onClick = { (context as? RegistrarPacienteActivity)?.finish() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) { Text(text = "Cancelar") }

    }

}
