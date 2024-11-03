package com.example.proyectofinaldam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

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

        val etEmailLogin: EditText = findViewById(R.id.etEmailLogin)
        val etPasswordLogin: EditText = findViewById(R.id.etPasswordLogin)
        val btLogin: Button = findViewById(R.id.btLogin)
        //val btRegister: Button = findViewById(R.id.btRegister)

        val auth = FirebaseAuth.getInstance()

        //Redirect to LoginActivity
        //btRegister.setOnClickListener {
        //    startActivity(Intent(this, RegisterActivity::class.java))
        //}

        btLogin.setOnClickListener {
            val email = etEmailLogin.text.toString()
            val password = etPasswordLogin.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->
                    if(task.isSuccessful){
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Authentication success.",
                            Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(this, PrincipalActivity::class.java))
                    }else {
                        // If sign in fails, display a message to the user.
                        //Message in snackbar
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Authentication failed.",
                            Snackbar.LENGTH_SHORT).show()
                        //startActivity(Intent(this, PrincipalActivity::class.java))
                    }
                }
        }


    }
}