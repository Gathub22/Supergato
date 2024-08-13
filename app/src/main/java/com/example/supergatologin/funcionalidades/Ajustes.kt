package com.example.supergatologin.funcionalidades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.example.supergatologin.inicio.Portada
import com.example.supergatologin.R
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.accederDatos
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarAvatar
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario

class Ajustes : AppCompatActivity() {

    private lateinit var sw_noche: Switch
    private lateinit var iv_foto: ImageView

    private lateinit var usuario: Usuario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ajustes)
        usuario = cargarUsuario(this)

        iv_foto = findViewById(R.id.iv_aju_avatar)
        adaptarAvatar(applicationContext, usuario.foto_url).into(iv_foto)
        iv_foto.setOnClickListener {
            val intencion = Intent(applicationContext, EditarUsuario::class.java)
            intencion.putExtra("usuario", usuario)
            intencion.putExtra("revisar", usuario)
            startActivity(intencion)
        }

        sw_noche = findViewById(R.id.sw_aju_noche)

        val datos = accederDatos(this)
        sw_noche.isChecked = datos.getBoolean("noche", false)

        sw_noche.setOnCheckedChangeListener { compoundButton, checkeado ->

            if(checkeado){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                datos.edit().putBoolean("noche", true).commit()
                println("MODO NOCHE ACTIVADO")
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                datos.edit().putBoolean("noche", false).commit()
                println("MODO DIA ACTIVADO")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intencion = Intent(applicationContext, Portada::class.java)
        startActivity(intencion)
    }
}