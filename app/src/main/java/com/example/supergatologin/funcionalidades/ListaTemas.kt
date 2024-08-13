package com.example.supergatologin.funcionalidades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.InicioSuper
import com.example.supergatologin.R
import com.example.supergatologin.adaptadores.AdaptadorListaUsuarios
import com.example.supergatologin.datos.Tema
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.verUsuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaTemas : AppCompatActivity() {

    private lateinit var iv_crear: ImageView
    private lateinit var grupo_rb: RadioGroup
    private lateinit var cb_supergato: CheckBox
    private lateinit var cb_gaming: CheckBox
    private lateinit var cb_mundo: CheckBox
    private lateinit var cb_otro: CheckBox


    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: AdaptadorListaTemas

    private val ref_bd = FirebaseDatabase.getInstance().getReference()
    private var lista_temas: MutableList<Tema> = mutableListOf<Tema>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_temas)

        cb_supergato = findViewById(R.id.cb_tem_supergato)
        cb_gaming = findViewById(R.id.cb_tem_gaming)
        cb_mundo = findViewById(R.id.cb_tem_mundo)
        cb_otro = findViewById(R.id.cb_tem_otro)

        adaptador = AdaptadorListaTemas(applicationContext, lista_temas)

        recycler = findViewById(R.id.rcy_temas)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)
        recycler.recycledViewPool.setMaxRecycledViews(0, 0)

        iv_crear = findViewById(R.id.iv_tem_crear)
        iv_crear.setOnClickListener {
            val intencion = Intent(applicationContext, EditarTema::class.java)
            // EXTRA ELIMINADO
            intencion.putExtra("crear", true)
            startActivity(intencion)
        }

        cb_supergato.setOnClickListener {
            buscarTemas()
        }

        cb_gaming.setOnClickListener {
            buscarTemas()
        }

        cb_mundo.setOnClickListener {
            buscarTemas()
        }

        cb_otro.setOnClickListener {
            buscarTemas()
        }
    }

    fun buscarTemas(){

        var lista = mutableListOf(1,2,3,4)

        if(!cb_supergato.isChecked &&
                    !cb_gaming.isChecked &&
                    !cb_mundo.isChecked &&
                    !cb_otro.isChecked){
            ref_bd.child("temas")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        lista_temas.clear()
                        snapshot.children.forEach{ hijo: DataSnapshot?->
                            val pojo_tema=hijo?.getValue(Tema::class.java)!!
                            if(pojo_tema.disponible && pojo_tema.tipo in lista) lista_temas.add(pojo_tema)
                        }
                        //Notificamos al adaptador del recycler de la vista que hay nuevos cambios
                        recycler.adapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) = println(error.message)
                })
        }else{
            if(!cb_supergato.isChecked) lista.remove(1)
            if(!cb_gaming.isChecked) lista.remove(2)
            if(!cb_mundo.isChecked) lista.remove(3)
            if(!cb_otro.isChecked) lista.remove(4)

            ref_bd.child("temas")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        lista_temas.clear()
                        snapshot.children.forEach{ hijo: DataSnapshot?->
                            val pojo_tema=hijo?.getValue(Tema::class.java)!!
                            if(pojo_tema.disponible && pojo_tema.tipo in lista) lista_temas.add(pojo_tema)
                        }
                        //Notificamos al adaptador del recycler de la vista que hay nuevos cambios
                        recycler.adapter?.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) = println(error.message)
                })
        }
    }

    override fun onBackPressed() {
        val intencion = Intent(applicationContext, InicioSuper::class.java)
        // EXTRA ELIMINADO
        startActivity(intencion)
    }
}