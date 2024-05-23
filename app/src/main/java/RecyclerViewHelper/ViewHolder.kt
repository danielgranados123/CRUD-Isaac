package RecyclerViewHelper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import daniel.granados.crudisaac2a.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.txtProductoCard)

    val imgEditar: ImageView = view.findViewById(R.id.imgEditar)
    val imgBorrar: ImageView = view.findViewById(R.id.imgBorrar)
}