package com.example.supergatologin.funcionalidades

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.supergatologin.R
import com.example.supergatologin.datos.Mensaje
import com.example.supergatologin.datos.Tema
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarEmail
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.guardarFoto
import com.example.supergatologin.funcionalidades.Herramientas.Companion.guardarTema
import com.example.supergatologin.funcionalidades.Herramientas.Companion.ref_bd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditarTema : AppCompatActivity() {

    lateinit var tv_titulo: TextView
    lateinit var et_dueño: TextView
    lateinit var et_nombre: EditText
    lateinit var iv_foto: ImageView
    lateinit var et_desc: TextView
    lateinit var sp_tipo: Spinner
    lateinit var iv_guardar: ImageView
    lateinit var iv_borrar: ImageView

    lateinit var usuario: Usuario
    lateinit var tema: Tema
    var tituloTemp: String = ""
    var fotoTemp: Uri? = null
    var descTemp: String = ""
    var dueñoTemp: String = ""
    var tipoTemp: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
//        METER INTENCIONES (CREACION Y EDICION) Y SEGUIR POR AQUI. LUEGO EL CHAT DEL TEMA
//        Y LISTOWO. A LOS AJUSTESSS
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editar_tema)

        tv_titulo = findViewById(R.id.tv_etm_titulo)
        et_dueño = findViewById(R.id.et_etm_autor)
        et_nombre = findViewById(R.id.et_etm_nombre)
        iv_foto = findViewById(R.id.iv_etm_foto)
        et_desc = findViewById(R.id.et_etm_desc)
        sp_tipo = findViewById(R.id.sp_etm_tipo)
        iv_guardar = findViewById(R.id.iv_etm_guardar)
        iv_borrar = findViewById(R.id.iv_etm_borrar)

        usuario = cargarUsuario(this)
        val crear = intent.getBooleanExtra("crear", false)


        println(usuario.admin)
        if(crear)
            crearTema()
        else{
            tema = intent.getParcelableExtra<Tema>("tema")!!
            if(usuario.admin!! || usuario.email == tema.dueño_email)
                editarTema()
            else
                verTema()
        }
    }

    private fun crearTema(){
        println("CREACION DE TEMA INICIADO...")
        inicTipos()

        tv_titulo.text = "Crear nuevo tema"

        et_dueño.visibility = View.GONE
        iv_borrar.visibility = View.GONE

        iv_foto.setOnClickListener {
            getGaleria.launch("image/*")
            true
        }

        iv_guardar.setOnClickListener {

            tituloTemp = et_nombre.text.toString()
            descTemp = et_desc.text.toString()

            if(tituloTemp == "" ||
                    descTemp == ""){
                Toast.makeText(applicationContext, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }else{
                val id = ref_bd.child("temas").push().key

                val temaTemp = Tema(id!!, usuario.email!!, true, tituloTemp, descTemp, "", tipoTemp, hashMapOf<String, Mensaje>())

                val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
                // EXTRA ELIMINADO
                intencion.putExtra("motivo", 3)
                intencion.putExtra("tema", temaTemp)
                if(fotoTemp != null) intencion.putExtra("foto", fotoTemp)
                startActivity(intencion)
            }
        }
    }

    private fun editarTema(){
        println("EDICION DE TEMA INICIADO...")
        tema = intent.getParcelableExtra<Tema>("tema")!!
        rellenarForm()
        sp_tipo.setSelection(lista_tipos.keys.indexOf(lista_tipos_id[tipoTemp]))

        iv_guardar.setOnClickListener {

            dueñoTemp = adaptarEmail(et_dueño.text.toString())
            tituloTemp = et_nombre.text.toString()
            descTemp = et_desc.text.toString()

            if(tituloTemp == tema.titulo &&
                dueñoTemp == tema.dueño_email &&
                descTemp == tema.desc &&
                fotoTemp == null &&
                    tipoTemp == tema.tipo){
                Toast.makeText(applicationContext, "No has cambiado ningun dato", Toast.LENGTH_SHORT).show()
            }else{

                val temaTemp = Tema(tema.id, dueñoTemp, true, tituloTemp, descTemp, tema.img_url, tipoTemp, tema.lista_mensajes)

                val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
                intencion.putExtra("motivo", 3)
                // EXTRA ELIMINADO
                intencion.putExtra("foto", fotoTemp)
                intencion.putExtra("tema", temaTemp)
                startActivity(intencion)
            }
        }

        iv_borrar.setOnClickListener {
            val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
            intencion.putExtra("motivo", 4)
            // EXTRA ELIMINADO
            intencion.putExtra("tema", tema)
            startActivity(intencion)
        }
    }

    private fun verTema(){
        println("RESUMEN DEL TEMA INICIADO...")
        tema = intent.getParcelableExtra<Tema>("tema")!!
        rellenarForm()
        sp_tipo.setSelection(lista_tipos.keys.indexOf(lista_tipos_id[tipoTemp]))

        et_nombre.isEnabled = false
        et_dueño.isEnabled = false
        et_desc.isEnabled = false
        sp_tipo.isEnabled = false
        iv_guardar.visibility = View.INVISIBLE
        iv_borrar.visibility = View.INVISIBLE
    }

    private fun rellenarForm(){

        tituloTemp = tema.titulo
        dueñoTemp = tema.dueño_email
        descTemp = tema.desc
        tipoTemp = tema.tipo

        et_nombre.setText(tituloTemp)
        et_dueño.setText(dueñoTemp)
        Glide.with(applicationContext).load(tema.img_url).into(iv_foto)
        et_desc.setText(descTemp)

        inicTipos()

//        tv_comida_desc.text = lista_panes_nom[usuarioOriginal.producto_fav_id!!.toInt()]
    }

    val lista_tipos = hashMapOf<String, Int>(
        "Supergato" to 1,
        "Gaming" to 2,
        "Mundo" to 3,
        "Otro" to 4
    )

    val lista_tipos_id = hashMapOf<Int, String>(
        1 to "Supergato",
        2 to "Gaming",
        3 to "Mundo",
        4 to "Otro"
    )

    fun inicTipos(){

        val nom_tipos = lista_tipos.keys.toList()
        var adaptador = ArrayAdapter(applicationContext,
            android.R.layout.simple_spinner_item,
            nom_tipos)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_tipo = findViewById(R.id.sp_etm_tipo)
        sp_tipo.adapter = adaptador
        sp_tipo.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                tipoTemp = lista_tipos.get(nom_tipos[posicion])!!
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

    }

    val getGaleria=registerForActivityResult(ActivityResultContracts.GetContent()){

        // it es un valor que acompaña a la función. Si esa función no guarda ningún valor, que avise
        if(it==null){
            Toast.makeText(applicationContext, R.string.sinfoto, Toast.LENGTH_SHORT).show()
        }else{
            // Si el it tiene una ruta de un archivo (el URI), que lo cambie en el ImageView
            fotoTemp = it
            iv_foto.setImageURI(it)
        }
    }
}