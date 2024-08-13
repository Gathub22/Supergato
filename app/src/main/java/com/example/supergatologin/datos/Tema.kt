package com.example.supergatologin.datos

import android.os.Parcelable
import com.example.supergatologin.datos.Mensaje
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tema(
    val id: String = "",
    val dueño_email: String = "",
    var disponible: Boolean = false,
    val titulo: String = "",
    val desc: String = "",
    var img_url: String = "",
    val tipo: Int = -1,
    val lista_mensajes: HashMap<String,Mensaje> = hashMapOf<String, Mensaje>()
): Parcelable