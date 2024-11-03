package com.example.proyectofinaldam.ui.visualizarcita

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VisualizarcitaViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is visualizarcita Fragment"
    }
    val text: LiveData<String> = _text
}