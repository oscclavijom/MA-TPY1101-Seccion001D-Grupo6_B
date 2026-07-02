package com.conectatarot.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.conectatarot.app.network.SesionItem

class HistorialSesionAdapter(
    private val sesiones: List<SesionItem>,
    private val isTarotistaView: Boolean = false
) : RecyclerView.Adapter<HistorialSesionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTarotista: TextView = view.findViewById(R.id.tvHistorialTarotista)
        val tvFecha: TextView = view.findViewById(R.id.tvHistorialFecha)
        val tvEstado: TextView = view.findViewById(R.id.tvHistorialEstado)
        val tvEspecialidad: TextView = view.findViewById(R.id.tvHistorialEspecialidad)
        val tvPrecio: TextView = view.findViewById(R.id.tvHistorialPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_sesion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val s = sesiones[position]
        val nombre = if (isTarotistaView) s.nombreCliente else s.nombreTarotista
        holder.tvTarotista.text = "🌙 ${nombre ?: "N/A"}"
        holder.tvFecha.text = "📅 ${s.fecha.take(16).replace("T", " ")}"
        holder.tvEspecialidad.text = "🔮 ${s.especialidad}"
        holder.tvPrecio.text = "$ ${s.precioTotal.toInt()}"

        val (color, texto) = when (s.estado) {
            "CANCELADA" -> Pair("#e74c3c", "❌ Cancelada")
            "RECHAZADA" -> Pair("#e74c3c", "❌ Rechazada")
            "CONFIRMADA" -> Pair("#3498db", "✔ Realizada")
            else -> Pair("#9b59b6", s.estado)
        }
        holder.tvEstado.text = texto
        holder.tvEstado.setTextColor(android.graphics.Color.parseColor(color))
    }

    override fun getItemCount() = sesiones.size
}
