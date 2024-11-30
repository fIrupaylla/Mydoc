package com.example.proyectofinaldam

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
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
import com.example.proyectofinaldam.databinding.ActivityPrincipalBinding
import com.example.proyectofinaldam.datastore.UserDataStore
import com.google.firebase.auth.FirebaseAuth

class PrincipalActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.colorPink
                )
            )
        )
        val currentUser = UserDataStore.getUserData(this)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarPrincipal.toolbar)

        binding.appBarPrincipal.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_estadoConsultas, R.id.nav_citas_finalizadas, R.id.nav_reservarcita, R.id.nav_visualizarcita, R.id.nav_historial_citas
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Encuentra el NavigationView en tu layout
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // Obtén la vista del encabezado
        val headerView = navigationView.getHeaderView(0)

        // Encuentra los TextView en el encabezado
        val nameTextView = headerView.findViewById<TextView>(R.id.header_title)
        val emailTextView = headerView.findViewById<TextView>(R.id.header_subtitle)

        // Verifica si hay un usuario autenticado y actualiza los TextViews
        if (currentUser != null) {
            nameTextView.text = "Nombre: ${currentUser["nombre"]} ${currentUser["apellido"]}"
            emailTextView.text = "Correo: ${currentUser["email"]}"
        } else {
            nameTextView.text = "Invitado"
            emailTextView.text = "Invitado@example.com"
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
}