package com.example.proyectofinaldam.ui.reservarcita

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservarcitaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is reservarcita Fragment"
    }
    val text: LiveData<String> = _text
}