package com.example.supergatologin.funcionalidades

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.supergatologin.R
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarAvatar
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.prepararChat
import com.google.firebase.database.FirebaseDatabase

class EditarUsuario : AppCompatActivity(){

    private lateinit var fr_estado_avatar: FrameLayout
    private lateinit var iv_avatar: ImageView
    private lateinit var et_nombre: EditText
    private lateinit var et_desc: EditText
    private lateinit var iv_corazon: ImageView
    private lateinit var sp_comida: Spinner
    private lateinit var fr_estado: FrameLayout
    private lateinit var sp_estados: Spinner
    private lateinit var tv_email: TextView
    private lateinit var tv_fecha: TextView
    private lateinit var tv_tipo: TextView
    private lateinit var iv_admin: ImageView
    private lateinit var ll_mensaje: LinearLayout
    private lateinit var iv_icono_mensaje: ImageView
    private lateinit var ll_mods: LinearLayout
    private lateinit var et_pass: EditText
    private lateinit var bt_admin: Button
    private lateinit var bt_disponible: Button
    private lateinit var bt_aplicar: Button
    private lateinit var iv_reiniciar: ImageView
    private lateinit var tv_estado_desc: TextView
    private lateinit var tv_comida_desc: TextView
    private lateinit var ll_nuevo_estado: LinearLayout
    private lateinit var ll_nueva_comida: LinearLayout

    private lateinit var usuarioOriginal: Usuario
    private lateinit var cliente: Usuario

    private val ref_bd = FirebaseDatabase.getInstance().getReference()

    private var colorInvalido: Int = Color.GRAY

    private val colores: HashMap<Int,Int> = hashMapOf<Int, Int>(
        0 to Color.GREEN,
        1 to Color.YELLOW,
        2 to Color.GRAY
    )

    private var prod: Int = 0
    private var estado: Int = 0
    private var esAdmin: Boolean = true
    private var vive: Boolean = true
    private var dir_foto: Uri? = null


    ///TODO: VER POR QUE CUANDO ADMIN SIN QUERER DA ADMIN A USUARIO NORMAL (cixfraXdaniel)
    ///TODO: PERMITIR QUE EL ADMIN PUEDA CAMBIARSE LA FOTO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ver_usuario)

        usuarioOriginal = intent.getParcelableExtra<Usuario>("revisar")!!
        cliente = intent.getParcelableExtra<Usuario>("usuario")!!
        print(usuarioOriginal.producto_fav_id!!)
        prod = usuarioOriginal.producto_fav_id!!.toInt()
        estado = usuarioOriginal.estado!!
        vive = usuarioOriginal.disponible


        fr_estado_avatar = findViewById(R.id.fr_ver_estado_avatar)
        iv_avatar = findViewById(R.id.iv_ver_avatar)
        et_nombre = findViewById(R.id.et_ver_nombre)
        et_desc = findViewById(R.id.et_ver_desc)
        iv_corazon = findViewById(R.id.iv_ver_corazon)
        sp_comida = findViewById(R.id.sp_ver_comida)
        fr_estado = findViewById(R.id.fr_ver_estado)
        sp_estados = findViewById(R.id.sp_ver_estado)
        tv_email = findViewById(R.id.tv_ver_email)
        tv_fecha = findViewById(R.id.tv_ver_fecha)
        tv_tipo = findViewById(R.id.tv_ver_tipo)
        iv_admin = findViewById(R.id.iv_ver_admin)
        ll_mensaje = findViewById(R.id.ll_ver_mensaje)
        iv_icono_mensaje = findViewById(R.id.iv_ver_icono_mensaje)
        ll_mods = findViewById(R.id.ll_ver_mods)
        et_pass = findViewById(R.id.et_ver_pass)
        bt_admin = findViewById(R.id.bt_ver_admin_mod)
        bt_disponible = findViewById(R.id.bt_ver_disponible)
        bt_aplicar = findViewById(R.id.bt_ver_aplicar)
        iv_reiniciar = findViewById(R.id.iv_ver_reiniciar)
        tv_estado_desc = findViewById(R.id.tv_ver_estado_desc)
        tv_comida_desc = findViewById(R.id.tv_ver_comida)
        ll_nuevo_estado = findViewById(R.id.ll_ver_nuevo_estado)
        ll_nueva_comida = findViewById(R.id.ll_ver_nueva_comida)

        cargarViews()

        ll_mensaje.setOnClickListener {
            prepararChat(ref_bd, applicationContext, cliente, usuarioOriginal)
        }

        iv_reiniciar.setOnClickListener {
            Toast.makeText(applicationContext, "Reiniciando...", Toast.LENGTH_SHORT).show()
            cargarViews()
        }

        bt_aplicar.setOnClickListener {
            var usuario = HashMap<String, Any?>()
            usuario.put("nombre", et_nombre.text.toString().trim()) //TODO: VALIDAR
            usuario.put("email", usuarioOriginal.email)
            usuario.put("contrasena", et_pass.text.toString())      // YESTO TMB
            usuario.put("desc", et_desc.text.toString().trim())
            usuario.put("fecha_creacion", usuarioOriginal.fecha_creacion)
            usuario.put("estado", estado)
            usuario.put("producto_fav_id", prod)
            usuario.put("admin", esAdmin)
            usuario.put("disponible",vive)

            usuario.put("foto", dir_foto)
            usuario.put("foto_url", usuarioOriginal.foto_url)

            val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
            intencion.putExtra("registro", usuario)
            intencion.putExtra("usuario", cliente)
            intencion.putExtra("motivo", 2)
            startActivity(intencion)
        }
    }

    val getGaleria=registerForActivityResult(ActivityResultContracts.GetContent()){

        // it es un valor que acompaña a la función. Si esa función no guarda ningún valor, que avise
        if(it==null){
            Toast.makeText(applicationContext, R.string.sinfoto, Toast.LENGTH_SHORT).show()
        }else{
            // Si el it tiene una ruta de un archivo (el URI), que lo cambie en el ImageView
            dir_foto = it
            iv_avatar.setImageURI(it)
        }
    }

    private fun cargarViews() {

        et_nombre.setText(usuarioOriginal.nombre)
        val trozosEmail = usuarioOriginal.email!!.split("@")
        tv_email.setText("Su email: ${trozosEmail[0]}@${trozosEmail[1].replace("_",".")}")
        tv_fecha.setText("Se unió el ${usuarioOriginal.fecha_creacion}")
        adaptarAvatar(applicationContext, usuarioOriginal.foto_url).into(iv_avatar)

        // Si el usuario es admin y no el cliente
        if(usuarioOriginal.admin!! && usuarioOriginal != cliente){
            tv_tipo.setText("Superadmin :3")

            iv_corazon.visibility = View.GONE
            tv_comida_desc.visibility = View.GONE
            fr_estado.visibility = View.GONE
            tv_estado_desc.visibility = View.GONE
            et_desc.visibility = View.GONE
            ll_nueva_comida.visibility = View.GONE
            ll_nuevo_estado.visibility = View.GONE
            ll_mods.visibility = View.GONE
            et_nombre.focusable = View.NOT_FOCUSABLE
        }else{

            if(usuarioOriginal.admin!!){
                tv_tipo.setText("Superadmin :3")

                bt_admin.setText("Ascendido")
                bt_admin.setBackgroundColor(colorInvalido)
                bt_admin.isClickable = false

                bt_disponible.setText("Imposible borrar")
                bt_disponible.setBackgroundColor(colorInvalido)
                bt_disponible.isClickable = false

            }else{
                tv_tipo.setText("Gato normal")
                iv_admin.visibility = View.INVISIBLE

                iv_avatar.setOnClickListener {
                    getGaleria.launch("image/*")
                    true
                }

                bt_admin.setText("Ascender")
                bt_admin.isClickable = true

                if(usuarioOriginal.disponible){
                    bt_disponible.setText("Eliminar")
                    bt_disponible.isClickable = true

                    bt_disponible.setOnClickListener {
                        vive = false
                        bt_disponible.setBackgroundColor(colorInvalido)
                        bt_disponible.isClickable = false
                    }
                }else{
                    bt_disponible.setText("Recuperar")
                    bt_disponible.isClickable = true

                    bt_disponible.setOnClickListener {
                        vive = true
                        bt_disponible.setBackgroundColor(colorInvalido)
                        bt_disponible.isClickable = false
                    }
                }
            }

            // Si lo ve un cliente no admin que no es ese usuario...
            if(!cliente.admin!! && usuarioOriginal != cliente){
                et_nombre.focusable = View.NOT_FOCUSABLE
                et_desc.focusable = View.NOT_FOCUSABLE
                ll_nueva_comida.visibility = View.INVISIBLE
                ll_nuevo_estado.visibility = View.INVISIBLE
                ll_mods.visibility = View.INVISIBLE
            }else{
                // Si lo ve un cliente admin o ese mismo usuario
                et_pass.setText(usuarioOriginal.contrasena!!)

                // Si es un cliente no admin...
                if(!cliente.admin!!){
                    bt_admin.visibility = View.INVISIBLE
                    et_nombre.focusable = View.NOT_FOCUSABLE
                    et_desc.focusable = View.NOT_FOCUSABLE
                }else{

                    bt_admin.setOnClickListener {
                        bt_admin.setBackgroundColor(colorInvalido)
                        bt_admin.isClickable = false
                    }
                }
            }
            et_desc.setText(usuarioOriginal.desc)
            fr_estado_avatar.backgroundTintList = ColorStateList.valueOf(Herramientas.colores[usuarioOriginal!!.estado]!!)
            fr_estado.backgroundTintList = ColorStateList.valueOf(Herramientas.colores[usuarioOriginal!!.estado]!!)

            // COMIDA
            val lista_panes = hashMapOf<String, Int>(
                "Barra casera" to 1,
                "Barra andaluza" to 2,
                "Barra bogavante" to 3
            )
            val lista_panes_nom = hashMapOf<Int, String>(
                1 to "Barra casera",
                2 to "Barra andaluza",
                3 to "Barra bogavante"
            )
            val nom_panes = lista_panes.keys.toList()
            var adaptador = ArrayAdapter(applicationContext,
                android.R.layout.simple_spinner_item,
                nom_panes)
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_comida = findViewById(R.id.sp_ver_comida)
            sp_comida.adapter = adaptador
            sp_comida.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                    prod = lista_panes.get(nom_panes[posicion])!!
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            tv_comida_desc.text = lista_panes_nom[usuarioOriginal.producto_fav_id!!.toInt()]

            // ESTADOS
            val lista_estados = hashMapOf<String, Int>(
                "Disponible" to 0,
                "Ausente" to 1,
                "Solo compro" to 2
            )
            val lista_estados_nom = hashMapOf(
                0 to "Disponible",
                1 to "Ausente",
                2 to "Solo compro"
            )
            val nom_estados = lista_estados.keys.toList()
            val adaptador2 = ArrayAdapter(applicationContext,
                android.R.layout.simple_spinner_item,
                nom_estados)
            adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_estados = findViewById(R.id.sp_ver_estado)
            sp_estados.adapter = adaptador2
            sp_estados.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                    estado = lista_estados.get(nom_estados[posicion])!!
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            tv_estado_desc.text = lista_estados_nom[usuarioOriginal.estado!!]
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        var intencion: Intent
//        if(cliente.admin!!){
//            intencion = Intent(applicationContext, ListaUsuarios::class.java)
//        }else{
//            intencion = Intent(applicationContext, Ajustes::class.java)
//        }
//
//        intencion.putExtra("usuario", cliente)
//        startActivity(intencion)
//    }


}

// A TOMAR POR CULOOOO
//package com.example.supergatologin.funcionalidades
//
//import android.content.Intent
//import android.content.res.ColorStateList
//import android.graphics.Color
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import android.widget.*
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import com.example.supergatologin.InicioSuper
//import com.example.supergatologin.R
//import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarAvatar
//import com.example.supergatologin.datos.Usuario
//import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario
//import com.example.supergatologin.funcionalidades.Herramientas.Companion.prepararChat
//import com.google.firebase.database.FirebaseDatabase
//
//class EditarUsuario : AppCompatActivity(){
//
//    private lateinit var fr_estado_avatar: FrameLayout
//    private lateinit var iv_avatar: ImageView
//    private lateinit var et_nombre: EditText
//    private lateinit var et_desc: EditText
//    private lateinit var iv_corazon: ImageView
//    private lateinit var sp_comida: Spinner
//    private lateinit var fr_estado: FrameLayout
//    private lateinit var sp_estados: Spinner
//    private lateinit var tv_email: TextView
//    private lateinit var tv_fecha: TextView
//    private lateinit var tv_tipo: TextView
//    private lateinit var iv_admin: ImageView
//    private lateinit var ll_mensaje: LinearLayout
//    private lateinit var iv_icono_mensaje: ImageView
//    private lateinit var ll_mods: LinearLayout
//    private lateinit var et_pass: EditText
//    private lateinit var bt_admin: Button
//    private lateinit var bt_disponible: Button
//    private lateinit var bt_aplicar: Button
//    private lateinit var iv_reiniciar: ImageView
//    private lateinit var tv_estado_desc: TextView
//    private lateinit var tv_comida_desc: TextView
//    private lateinit var ll_nuevo_estado: LinearLayout
//    private lateinit var ll_nueva_comida: LinearLayout
//
//    private lateinit var usuarioOriginal: Usuario
//    private lateinit var cliente: Usuario
//
//    private val ref_bd = FirebaseDatabase.getInstance().getReference()
//
//    private var colorInvalido: Int = Color.GRAY
//
//    private val colores: HashMap<Int,Int> = hashMapOf<Int, Int>(
//        0 to Color.GREEN,
//        1 to Color.YELLOW,
//        2 to Color.GRAY
//    )
//
//    private var prod: Int = 0
//    private var estado: Int = 0
//    private var esAdmin: Boolean = true
//    private var vive: Boolean = true
//    private var dir_foto: Uri? = null
//
//
//    ///TODO: VER POR QUE CUANDO ADMIN SIN QUERER DA ADMIN A USUARIO NORMAL (cixfraXdaniel)
//    ///TODO: PERMITIR QUE EL ADMIN PUEDA CAMBIARSE LA FOTO
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.ver_usuario)
//
//        usuarioOriginal = intent.getParcelableExtra<Usuario>("revisar")!!
//        cliente = cargarUsuario(this)
//
//        prod = usuarioOriginal.producto_fav_id!!.toInt()
//        estado = usuarioOriginal.estado!!
//        vive = usuarioOriginal.disponible
//
//
//        fr_estado_avatar = findViewById(R.id.fr_ver_estado_avatar)
//        iv_avatar = findViewById(R.id.iv_ver_avatar)
//        et_nombre = findViewById(R.id.et_ver_nombre)
//        et_desc = findViewById(R.id.et_ver_desc)
//        iv_corazon = findViewById(R.id.iv_ver_corazon)
//        sp_comida = findViewById(R.id.sp_ver_comida)
//        fr_estado = findViewById(R.id.fr_ver_estado)
//        sp_estados = findViewById(R.id.sp_ver_estado)
//        tv_email = findViewById(R.id.tv_ver_email)
//        tv_fecha = findViewById(R.id.tv_ver_fecha)
//        tv_tipo = findViewById(R.id.tv_ver_tipo)
//        iv_admin = findViewById(R.id.iv_ver_admin)
//        ll_mensaje = findViewById(R.id.ll_ver_mensaje)
//        iv_icono_mensaje = findViewById(R.id.iv_ver_icono_mensaje)
//        ll_mods = findViewById(R.id.ll_ver_mods)
//        et_pass = findViewById(R.id.et_ver_pass)
//        bt_admin = findViewById(R.id.bt_ver_admin_mod)
//        bt_disponible = findViewById(R.id.bt_ver_disponible)
//        bt_aplicar = findViewById(R.id.bt_ver_aplicar)
//        iv_reiniciar = findViewById(R.id.iv_ver_reiniciar)
//        tv_estado_desc = findViewById(R.id.tv_ver_estado_desc)
//        tv_comida_desc = findViewById(R.id.tv_ver_comida)
//        ll_nuevo_estado = findViewById(R.id.ll_ver_nuevo_estado)
//        ll_nueva_comida = findViewById(R.id.ll_ver_nueva_comida)
//
//        cargarViews()
//
//        ll_mensaje.setOnClickListener {
//            prepararChat(ref_bd, applicationContext, cliente, usuarioOriginal)
//        }
//
//        iv_reiniciar.setOnClickListener {
//            Toast.makeText(applicationContext, "Reiniciando...", Toast.LENGTH_SHORT).show()
//            cargarViews()
//        }
//
//        bt_aplicar.setOnClickListener {
//            var usuario = HashMap<String, Any?>()
//            usuario.put("nombre", et_nombre.text.toString().trim()) //TODO: VALIDAR
//            usuario.put("email", usuarioOriginal.email)
//            usuario.put("contrasena", et_pass.text.toString())      // YESTO TMB
//            usuario.put("desc", et_desc.text.toString().trim())
//            usuario.put("fecha_creacion", usuarioOriginal.fecha_creacion)
//            usuario.put("estado", estado)
//            usuario.put("producto_fav_id", prod)
//            usuario.put("admin", esAdmin)
//            usuario.put("disponible",vive)
//
//            usuario.put("foto", dir_foto)
//            usuario.put("foto_url", usuarioOriginal.foto_url)
//
//            val intencion = Intent(applicationContext, PruebaCaptcha::class.java)
//            intencion.putExtra("registro", usuario)
//            intencion.putExtra("usuario", cliente)
//            intencion.putExtra("motivo", 2)
//            startActivity(intencion)
//        }
//    }
//
//    val getGaleria=registerForActivityResult(ActivityResultContracts.GetContent()){
//
//        // it es un valor que acompaña a la función. Si esa función no guarda ningún valor, que avise
//        if(it==null){
//            Toast.makeText(applicationContext, R.string.sinfoto, Toast.LENGTH_SHORT).show()
//        }else{
//            // Si el it tiene una ruta de un archivo (el URI), que lo cambie en el ImageView
//            dir_foto = it
//            iv_avatar.setImageURI(it)
//        }
//    }
//
//    private fun cargarViews() {
//
//        adaptarAvatar(applicationContext, usuarioOriginal.foto_url).into(iv_avatar)
//        et_nombre.setText(usuarioOriginal.nombre)
//        et_desc.setText(usuarioOriginal.desc)
//        val trozosEmail = usuarioOriginal.email!!.split("@")
//        tv_email.setText("Su email: ${trozosEmail[0]}@${trozosEmail[1].replace("_",".")}")
//        tv_fecha.setText("Se unió el ${usuarioOriginal.fecha_creacion}")
//        et_pass.setText(usuarioOriginal.contrasena)
//
//        // COMIDA
//        tv_comida_desc.text = lista_panes_nom[usuarioOriginal.producto_fav_id!!.toInt()]
//
//        // ESTADOS
//        tv_estado_desc.text = lista_estados_nom[usuarioOriginal.estado!!]
//
//        if(!usuarioOriginal.admin!!) {
//            tv_tipo.setText("Gato normal")
//            iv_admin.visibility = View.INVISIBLE
//        }else tv_tipo.setText("¡Supergato!")
//
//        fr_estado_avatar.backgroundTintList = ColorStateList.valueOf(Herramientas.colores[usuarioOriginal!!.estado]!!)
//        fr_estado.backgroundTintList = ColorStateList.valueOf(Herramientas.colores[usuarioOriginal!!.estado]!!)
////        ACABA ESTO POR FAVOR
//
//        /// Usuario que se vee a si mismo
//        if(cliente.email == usuarioOriginal.email){
//
//            iv_icono_mensaje.visibility = View.GONE
//            ll_mensaje.visibility = View.GONE
//
//            iv_avatar.setOnClickListener {
//                getGaleria.launch("image/*")
//                true
//            }
//
//            if(cliente.admin!!){
//
//            }
//
//        }else{ // No son lo mismo (un usuario ve el perfil del otro)
//
//            if(cliente.admin!!){ // Si lo ve un admin
//
//                if(usuarioOriginal.admin!!){ // Si ve a otro admin
//
//
//
//                }else{                       // Si ve a otro usuario normal
//
//                    iv_avatar.setOnClickListener {
//                        getGaleria.launch("image/*")
//                        true
//                    }
//
//                }
//
//            }else{              // Si lo ve otro normal
//
//                if(usuarioOriginal.admin!!) { // Si ve a un admin
//
//                }
//            }
//        }
//
//
//
//
//
//
////        // Si el usuario es admin y no el cliente
////        if(usuarioOriginal.admin!! && usuarioOriginal != cliente){
////            tv_tipo.setText("Superadmin :3")
////
////            iv_corazon.visibility = View.GONE
////            tv_comida_desc.visibility = View.GONE
////            fr_estado.visibility = View.GONE
////            tv_estado_desc.visibility = View.GONE
////            et_desc.visibility = View.GONE
////            ll_nueva_comida.visibility = View.GONE
////            ll_nuevo_estado.visibility = View.GONE
////            ll_mods.visibility = View.GONE
////            et_nombre.focusable = View.NOT_FOCUSABLE
////        }else{
////
////            if(usuarioOriginal.admin!!){
////                tv_tipo.setText("Superadmin :3")
////
////                bt_admin.setText("Ascendido")
////                bt_admin.setBackgroundColor(colorInvalido)
////                bt_admin.isClickable = false
////
////                bt_disponible.setText("Imposible borrar")
////                bt_disponible.setBackgroundColor(colorInvalido)
////                bt_disponible.isClickable = false
////
////            }else{
////                tv_tipo.setText("Gato normal")
////                iv_admin.visibility = View.INVISIBLE
////
////                iv_avatar.setOnClickListener {
////                    getGaleria.launch("image/*")
////                    true
////                }
////
////                bt_admin.setText("Ascender")
////                bt_admin.isClickable = true
////
////                if(usuarioOriginal.disponible){
////                    bt_disponible.setText("Eliminar")
////                    bt_disponible.isClickable = true
////
////                    bt_disponible.setOnClickListener {
////                        vive = false
////                        bt_disponible.setBackgroundColor(colorInvalido)
////                        bt_disponible.isClickable = false
////                    }
////                }else{
////                    bt_disponible.setText("Recuperar")
////                    bt_disponible.isClickable = true
////
////                    bt_disponible.setOnClickListener {
////                        vive = true
////                        bt_disponible.setBackgroundColor(colorInvalido)
////                        bt_disponible.isClickable = false
////                    }
////                }
////            }
////
////            // Si lo ve un cliente no admin que no es ese usuario...
////            if(!cliente.admin!! && usuarioOriginal != cliente){
////                et_nombre.focusable = View.NOT_FOCUSABLE
////                et_desc.focusable = View.NOT_FOCUSABLE
////                ll_nueva_comida.visibility = View.INVISIBLE
////                ll_nuevo_estado.visibility = View.INVISIBLE
////                ll_mods.visibility = View.INVISIBLE
////            }else{
////                // Si lo ve un cliente admin o ese mismo usuario
////                et_pass.setText(usuarioOriginal.contrasena!!)
////
////                // Si es un cliente no admin...
////                if(!cliente.admin!!){
////                    bt_admin.visibility = View.INVISIBLE
////                    et_nombre.focusable = View.NOT_FOCUSABLE
////                    et_desc.focusable = View.NOT_FOCUSABLE
////                }else{
////
////                    bt_admin.setOnClickListener {
////                        bt_admin.setBackgroundColor(colorInvalido)
////                        bt_admin.isClickable = false
////                    }
////                }
////            }
////            et_desc.setText(usuarioOriginal.desc)
////            fr_estado_avatar.backgroundTintList = ColorStateList.valueOf(Herramientas.colores[usuarioOriginal!!.estado]!!)
////            fr_estado.backgroundTintList = ColorStateList.valueOf(Herramientas.colores[usuarioOriginal!!.estado]!!)
//
//    }
//
//
//    val lista_panes = hashMapOf<String, Int>(
//        "Barra casera" to 1,
//        "Barra andaluza" to 2,
//        "Barra bogavante" to 3
//    )
//
//    val lista_panes_nom = hashMapOf<Int, String>(
//        1 to "Barra casera",
//        2 to "Barra andaluza",
//        3 to "Barra bogavante"
//    )
//
//    val lista_estados = hashMapOf<String, Int>(
//        "Disponible" to 0,
//        "Ausente" to 1,
//        "Solo compro" to 2
//    )
//
//    val lista_estados_nom = hashMapOf(
//        0 to "Disponible",
//        1 to "Ausente",
//        2 to "Solo compro"
//    )
//
//    fun prepararSpinners(){
//
//        val nom_panes = lista_panes.keys.toList()
//        var adaptador = ArrayAdapter(applicationContext,
//            android.R.layout.simple_spinner_item,
//            nom_panes)
//        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        sp_comida = findViewById(R.id.sp_ver_comida)
//        sp_comida.adapter = adaptador
//        sp_comida.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
//                prod = lista_panes.get(nom_panes[posicion])!!
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
//        val nom_estados = lista_estados.keys.toList()
//        val adaptador2 = ArrayAdapter(applicationContext,
//            android.R.layout.simple_spinner_item,
//            nom_estados)
//        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        sp_estados = findViewById(R.id.sp_ver_estado)
//        sp_estados.adapter = adaptador2
//        sp_estados.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
//                estado = lista_estados.get(nom_estados[posicion])!!
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
//
//    }
//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        var intencion = Intent(applicationContext, InicioSuper::class.java)
//        intencion.putExtra("usuario", cliente)
//        startActivity(intencion)
//    }
//
//
//}