package com.example.supergatologin.funcionalidades

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.R
import com.example.supergatologin.adaptadores.AdaptadorGateria
import com.example.supergatologin.datos.Mensaje
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarAvatar
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class Gateria : AppCompatActivity(){

    var bd_ref = FirebaseDatabase.getInstance().getReference()
    private var lista_mensajes = mutableListOf<Mensaje>()
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: AdaptadorGateria

    private lateinit var iv_logo: ImageView
    private lateinit var tv_titulo: TextView
    private lateinit var tv_desc: TextView
    private lateinit var et_entrada: EditText
    private lateinit var iv_enviar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_chat)

        iv_logo = findViewById(R.id.iv_usr_avatar)
        tv_titulo = findViewById(R.id.tv_usr_nombre)
        tv_desc = findViewById(R.id.tv_usr_descripcion)

        adaptarAvatar(applicationContext, R.drawable.gateria).into(iv_logo)
        tv_titulo.setText(R.string.gat_titulo)
        tv_desc.setText(R.string.gat_desc)

        val usuario = cargarUsuario(this)

        bd_ref.child("gateria")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista_mensajes.clear()
                    snapshot.children.forEach{ hijo:DataSnapshot?->
                        val pojo_mensaje=hijo?.getValue(Mensaje::class.java)
                        lista_mensajes.add(pojo_mensaje!!)
                    }
                    //Notificamos al adaptador del recycler de la vista que hay nuevos cambios
                    recycler.adapter?.notifyDataSetChanged()
                    recycler.scrollToPosition(lista_mensajes.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        adaptador = AdaptadorGateria(lista_mensajes, bd_ref, this)

        recycler = findViewById(R.id.rcy_chat)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)
        recycler.recycledViewPool.setMaxRecycledViews(0, 0)

        et_entrada = findViewById(R.id.et_cht_entrada)
        iv_enviar = findViewById(R.id.iv_cht_enviar)
        iv_enviar.setOnClickListener() {

            val mensaje = et_entrada.text.toString().trim()
            if (mensaje != "") {

                val ahora = Calendar.getInstance()
                val formateador = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
                val fecha_hora = formateador.format(ahora.time)

                val publicacion = Mensaje(usuario?.email!!, fecha_hora, mensaje)

                val id_pub = bd_ref.child("gateria")
                    .push().key

                bd_ref.child("gateria")
                    .child(id_pub!!)
                    .setValue(publicacion)

                et_entrada.text.clear()
            } else {
                Toast.makeText(applicationContext, "No has escrito nada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}