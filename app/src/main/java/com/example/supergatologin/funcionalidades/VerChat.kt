package com.example.supergatologin.funcionalidades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.R
import com.example.supergatologin.adaptadores.AdaptadorGateria
import com.example.supergatologin.datos.Chat
import com.example.supergatologin.datos.Mensaje
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.actualizarAppBar
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class VerChat : AppCompatActivity() {

    private lateinit var fr_estado : FrameLayout
    private lateinit var iv_avatar: ImageView
    private lateinit var tv_nombre: TextView
    private lateinit var tv_desc: TextView
    private lateinit var et_entrada: EditText
    private lateinit var iv_enviar: ImageView
    private lateinit var recycler: RecyclerView

    private lateinit var adaptador: AdaptadorGateria

    private val ref_bd = FirebaseDatabase.getInstance().getReference()
    private val lista_mensajes = mutableListOf<Mensaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vista_chat)

        val chat = intent.getParcelableExtra<Chat>("chat")
        val usuario = cargarUsuario(this)
        val destino = intent.getParcelableExtra<Usuario>("destino")

        fr_estado = findViewById(R.id.fr_usr_estado)
        iv_avatar = findViewById(R.id.iv_usr_avatar)
        tv_nombre = findViewById(R.id.tv_usr_nombre)
        tv_desc = findViewById(R.id.tv_usr_descripcion)
        et_entrada = findViewById(R.id.et_cht_entrada)
        iv_enviar = findViewById(R.id.iv_cht_enviar)

        println(usuario)
        println(destino)
        actualizarAppBar(applicationContext,usuario!!, destino!!, iv_avatar, tv_nombre, tv_desc, fr_estado)

        ref_bd.child("chats").child(chat!!.usuarios_email).child("lista_mensajes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista_mensajes.clear()
                    snapshot.children.forEach{ hijo: DataSnapshot?->
                        val pojo_mensaje=hijo?.getValue(Mensaje::class.java)
                        lista_mensajes.add(pojo_mensaje!!)
                    }
                    //Notificamos al adaptador del recycler de la vista que hay nuevos cambios
                    recycler.adapter?.notifyDataSetChanged()
                    recycler.scrollToPosition(lista_mensajes.size - 1)
                }

                override fun onCancelled(error: DatabaseError) = println(error.message)
            })

        adaptador = AdaptadorGateria(lista_mensajes, ref_bd, this)

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

                val id_pub = ref_bd.child("gateria")
                    .push().key
                val publicacion = Mensaje(usuario?.email!!, fecha_hora, mensaje)

                ref_bd.child("chats")
                    .child(chat.usuarios_email)
                    .child("lista_mensajes")
                    .child(id_pub!!)
                    .setValue(publicacion)

                et_entrada.text.clear()
            } else {
                Toast.makeText(applicationContext, "No has escrito nada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}