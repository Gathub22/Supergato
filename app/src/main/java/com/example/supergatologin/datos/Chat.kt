package com.example.supergatologin.datos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Chat (
    val usuarios_email: String = "",
    val lista_mensajes: HashMap<String,Mensaje> = hashMapOf<String, Mensaje>()
): Parcelable