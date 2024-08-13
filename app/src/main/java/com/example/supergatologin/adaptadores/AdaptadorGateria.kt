package com.example.supergatologin.adaptadores
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarAvatar
import com.example.supergatologin.datos.Mensaje
import com.example.supergatologin.R
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.EditarUsuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import androidx.core.content.ContextCompat.startActivity
import com.example.supergatologin.funcionalidades.Herramientas.Companion.cargarUsuario

class AdaptadorGateria(val lista_mensajes: List<Mensaje>, val bd_ref: DatabaseReference, actividad: AppCompatActivity): RecyclerView.Adapter<AdaptadorGateria.BocadilloViewHolder>(){

    private lateinit var contexto: Context
    private val cliente = cargarUsuario(actividad)

    data class Participante (
        var ly: LinearLayout,
        var avatar: ImageView,
        var fecha: TextView,
        var mensaje: TextView
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BocadilloViewHolder {

        // Seleccionamos la vista que se va utilizar para representar los datos de cada elemento de la lista
        contexto = parent.context
        val vista_item = LayoutInflater.from(contexto).inflate(R.layout.bocadillo_chat,parent, false)

        return BocadilloViewHolder(vista_item) // Ejecuta otra función que devuelve un ViewHolder
    }

    // Esta es la clase que se devuelve y sobre la que operará el RecyclerView
    inner class BocadilloViewHolder(bocadilloView: View): RecyclerView.ViewHolder(bocadilloView){

        val items_emisor = Participante(
            ly = bocadilloView.findViewById<LinearLayout>(R.id.ly_cht_emisor),
            avatar = bocadilloView.findViewById<ImageView>(R.id.iv_cht_avatar_emisor),
            fecha = bocadilloView.findViewById<TextView>(R.id.tv_cht_fecha_emisor),
            mensaje = bocadilloView.findViewById<TextView>(R.id.tv_cht_mensaje_emisor)
        )

        val items_receptor = Participante(
            ly = bocadilloView.findViewById<LinearLayout>(R.id.ly_cht_receptor),
            avatar = bocadilloView.findViewById<ImageView>(R.id.iv_cht_avatar_receptor),
            fecha = bocadilloView.findViewById<TextView>(R.id.tv_cht_fecha_receptor),
            mensaje = bocadilloView.findViewById<TextView>(R.id.tv_cht_mensaje_receptor)
        )

    }

    override fun onBindViewHolder(holder: BocadilloViewHolder, position: Int) {

        val item_actual=lista_mensajes!![position]  // Sacamos el objeto de la lista

        if(item_actual.emisor_email == cliente.email){
            adaptarAvatar(contexto, cliente.foto_url).into(holder.items_receptor.avatar)

            holder.items_receptor.fecha.text = item_actual.fecha
            holder.items_receptor.mensaje.text = item_actual.contenido

            holder.items_emisor.ly.visibility = View.GONE
            holder.items_emisor.avatar.visibility = View.GONE
            holder.items_emisor.fecha.visibility = View.GONE
            holder.items_emisor.mensaje.visibility = View.GONE

        }else{

            var usuario_emisor: Usuario? = null
            bd_ref.child("usuarios")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val tempUsr = hijo?.getValue(Usuario::class.java)
                            if (item_actual.emisor_email == tempUsr!!.email){
                                usuario_emisor = tempUsr
                                println(usuario_emisor)
                            }
                        }
                        println(usuario_emisor)
                        if(usuario_emisor!!.disponible){
                            adaptarAvatar(contexto, usuario_emisor!!.foto_url).into(holder.items_emisor.avatar)
                        }else{
                            adaptarAvatar(contexto, "").into(holder.items_emisor.avatar)
                        }

                        holder.items_emisor.avatar.setOnClickListener {
                            if(usuario_emisor!!.disponible){
                                val intencion = Intent(contexto, EditarUsuario::class.java)
                                intencion.putExtra("revisar", usuario_emisor)
                                startActivity(contexto, intencion, null)
                            }else{

                                if(cliente.admin!!){
                                    val intencion = Intent(contexto, EditarUsuario::class.java)
                                    intencion.putExtra("revisar", usuario_emisor)
                                    startActivity(contexto, intencion, null)
                                }else{
                                    Toast.makeText(contexto, "El usuario no existe", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            holder.items_emisor.fecha.text = item_actual.fecha
            holder.items_emisor.mensaje.text = item_actual.contenido

            holder.items_receptor.ly.visibility = View.GONE
            holder.items_receptor.avatar.visibility = View.GONE
            holder.items_receptor.fecha.visibility = View.GONE
            holder.items_receptor.mensaje.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = lista_mensajes!!.size
}