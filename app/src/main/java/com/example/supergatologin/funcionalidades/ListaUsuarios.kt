package com.example.supergatologin.funcionalidades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.R
import com.example.supergatologin.adaptadores.AdaptadorListaUsuarios
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
import com.example.supergatologin.inicio.Portada
import com.google.firebase.database.*

class ListaUsuarios : AppCompatActivity() {

    private lateinit var usuario: Usuario
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: AdaptadorListaUsuarios

    private val bd_ref = FirebaseDatabase.getInstance().getReference()
    private var lista_usuarios = mutableListOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_usuarios)

        val usuario = cargarUsuario(this)

        bd_ref.child("usuarios")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista_usuarios.clear()
                    snapshot.children.forEach{ hijo: DataSnapshot?->
                        val pojo_usuario=hijo?.getValue(Usuario::class.java)
                        if(pojo_usuario!!.email != usuario!!.email)
                            lista_usuarios.add(pojo_usuario!!)
                    }
                    //Notificamos al adaptador del recycler de la vista que hay nuevos cambios
                    recycler.adapter?.notifyDataSetChanged()
                    recycler.scrollToPosition(lista_usuarios.size - 1)
                }

                override fun onCancelled(error: DatabaseError) = println(error.message)
            })

        adaptador = AdaptadorListaUsuarios(lista_usuarios, bd_ref, this)

        recycler = findViewById(R.id.rcy_usuarios)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
//        recycler.setHasFixedSize(true)
//        recycler.recycledViewPool.setMaxRecycledViews(0, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_busqueda,menu)
        val item = menu?.findItem(R.id.action_search)

        val searchView = item?.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter(newText)
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                adaptador.filter.filter("")
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val intencion = Intent(applicationContext, Portada::class.java)
        intencion.putExtra("usuario", intent.getParcelableExtra<Usuario>("usuario"))
        startActivity(intencion)
    }
}