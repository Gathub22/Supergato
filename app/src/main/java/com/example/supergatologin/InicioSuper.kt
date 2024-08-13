package com.example.supergatologin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.supergatologin.funcionalidades.Herramientas.Companion.accederDatos
import com.example.supergatologin.funcionalidades.Herramientas.Companion.actualizarAppBar
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.*
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.guardarUsuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.verUsuario
import com.example.supergatologin.inicio.Portada


class InicioSuper: AppCompatActivity() {

    private lateinit var datos_app: SharedPreferences
    private lateinit var menu_avatar: ImageView
    private lateinit var menu_nombre: TextView
    private lateinit var menu_desc: TextView
    private lateinit var menu_estado: FrameLayout
    private lateinit var menu_ajustes: ImageView
    private lateinit var menu_admin: ImageView

    private var usuario: Usuario? = null

    private lateinit var bt_gateria: Button
    private lateinit var bt_temas: Button
    private lateinit var bt_usuarios: Button
    private lateinit var bt_salir: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_super)

        usuario = cargarUsuario(this)
        verUsuario(usuario!!.email!!){
            usuario = it
        }
        println(usuario)

        if(usuario!!.email == null){
            Toast.makeText(applicationContext, "Por favor, inicia sesión de nuevo", Toast.LENGTH_SHORT).show()
            val intencion = Intent(applicationContext, Portada::class.java)
            intencion.putExtra("fallo", 1)
            startActivity(intencion)
        }else{

            datos_app = accederDatos(this)

            if(!usuario!!.disponible){
                Toast.makeText(applicationContext, "Tu cuenta ha sido cerrada", Toast.LENGTH_SHORT).show()
                datos_app.edit().putString("email", null).commit()
                val intencion = Intent(applicationContext, Portada::class.java)
                intencion.putExtra("fallo", 1)
                startActivity(intencion)
            }else{
                guardarUsuario(this, usuario!!)
                cargarInicio()
            }
        }
    }

    fun cargarInicio(){
        // GUARDAR SESION

        datos_app.edit().putString("email", usuario!!.email).commit()

        menu_avatar = findViewById(R.id.iv_usr_avatar)
        menu_desc = findViewById(R.id.tv_usr_descripcion)
        menu_nombre = findViewById(R.id.tv_usr_nombre)
        menu_estado = findViewById(R.id.fr_usr_estado)
        menu_ajustes = findViewById(R.id.iv_usr_chat)
        menu_admin = findViewById(R.id.iv_usr_admin)

        actualizarAppBar(applicationContext, usuario!!, menu_avatar, menu_nombre, menu_desc, menu_estado, menu_admin)

        menu_ajustes.setOnClickListener {
            val intencion = Intent(applicationContext, Ajustes::class.java)
            // EXTRA ELIMINADO
            startActivity(intencion)
        }

        bt_gateria = findViewById<Button>(R.id.bt_ini_gateria)
        bt_gateria.setOnClickListener {
            val intencion = Intent(applicationContext, Gateria::class.java)
            // EXTRA ELIMINADO
            startActivity(intencion)
        }

        bt_temas = findViewById(R.id.bt_ini_temas)
        bt_temas.setOnClickListener {
            val intencion = Intent(applicationContext, ListaTemas::class.java)
            // EXTRA ELIMINADO
            startActivity(intencion)
        }

        bt_usuarios = findViewById(R.id.bt_ini_usuarios)
        if(usuario!!.admin!!){
            bt_usuarios.setOnClickListener {
                val intencion = Intent(applicationContext, ListaUsuarios::class.java)
                // EXTRA ELIMINADO

                startActivity(intencion)
            }
        }else{
            bt_usuarios.visibility = View.GONE
        }

        bt_salir = findViewById(R.id.bt_ini_salir)
        bt_salir.setOnClickListener {
            datos_app.edit().putString("email", null).commit()
            Toast.makeText(applicationContext, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            val intencion = Intent(applicationContext, Portada::class.java)
            startActivity(intencion)
        }
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Para salir pulsa \"Cerrar sesión\"", Toast.LENGTH_SHORT).show()
    }
}