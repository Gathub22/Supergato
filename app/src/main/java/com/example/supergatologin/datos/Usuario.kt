package com.example.supergatologin.datos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Usuario(
    var email: String? = "",
    var nombre: String? = "",
    var foto_url: String? = "",
    var estado: Int? = -1,
    var desc: String? = "",
    var admin: Boolean? = true,
    var fecha_creacion: String? = "",
    var producto_fav_id: String? = "",
    var contrasena: String? = "",
    var disponible: Boolean = true
): Parcelable