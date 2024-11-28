package com.example.proyectofinaldam.util

import okhttp3.*
import java.io.IOException

class SendGridMail {

    companion object {
        private const val SENDGRID_API_KEY = ""
        private const val SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send"
    }

    // Modificado para aceptar un callback que devuelve true o false
    fun sendEmail(toEmail: String, subject: String, body: String, callback: (Boolean) -> Unit) {
        val client = OkHttpClient()

        // Crear el cuerpo del correo en formato JSON
        val jsonBody = """
            {
                "personalizations": [{
                    "to": [{"email": "$toEmail"}],
                    "subject": "$subject"
                }],
                "from": {"email": "jandirayrtonventocillamartinez@gmail.com"},
                "content": [{"type": "text/plain", "value": "$body"}]
            }
        """.trimIndent()

        // Crear el cuerpo de la solicitud HTTP
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody)

        // Crear la solicitud
        val request = Request.Builder()
            .url(SENDGRID_URL)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $SENDGRID_API_KEY")
            .build()

        // Enviar la solicitud en segundo plano
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // En caso de error en la solicitud, se pasa false al callback
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // El correo fue enviado exitosamente
                    callback(true)
                } else {
                    // Hubo un error al enviar el correo
                    callback(false)
                }
            }
        })
    }
}
