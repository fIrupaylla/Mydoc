package com.example.proyectofinaldam.ui.registrar_paciente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
                UserForm { nombres, apellidos, dni, email, phone, password, role ->
                    checkAndSavePatient(
                        dni = dni,
                        nombre = nombres,
                        apellido = apellidos,
                        email = email,
                        phone = phone,
                        password = password,
                        role = role
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
        password: String,
        role: String,
        collection: String // Recibe la colección como argumento
    ) {
        val user = hashMapOf(
            "Nombres" to nombres,
            "Apellidos" to apellidos,
            "DNI" to dni,
            "Correo" to email,
            "Celular" to phone,
            "Contraseña" to password,
            "role" to role
        )

        db.collection(collection)
            .document(dni)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos guardados correctamente en $collection", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkAndSavePatient(
        dni: String,
        nombre: String,
        apellido: String,
        email: String,
        phone: String,
        password: String,
        role: String // Incluimos el rol para determinar la colección
    ) {
        val collection = if (role == "Paciente") "pacientes" else "doctores"
        db.collection(collection)
            .whereEqualTo("DNI", dni)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "El DNI ya está registrado en $collection.", Toast.LENGTH_SHORT).show()
                } else {
                    // Pasamos el rol a la función saveToFirestore
                    saveToFirestore(
                        nombres = nombre,
                        apellidos = apellido,
                        dni = dni,
                        email = email,
                        phone = phone,
                        password = password,
                        role = role,
                        collection = collection // Aquí pasamos la colección
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
@OptIn(ExperimentalMaterial3Api::class)
fun UserForm(onSubmit: (String, String, String, String, String, String, String) -> Unit) {
    var dni by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") } // Perfil seleccionado
    val roles = listOf("Paciente", "Doctor") // Opciones para seleccionar el perfil
    var expanded by remember { mutableStateOf(false) } // Controla si el menú está abierto o cerrado
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Registrar Usuario",
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

        // Selector de perfil con ExposedDropdownMenuBox
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded } // Controla el abrir/cerrar del menú
        ) {
            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                label = { Text("Seleccione el perfil") },
                readOnly = true, // No permite escribir en el campo
                trailingIcon = { // Icono para indicar que es un menú desplegable
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor() // Ancla para el menú desplegable
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // Cierra el menú al hacer clic fuera
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        onClick = {
                            selectedRole = role
                            expanded = false // Cierra el menú al seleccionar
                        },
                        text = { Text(role) }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (nombres.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || email.isEmpty() ||
                    phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedRole.isEmpty()
                ) {
                    Toast.makeText(context, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                } else {
                    onSubmit(nombres, apellidos, dni, email, phone, password, selectedRole)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF95C34A)
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(text = "Registrar", color = Color.White)
        }

        Button(
            onClick = { (context as? RegistrarPacienteActivity)?.finish() },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Cancelar")
        }
    }
}
