package RecyclerViewHelper

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import daniel.granados.crudisaac2a.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import modelo.ClaseConexion
import modelo.dataClassProductos
import java.util.UUID

class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarLista(nuevaLista: List<dataClassProductos>){

        Datos = nuevaLista
        notifyDataSetChanged()

    }

    fun eliminarRegistro(nombreProducto : String, posicion: Int){

        //Quitar el elemento de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO){

            //1- Crear objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            val delProducto = objConexion?.prepareStatement("delete tbProductos where nombreProducto = ?")!!
            delProducto.setString(1, nombreProducto)
            delProducto.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }

        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()

    }

    fun actualizarProducto(nombreProducto: String, uuid: String){
        //1-Creo una corrutina
        GlobalScope.launch(Dispatchers.IO){
            //1- Crear objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Variable que contenga un prepareStatement
            val updateProducto = objConexion?.prepareStatement("update tbProductos set nombreProducto = ? where uuid = ?")!!
            updateProducto.setString(1, nombreProducto)
            updateProducto.setString(2, uuid)
            updateProducto.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =            LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(vista)
    }
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombreProducto

        val item = Datos[position]
        holder.imgBorrar.setOnClickListener {


            holder.imgBorrar.setOnClickListener {
                //Creamos una alerta
                //1-Invocamos el contexto

                val context = holder.itemView.context

                //Creo la alerta
                val builder = AlertDialog.Builder(context)

                //Le ponemos un titulo a la alerta

                builder.setTitle("¡Espera!")

                //Ponemos el mensaje
                builder.setMessage("¿Estás seguro de que deseas elimar el registro?")

                //Paso final, agregamos los botones
                builder.setPositiveButton("Si"){
                    dialog, wich ->
                    eliminarRegistro(item.nombreProducto, position)
                }

                builder.setNegativeButton("No"){
                    dialog, wich ->
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }

            holder.imgEditar.setOnClickListener {

                val context = holder.itemView.context

                //Crear alerta

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Editar nombre de producto:")

                //Agregamos cuadro de texto para que el usuario escriba el nuevo nombre
                val cuadritoNuevoNombre = EditText(context)
                cuadritoNuevoNombre.setHint(item.nombreProducto)
                builder.setView(cuadritoNuevoNombre)

                //Paso final, agregamos los botones
                builder.setPositiveButton("Actualizar"){
                        dialog, wich ->
                    actualizarProducto(cuadritoNuevoNombre.text.toString(), item.uuid)
                }

                builder.setNegativeButton("Cancelar"){
                        dialog, wich ->
                    dialog.dismiss()
                }

                val alertDialog = builder.create()
                alertDialog.show()
            }
        }


    }
}