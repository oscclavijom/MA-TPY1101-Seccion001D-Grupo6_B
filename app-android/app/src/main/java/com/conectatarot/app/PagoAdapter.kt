package com.conectatarot.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.conectatarot.app.network.SesionItem

class PagoAdapter(
    private val pagos: List<SesionItem>
) : RecyclerView.Adapter<PagoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTarotista: TextView = view.findViewById(R.id.tvPagoTarotista)
        val tvFecha: TextView = view.findViewById(R.id.tvPagoFecha)
        val tvEstadoPago: TextView = view.findViewById(R.id.tvPagoEstado)
        val tvEspecialidad: TextView = view.findViewById(R.id.tvPagoEspecialidad)
        val tvMonto: TextView = view.findViewById(R.id.tvPagoMonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pago, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = pagos[position]
        holder.tvTarotista.text = "🌙 ${p.nombreTarotista}"
        holder.tvFecha.text = "📅 ${p.fecha.take(16).replace("T", " ")}"
        holder.tvEspecialidad.text = "🔮 ${p.especialidad}"
        holder.tvMonto.text = "$ ${p.precioTotal.toInt()}"

        val (color, texto) = when (p.estadoPago) {
            "PAGADO" -> Pair("#27ae60", "✅ Pagado")
            else -> Pair("#9b59b6", p.estadoPago ?: "Desconocido")
        }
        holder.tvEstadoPago.text = texto
        holder.tvEstadoPago.setTextColor(android.graphics.Color.parseColor(color))
    }

    override fun getItemCount() = pagos.size
}
