package com.example.supergatologin.inicio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.supergatologin.R
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarEmail
import com.example.supergatologin.funcionalidades.PruebaCaptcha
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RegistroUsuario: AppCompatActivity() {

    lateinit var iv_foto: ImageView
    private var dir_foto: Uri? = null

    lateinit var et_nombre: TextInputEditText
    lateinit var et_email: TextInputEditText
    lateinit var et_pass1: TextInputEditText
    lateinit var et_pass2: TextInputEditText
    lateinit var sp_prod: Spinner
    lateinit var sp_estado: Spinner
    lateinit var tv_terminos: TextView
    lateinit var bt_registro: Button

    var prod: Int = 0
    var estado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_registro)

        et_nombre = findViewById(R.id.et_reg_nombre)
        et_email = findViewById(R.id.et_reg_email)
        et_pass1 = findViewById(R.id.et_reg_pass1)
        et_pass2 = findViewById(R.id.et_reg_pass2)

        //SPINNER PRODUCTOS
        val lista_panes = hashMapOf<String, Int>(
            "Barra casera" to 1,
            "Barra andaluza" to 2,
            "Barra bogavante" to 3
        )
        val nom_panes = lista_panes.keys.toList()

        var adaptador = ArrayAdapter(applicationContext,
                                     android.R.layout.simple_spinner_item,
                                     nom_panes)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sp_prod = findViewById(R.id.sp_reg_prod)
        sp_prod.adapter = adaptador

        sp_prod.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                prod = lista_panes.get(nom_panes[posicion])!!
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        //SPINNER ESTADOS
        val lista_estados = hashMapOf<String, Int>(
            "Disponible" to 0,
            "Ausente" to 1,
            "Solo compro" to 2
        )
        val nom_estados = lista_estados.keys.toList()

        adaptador = ArrayAdapter(applicationContext,
            android.R.layout.simple_spinner_item,
            nom_estados)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sp_estado = findViewById(R.id.sp_reg_estado)
        sp_estado.adapter = adaptador
        sp_estado.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                estado = lista_estados.get(nom_estados[posicion])!!
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        iv_foto = findViewById(R.id.iv_reg_foto)
        iv_foto.setOnClickListener {
            val ficheroTemp = crearFicheroImagen()
            dir_foto = FileProvider.getUriForFile(
                applicationContext,
                "com.example.supergatologin.fileprovider",
                ficheroTemp
            )
            getCamara.launch(dir_foto)
        }
        iv_foto.setOnLongClickListener {
            getGaleria.launch("image/*")
            true
        }

        tv_terminos = findViewById(R.id.tv_reg_terminos)
        tv_terminos.setOnClickListener {
            val intencion = Intent(applicationContext, Terminos::class.java)
            startActivity(intencion)
        }

        bt_registro = findViewById(R.id.bt_reg_registro)
        bt_registro.setOnClickListener {
            val nombre = et_nombre.text.toString()
            val email = adaptarEmail(et_email.text.toString())
            val contr = et_pass1.text.toString()

            val calendario = Calendar.getInstance().time
            val formateador = SimpleDateFormat("YYYY-MM-dd")
            val hoy = formateador.format(calendario)

            var usuario = HashMap<String, Any?>()
            usuario.put("nombre", nombre)
            usuario.put("email", email)
            usuario.put("contrasena", contr)
            usuario.put("foto",dir_foto)
            usuario.put("fecha_creacion", hoy)
            usuario.put("estado", estado)
            usuario.put("producto_fav_id", prod)

            val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
            intencion.putExtra("registro", usuario)
            intencion.putExtra("motivo", 0) //Captcha
            startActivity(intencion)
        }




    }

    val getCamara=registerForActivityResult(ActivityResultContracts.TakePicture()){

        if(it){
            iv_foto.setImageURI(dir_foto)
        }else{
            Snackbar.make(iv_foto,
                R.string.sinfoto,
                Snackbar.LENGTH_LONG).show()
        }
    }

    val getGaleria=registerForActivityResult(ActivityResultContracts.GetContent()){

        // it es un valor que acompaña a la función. Si esa función no guarda ningún valor, que avise
        if(it==null){
            Snackbar.make(iv_foto,
                R.string.sinfoto,
                Snackbar.LENGTH_LONG).show()
        }else{
            // Si el it tiene una ruta de un archivo (el URI), que lo cambie en el ImageView
            dir_foto = it
            iv_foto.setImageURI(it)
        }
    }

    private fun crearFicheroImagen(): File {
        val cal:Calendar?= Calendar.getInstance()
        val timeStamp:String?= SimpleDateFormat("yyyyMMdd_HHmmss").format(cal!!.time)
        val nombreFichero:String?="JPEG_"+timeStamp+"_"
        val carpetaDir: File?=applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val ficheroImagen: File?= File.createTempFile(nombreFichero!!,".jpg",carpetaDir)

        return ficheroImagen!!
    }
}