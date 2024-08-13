package com.example.supergatologin.datos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Mensaje(
//    val id: String = "",
    val emisor_email: String = "",
    val fecha: String = "",
    val contenido: String = ""
):Parcelable