package com.example.supergatologin.funcionalidades

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.*
import com.example.supergatologin.datos.Captcha
import com.example.supergatologin.funcionalidades.Herramientas.Companion.escribirUsuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.guardarFoto
import com.example.supergatologin.InicioSuper
import com.example.supergatologin.R
import com.example.supergatologin.datos.Tema
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.guardarTema
import com.example.supergatologin.funcionalidades.Herramientas.Companion.guardarUsuario
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1

class PruebaCaptcha : AppCompatActivity() {

    private lateinit var ref_bd: DatabaseReference
    private lateinit var ref_sto: StorageReference
    lateinit var imagen: ImageView
    lateinit var entrada: TextInputEditText
    lateinit var comp: Button
    lateinit var solucion: String

    var motivos = hashMapOf<Int, KFunction1<AppCompatActivity, Unit>>(
        0 to ::crearCuenta,
        1 to ::iniciarSesion,
        2 to ::editarCuenta,
        3 to ::meterTema,
        4 to ::borrarTema
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prueba_captcha)

        var accion : KFunction1<AppCompatActivity, Unit>? = null
        val motivo = intent.getIntExtra("motivo", -1)
        if(motivo == -1){
            Toast.makeText(applicationContext, "Error de Captcha", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            accion = motivos.get(motivo)
        }

        ref_sto = FirebaseStorage.getInstance().getReference()
        ref_bd = FirebaseDatabase.getInstance().getReference()

        imagen = findViewById(R.id.iv_cap_captcha)
        entrada = findViewById(R.id.et_cap_solucion)
        comp = findViewById(R.id.bt_cap_comprobar)

        val c = Captcha(500,300,3,"alpha", 5)

        imagen.setImageBitmap(c.img)
        solucion = c.getAnswer()

        comp.setOnClickListener {
            if(!c.isDead){
                if(c.checkAnswer(entrada.text.toString())){
                    Toast.makeText(applicationContext, "Espera...", Toast.LENGTH_SHORT).show()
                    accion!!.invoke(this)
                }else{
                    Toast.makeText(applicationContext, "Respuesta incorrecta", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            }else{
                Toast.makeText(applicationContext, "Captcha inválido. Inténtalo de nuevo", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun crearCuenta(actividad: AppCompatActivity){

        val registro = intent.getSerializableExtra("registro") as HashMap<String, Any>

        GlobalScope.launch (Dispatchers.IO) {
            println("ESCRIBIENDO USUARIO...")
            val id_foto = ref_bd.child("usuarios").push().key

            var desc = "¡Hola! Me llamo ${registro.get("nombre") as String}"

            var dir_img_fb = guardarFoto(
                    "usuarios",
                    id_foto,
                    registro.get("foto") as Uri?
            )

            escribirUsuario(
                registro.get("email") as String,
                registro.get("nombre") as String,
                dir_img_fb,
                registro.get("estado") as Int,
                desc,
                false,
                registro.get("fecha_creacion") as String,
                registro.get("contrasena") as String,
                registro.get("producto_fav_id").toString(),
                true
            )

            val usuario = Usuario(
                registro.get("email") as String,
                registro.get("nombre") as String,
                dir_img_fb,
                registro.get("estado") as Int,
                desc,
                false,
                registro.get("fecha_creacion") as String,
                registro.get("contrasena") as String,
                registro.get("producto_fav_id").toString(),
                true
            )

            guardarUsuario(actividad, usuario)
            val intencion = Intent(applicationContext, InicioSuper::class.java)
            startActivity(intencion)
        }
    }
    private fun editarCuenta(actividad: AppCompatActivity){

        val registro = intent.getSerializableExtra("registro") as HashMap<String, Any>

        var usuario: Usuario? = null
        GlobalScope.launch (Dispatchers.IO) {
            println("EDITANDO USUARIO...")

            var dir_img_fb: String?
            val dir_img = registro.get("foto") as Uri?
            if(dir_img != null){
                println("NUEVA FOTO DETECTADA")
                val id_foto = ref_bd.child("usuarios").push().key
                dir_img_fb = guardarFoto(
                    "usuarios",
                    id_foto,
                    dir_img
                )
            }else{
                dir_img_fb = registro.get("foto_url") as String?
            }

            escribirUsuario(
                registro.get("email") as String,
                registro.get("nombre") as String,
                dir_img_fb!!,
                registro.get("estado") as Int,
                (registro.get("desc") as String?)!!,
                (registro.get("admin") as Boolean)!!,
                registro.get("fecha_creacion") as String,
                registro.get("contrasena") as String,
                registro.get("producto_fav_id").toString(),
                registro.get("disponible") as Boolean
            )

            usuario = Usuario(
                registro.get("email") as String,
                registro.get("nombre") as String,
                dir_img_fb,
                registro.get("estado") as Int,
                registro.get("desc") as String,
                registro.get("admin") as Boolean,
                registro.get("fecha_creacion") as String,
                registro.get("producto_fav_id").toString(),
                registro.get("contrasena") as String,

                registro.get("disponible") as Boolean
            )
            runOnUiThread {
                Toast.makeText(applicationContext, "Cambios aplicados", Toast.LENGTH_SHORT).show()
                val nuevaIntencion = Intent(applicationContext, EditarUsuario::class.java)
                nuevaIntencion.putExtra("revisar", usuario)
                var cliente = intent.getParcelableExtra("usuario") as Usuario?
                if(usuario!!.email == cliente!!.email){
                    cliente = usuario
                }
                guardarUsuario(actividad, cliente!!)
                startActivity(nuevaIntencion)
            }
        }
    }

    private fun meterTema(actividad: AppCompatActivity){

        GlobalScope.launch (Dispatchers.IO){
            println("METIENDO TEMA...")

            val tema = intent.getParcelableExtra<Tema>("tema")!!

            val img_url = intent.getParcelableExtra<Uri>("foto")

            if(img_url != null){
                val id = Herramientas.ref_bd.child("temas").push().key
                tema.img_url = guardarFoto("temas", id, img_url)
            }
            guardarTema(tema)

            runOnUiThread {
                Toast.makeText(applicationContext, "Tu tema ya existe :)", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, VerTema::class.java)
                intent.putExtra("tema", tema)
                startActivity(intent)
            }
        }

    }

    private fun borrarTema(actividad: AppCompatActivity){
        val usuario = intent.getParcelableExtra<Usuario>("usuario")!!
        val tema = intent.getParcelableExtra<Tema>("tema")!!

        tema.disponible = false
        ref_bd.child("temas").child(tema.id).setValue(tema)
        Toast.makeText(applicationContext, "Tema borrado con éxito", Toast.LENGTH_SHORT).show()

        val intencion = Intent(applicationContext, ListaTemas::class.java)
        // EXTRA ELIMINADO
        startActivity(intencion)
    }


    private fun iniciarSesion(actividad: AppCompatActivity){
        println("INICIANDO SESION...")
        guardarUsuario(this, intent.getParcelableExtra<Usuario>("usuario")!!)
        val intencion = Intent(applicationContext, InicioSuper::class.java)
        startActivity(intencion)
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Captcha cancelado", Toast.LENGTH_SHORT).show()
        finish()
    }

}