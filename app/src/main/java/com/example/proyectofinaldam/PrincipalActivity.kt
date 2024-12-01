package com.example.proyectofinaldam

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.NavigationUI
import com.example.proyectofinaldam.databinding.ActivityPrincipalBinding
import com.example.proyectofinaldam.datastore.UserDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrincipalActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de la barra de acción
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(this, R.color.colorPink)
            )
        )

        // Obtiene los datos del usuario y su rol
        val currentUser = UserDataStore.getUserData(this)
        val userRole = currentUser["role"] ?: "Invitado" // Rol por defecto

        // Enlace del layout
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarPrincipal.toolbar)

        // Acción para el FAB (si es necesario)
        binding.appBarPrincipal.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_principal)

        // Configura los destinos principales
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_estadoConsultas, R.id.nav_citas_finalizadas,
                R.id.nav_registrar_cita, R.id.nav_visualizarcita, R.id.nav_historial_citas, R.id.nav_logout
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configura el menú según el rol del usuario
        navView.menu.clear()
        when (userRole) {
            "Paciente" -> navView.inflateMenu(R.menu.menu_paciente)
            "Doctor" -> navView.inflateMenu(R.menu.menu_doctor)
            else -> navView.inflateMenu(R.menu.activity_main_drawer) // Para roles desconocidos o invitados
        }

        // Conecta el NavigationView con el NavController
        navView.setupWithNavController(navController)

        // Personalización del encabezado del NavigationView
        val headerView = navView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.header_title)
        val emailTextView = headerView.findViewById<TextView>(R.id.header_subtitle)

        if (currentUser.isNotEmpty()) {
            nameTextView.text = "Nombre: ${currentUser["nombre"]} ${currentUser["apellido"]}"
            emailTextView.text = "Correo: ${currentUser["email"]}"
        } else {
            nameTextView.text = "Invitado"
            emailTextView.text = "Invitado@example.com"
        }

        // Manejo personalizado de logout
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    cerrarSesion()
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding.drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                cerrarSesion() // Llama a tu función de cierre de sesión
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cerrarSesion() {
        Log.d("CerrarSesion", "Iniciando cierre de sesión")

        CoroutineScope(Dispatchers.IO).launch {
            UserDataStore.clearUserData(this@PrincipalActivity)
            Log.d("CerrarSesion", "Datos eliminados de DataStore")
        }

        FirebaseAuth.getInstance().signOut()
        Log.d("CerrarSesion", "Sesión de Firebase cerrada")

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Log.d("CerrarSesion", "Redirigiendo al LoginActivity")

        finish()
    }
}