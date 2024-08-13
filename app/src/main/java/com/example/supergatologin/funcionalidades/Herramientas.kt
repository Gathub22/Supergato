package com.example.supergatologin.funcionalidades

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.supergatologin.R
import com.example.supergatologin.datos.Chat
import com.example.supergatologin.datos.Mensaje
import com.example.supergatologin.datos.Tema
import com.example.supergatologin.datos.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch


class Herramientas {


    companion object{

        val ref_bd = FirebaseDatabase.getInstance().getReference()
        private val ref_sto = FirebaseStorage.getInstance().getReference()

        fun adaptarEmail(email: String) : String {
            val email_formateado= email.split("@")
            return "${email_formateado[0]}@${email_formateado[1].replace(".","_")}"
        }

        fun escribirUsuario(dir_email: String, nombre: String, dir_img_fb: String, estado: Int, desc: String, admin: Boolean, fecha_creacion: String, contrasena: String, producto_fav_id: String, disponible: Boolean){
            val email_formateado = adaptarEmail(dir_email)
            ref_bd.child("usuarios")
                    .child(email_formateado!!)
                    .setValue(Usuario(email_formateado, nombre, dir_img_fb, estado, desc, admin, fecha_creacion, producto_fav_id, contrasena, disponible))
        }


        fun verUsuario(email: String, callback: (Usuario?) -> Unit){
            val usuarioRef = ref_bd.child("usuarios").child(email)
            usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val usuario = dataSnapshot.getValue(Usuario::class.java)
                    callback(usuario)
                }

                override fun onCancelled(databaseError: DatabaseError) = println(databaseError.message)

            })
        }

        fun guardarTema(tema: Tema){

            ref_bd.child("temas").child(tema.id).setValue(tema)

        }

        suspend fun guardarFoto(direccion: String, id:String?, dir_foto: Uri?):String{
            lateinit var url_foto_firebase: Uri

            url_foto_firebase = ref_sto.child("fotos")
                .child(direccion)
                .child(id!!)
                .putFile(dir_foto!!)
                .await()
                .storage
                .downloadUrl
                .await()

            return url_foto_firebase.toString()
        }

        fun prepararChat(ref_bd: DatabaseReference, contexto: Context, cliente: Usuario, destino: Usuario){

            val semaforo = CountDownLatch(1)
            val intencion = Intent(contexto, VerChat::class.java)
            var sesionChat: Chat? = null
            println("BUSCANDO CHATS...")
            ref_bd.child("chats")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val tempChat = hijo?.getValue(Chat::class.java)
                            val usuarios = tempChat!!.usuarios_email.split("&")
                            if(cliente.email in usuarios && destino.email in usuarios)
                                sesionChat = tempChat
                        }

                        if(sesionChat == null) {
                            println("CREANDO NUEVA SESION...")
                            sesionChat = Chat("${cliente.email}&${destino.email}",hashMapOf<String, Mensaje>())

                            ref_bd.child("chats").child(sesionChat!!.usuarios_email).setValue(sesionChat)
                        }
                        intencion.putExtra("chat", sesionChat)
                        intencion.putExtra("usuario", cliente)
                        intencion.putExtra("destino", destino)
                        intencion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(contexto,intencion, null)
                    }

                    override fun onCancelled(error: DatabaseError) = println(error)
                })
        }

        val colores: HashMap<Int,Int> = hashMapOf<Int, Int>(
            0 to Color.GREEN,
            1 to Color.YELLOW,
            2 to Color.GRAY
        )
        fun actualizarAppBar(contexto: Context, usuario: Usuario, ab_avatar: ImageView, ab_nombre: TextView, ab_desc: TextView, ab_estado: FrameLayout, ab_admin: ImageView){

            adaptarAvatar(contexto, usuario.foto_url).into(ab_avatar)
            ab_avatar.setOnClickListener {
                val intencion = Intent(contexto, EditarUsuario::class.java)
                intencion.putExtra("revisar", usuario)
                // EXTRA ELIMINADO
                intencion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(contexto, intencion, null)
            }
            ab_estado.backgroundTintList = ColorStateList.valueOf(colores[usuario.estado]!!)

            ab_nombre.text = usuario.nombre

            if(!usuario.admin!!){
                ab_admin.visibility = View.GONE
            }

            ab_desc.text = usuario.desc
        }

        fun actualizarAppBar(contexto: Context, cliente: Usuario, usuario: Usuario, ab_avatar: ImageView, ab_nombre: TextView, ab_desc: TextView, ab_estado: FrameLayout){

            adaptarAvatar(contexto, usuario.foto_url).into(ab_avatar)
            ab_avatar.setOnClickListener {
                val intencion = Intent(contexto, EditarUsuario::class.java)

                intencion.putExtra("revisar", usuario)
                intencion.putExtra("usuario", cliente)
                intencion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(contexto, intencion, null)
            }
            ab_estado.backgroundTintList = ColorStateList.valueOf(colores[usuario.estado]!!)

            ab_nombre.text = usuario.nombre

            ab_desc.text = usuario.desc
        }

        fun actualizarAppBar(contexto: Context, usuario: Usuario, tema: Tema, ab_foto: ImageView, ab_titulo: TextView, ab_desc: TextView){

            adaptarAvatar(contexto, tema.img_url).into(ab_foto)
            ab_foto.setOnClickListener {
                val intencion = Intent(contexto, EditarTema::class.java)

                // EXTRA ELIMINADO
                intencion.putExtra("tema", tema)
                intencion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(contexto, intencion, null)
            }
            ab_titulo.text = tema.titulo

            ab_desc.text = tema.desc
        }

        fun adaptarAvatar(contexto: Context, img_url: String?): RequestBuilder<Drawable> {
            val opciones= RequestOptions.bitmapTransform(CircleCrop()).placeholder(R.drawable.gatoplaceholder)
            return Glide.with(contexto)
                .load(img_url)
                .apply(opciones)
        }

        fun adaptarAvatar(contexto: Context, img_res: Int?): RequestBuilder<Drawable> {
            val opciones= RequestOptions.bitmapTransform(CircleCrop()).placeholder(R.drawable.gatoplaceholder)
            return Glide.with(contexto)
                .load(img_res)
                .apply(opciones)
        }

        fun accederDatos(actividad : AppCompatActivity): SharedPreferences{
            return actividad.getSharedPreferences("${actividad.getString(R.string.app_id)}_DATOS", 0)
        }

        fun cargarUsuario(actividad : AppCompatActivity): Usuario{
            val datos = accederDatos(actividad)

            val email = datos.getString("email", null)
            val nom = datos.getString("nombre", null)
            val foto_url = datos.getString("foto_url", null)
            val estado = datos.getInt("estado", -1)
            val desc = datos.getString("desc", null)
            val admin = datos.getBoolean("admin", false)
            val fecha_creacion = datos.getString("fecha", null)
            val prod_fav_id = datos.getString("prod_fav", null)
            val contrasena = datos.getString("contrasena", null)
            val disponible = datos.getBoolean("disponible", false)

            return Usuario(email, nom, foto_url, estado, desc, admin, fecha_creacion, prod_fav_id, contrasena, disponible)
        }

        fun guardarUsuario(actividad: AppCompatActivity, usuario: Usuario){
            val datos = accederDatos(actividad)

            with(datos.edit()){
                putString("email", usuario.email)
                putString("nombre", usuario.nombre)
                putString("foto_url", usuario.foto_url)
                putInt("estado", usuario.estado!!)
                putString("desc", usuario.desc)
                putBoolean("admin", usuario.admin!!)
                putString("fecha", usuario.fecha_creacion)
                putString("prod_fav", usuario.producto_fav_id)
                putString("contrasena", usuario.contrasena)
                putBoolean("disponible", usuario.disponible)
                commit()
            }

            println("Datos guardados")
        }

        class Excepcion(msg: String) : Exception(){

            override var message = "$msg >:3"
            override fun toString(): String = this.message

        }
    }
}