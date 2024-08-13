package com.example.supergatologin.funcionalidades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.R
import com.example.supergatologin.adaptadores.AdaptadorGateria
import com.example.supergatologin.datos.Mensaje
import com.example.supergatologin.datos.Tema
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.actualizarAppBar
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class VerTema : AppCompatActivity() {

    lateinit var iv_foto: ImageView
    lateinit var tv_titulo: TextView
    lateinit var tv_desc: TextView
    lateinit var rcy_chat: RecyclerView
    lateinit var et_entrada: EditText
    lateinit var iv_enviar: ImageView

    lateinit var usuario: Usuario
    lateinit var tema: Tema

    val ref_bd = FirebaseDatabase.getInstance().getReference()
    val lista_mensajes = mutableListOf<Mensaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_tema)

        tema = intent.getParcelableExtra<Tema>("tema")!!
        usuario = cargarUsuario(this)

        iv_foto = findViewById(R.id.iv_vtm_foto)
        tv_titulo = findViewById(R.id.tv_vtm_titulo)
        tv_desc = findViewById(R.id.tv_vtm_descripcion)
        rcy_chat = findViewById(R.id.rcy_vtm_chat)

        actualizarAppBar(
            applicationContext,
            usuario,
            tema,
            iv_foto,
            tv_titulo,
            tv_desc
        )

        ref_bd.child("temas").child(tema.id).child("lista_mensajes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista_mensajes.clear()
                    snapshot.children.forEach{ hijo: DataSnapshot?->
                        val pojo_mensaje=hijo?.getValue(Mensaje::class.java)
                        lista_mensajes.add(pojo_mensaje!!)
                    }
                    //Notificamos al adaptador del recycler de la vista que hay nuevos cambios
                    rcy_chat.adapter?.notifyDataSetChanged()
                    rcy_chat.scrollToPosition(lista_mensajes.size - 1)
                }

                override fun onCancelled(error: DatabaseError) = println(error.message)
            })

        val adaptador = AdaptadorGateria(lista_mensajes, ref_bd, this)

        rcy_chat = findViewById(R.id.rcy_vtm_chat)
        rcy_chat.adapter = adaptador
        rcy_chat.layoutManager = LinearLayoutManager(applicationContext)
        rcy_chat.setHasFixedSize(true)
        rcy_chat.recycledViewPool.setMaxRecycledViews(0, 0)

        et_entrada = findViewById(R.id.et_vtm_entrada)
        iv_enviar = findViewById(R.id.iv_vtm_enviar)
        iv_enviar.setOnClickListener() {

            val mensaje = et_entrada.text.toString().trim()
            if (mensaje != "") {

                val ahora = Calendar.getInstance()
                val formateador = SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
                val fecha_hora = formateador.format(ahora.time)

                val id_pub = ref_bd.child("temas")
                    .child(tema.id)
                    .push().key
                val publicacion = Mensaje(usuario?.email!!, fecha_hora, mensaje)

                ref_bd.child("temas")
                    .child(tema.id)
                    .child("lista_mensajes")
                    .child(id_pub!!)
                    .setValue(publicacion)

                et_entrada.text.clear()
            } else {
                Toast.makeText(applicationContext, "No has escrito nada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intencion = Intent(applicationContext, ListaTemas::class.java)
        // EXTRA ELIMINADO
        startActivity(intencion)
    }
}