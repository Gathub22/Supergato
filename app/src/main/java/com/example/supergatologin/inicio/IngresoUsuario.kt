package com.example.supergatologin.inicio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarEmail
import com.example.supergatologin.R
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.PruebaCaptcha
import com.google.firebase.database.*

class IngresoUsuario: AppCompatActivity() {

    private lateinit var et_email: EditText
    private lateinit var et_pass: EditText
    private lateinit var boton_ingreso: Button

    private lateinit var bd_ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_ingreso)

        bd_ref = FirebaseDatabase.getInstance().getReference()

        et_email = findViewById(R.id.et_ing_email)
        et_pass = findViewById(R.id.et_ing_pass)

        boton_ingreso = findViewById<Button>(R.id.bt_ing_entrar)
        boton_ingreso.setOnClickListener {

            val email = adaptarEmail(et_email.text.toString().trim())
            println(email)
            val pass = et_pass.text.toString()

            var usuario: Usuario? = null
            bd_ref.child("usuarios")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val tempUsr = hijo?.getValue(Usuario::class.java)
                            if(email == tempUsr!!.email)
                                usuario = tempUsr
                        }

                        if(usuario != null){
                            if(usuario!!.contrasena == pass){

                                val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
                                intencion.putExtra("usuario", usuario)
                                intencion.putExtra("motivo", 1)
                                startActivity(intencion)
                            }else{
                                Toast.makeText(applicationContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(applicationContext, "El usuario no existe", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    }
                })

        }

    }
}