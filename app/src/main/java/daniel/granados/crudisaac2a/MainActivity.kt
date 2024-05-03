package daniel.granados.crudisaac2a

import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassProductos

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1-Mandar a llamar a todos los elementos de la pantalla
        val txtNombreUsuario = findViewById<EditText>(R.id.txtNombreProducto)
        val txtPrecio = findViewById<EditText>(R.id.txtPrecioProducto)
        val txtCantidad = findViewById<EditText>(R.id.txtCantidadProducto)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)

        //2- Programar el boton
        btnAgregar.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO){
                //Guardar datos
                //1-Crear objeto de la clase conexión

                val claseConexion = ClaseConexion().cadenaConexion()

                //2- Crear variable que contenga un PreparedStatement
                val addProducto = claseConexion?.prepareStatement("insert into tbProductos(nombreProducto, precio, cantidad) values(?, ?, ?)")!!

                addProducto.setString(1, txtNombreUsuario.text.toString())
                addProducto.setInt(2, txtPrecio.text.toString().toInt())
                addProducto.setInt( 3, txtCantidad.text.toString().toInt())
                addProducto.executeUpdate()
        }

        }

        ////////////////////////////Mostrar//////////////////
        val rcvProductos = findViewById<RecyclerView>(R.id.rcvProductos)

        //Asignar layout al RecyclerView

        rcvProductos.layoutManager = LinearLayoutManager(this)

        //Función para obtener datos
        fun obtenerDatos(): List<dataClassProductos>{
            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultset = statement?.executeQuery("select * from tbProductos")!!

            val productos = mutableListOf<dataClassProductos>()
            while (resultset.next()){
                val nombre = resultset.getString("nombreProducto")
                val producto = dataClassProductos(nombre)
                productos.add(producto)
            }
            return productos
        }

        //Asignar adaptador
        CoroutineScope(Dispatchers.IO).launch {
            val productosDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val miAdapter = Adaptador(productosDB)
                rcvProductos.adapter = miAdapter
            }
        }

    }
}