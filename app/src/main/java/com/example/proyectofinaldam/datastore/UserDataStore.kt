package com.example.proyectofinaldam.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

object UserDataStore {
    private val DNI_KEY = stringPreferencesKey("dni")
    private val NOMBRE_KEY = stringPreferencesKey("nombre")
    private val APELLIDO_KEY = stringPreferencesKey("apellido")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val CELLPHONE_KEY = stringPreferencesKey("celular")

    suspend fun saveUserData(
        context: Context,
        dni: String,
        nombre: String,
        apellido: String,
        email: String,
        celular: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[DNI_KEY] = dni
            preferences[NOMBRE_KEY] = nombre
            preferences[APELLIDO_KEY] = apellido
            preferences[EMAIL_KEY] = email
            preferences[CELLPHONE_KEY] = celular
        }
    }

    fun getUserData(context: Context): Map<String, String> {
        val preferences = runBlocking {
            context.dataStore.data.first()
        }
        val dni = preferences[DNI_KEY] ?: ""
        val nombre = preferences[NOMBRE_KEY] ?: ""
        val apellido = preferences[APELLIDO_KEY] ?: ""
        val email = preferences[EMAIL_KEY] ?: ""
        val celular = preferences[CELLPHONE_KEY] ?: ""
        return mapOf(
            "dni" to dni,
            "nombre" to nombre,
            "apellido" to apellido,
            "email" to email,
            "celular" to celular
        )
    }
}
