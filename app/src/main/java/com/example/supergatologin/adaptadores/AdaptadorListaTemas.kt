package com.example.supergatologin.funcionalidades

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.supergatologin.R
import com.example.supergatologin.datos.Tema
import com.example.supergatologin.datos.Usuario
import com.example.supergatologin.funcionalidades.Herramientas.Companion.adaptarAvatar

class AdaptadorListaTemas(contextoApp: Context, val lista_temas: List<Tema>):
    RecyclerView.Adapter<AdaptadorListaTemas.UsuarioViewHolder>(), Filterable {

    private var contexto = contextoApp
    private var lista_filtrada=lista_temas

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {

        // Seleccionamos la vista que se va utilizar para representar los datos de cada elemento de la lista
        contexto = parent.context
        val vista_item = LayoutInflater.from(contexto).inflate(R.layout.item_tema,parent, false)

        return UsuarioViewHolder(vista_item) // Ejecuta otra función que devuelve un ViewHolder
    }

    // Esta es la clase que se devuelve y sobre la que operará el RecyclerView
    inner class UsuarioViewHolder(usuarioView: View): RecyclerView.ViewHolder(usuarioView){
        val nombre = usuarioView.findViewById<TextView>(R.id.tv_tem_nombre)
        val desc = usuarioView.findViewById<TextView>(R.id.tv_tem_descripcion)
        val imagen = usuarioView.findViewById<ImageView>(R.id.iv_tem_foto)
        val chat = usuarioView.findViewById<ImageView>(R.id.iv_tem_chat)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {

        val item_actual=lista_filtrada!![position]  // Sacamos el objeto de la lista

        holder.nombre.text = item_actual.titulo
        holder.desc.text = item_actual.desc

        adaptarAvatar(contexto, item_actual.img_url).into(holder.imagen)
        holder.imagen.setOnClickListener {
            val intencion = Intent(contexto, EditarTema::class.java)
            intencion.putExtra("crear", false)
            intencion.putExtra("tema", item_actual)
            startActivity(contexto, intencion, null)
        }

        holder.chat.setOnClickListener {
            val intencion = Intent(contexto, VerTema::class.java)
            intencion.putExtra("tema", item_actual)
            startActivity(contexto, intencion, null)
        }
    }
    override fun getItemCount(): Int = lista_filtrada!!.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val busqueda = constraint.toString().lowercase()
                if (busqueda.length < 3) {
                    lista_filtrada = lista_temas
                } else {
                    lista_filtrada = (lista_temas.filter { // Se añaden los it (objetos candidatos) si cumplen
                        // una de las condiciones de abajo
                        it.titulo.toString().lowercase().contains(busqueda)
                    }) as MutableList<Tema>
                }
                val filterResults = FilterResults()
                filterResults.values = lista_filtrada
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) = notifyDataSetChanged()

        }
    }
}