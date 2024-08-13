package com.example.supergatologin.inicio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.supergatologin.funcionalidades.Herramientas.Companion.accederDatos
import com.example.supergatologin.InicioSuper
import com.example.supergatologin.R
import com.example.supergatologin.datos.Usuario
import com.google.firebase.database.*

class Portada : AppCompatActivity(){

    lateinit var datos_app: SharedPreferences
    lateinit var ref_bd: DatabaseReference

    lateinit var bt_registro: Button
    lateinit var tv_ingreso: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portada)



        if(intent.getIntExtra("fallo",0) != 1){
            datos_app = accederDatos(this)

            val modo_noche = datos_app.getBoolean("noche", false)
            if(modo_noche){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                println("MODO NOCHE ACTIVADO")
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                println("MODO DIA ACTIVADO")
            }

            val email = datos_app.getString("email", null)

            if(email != null){
                println("USUARIO DETECTADO ${email}")

                ref_bd = FirebaseDatabase.getInstance().getReference()

                var usuario: Usuario? = null
                ref_bd.child("usuarios")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach { hijo: DataSnapshot? ->
                                val tempUsr = hijo?.getValue(Usuario::class.java)
                                if(email == tempUsr!!.email)
                                    usuario = tempUsr
                            }

                            val intencion = Intent(applicationContext, InicioSuper::class.java)
                            // EXTRA ELIMINADO
                            startActivity(intencion)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }


        bt_registro = findViewById(R.id.bt_registro)

        bt_registro.setOnClickListener {
            var intent = Intent(applicationContext, RegistroUsuario::class.java)
            startActivity(intent)
        }

        tv_ingreso = findViewById(R.id.tv_ing_ingreso)

        tv_ingreso.setOnClickListener {
            val intent = Intent(applicationContext, IngresoUsuario::class.java)
            startActivity(intent)
        }
    }


}