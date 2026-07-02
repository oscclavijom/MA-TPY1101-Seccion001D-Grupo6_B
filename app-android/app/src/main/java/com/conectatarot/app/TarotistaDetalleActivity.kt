package com.conectatarot.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.conectatarot.app.network.Disponibilidad

class TarotistaDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarotista_detalle)

        val nombre = intent.getStringExtra("nombre") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        val precio = intent.getDoubleExtra("precio", 0.0)
        val especialidades =
            intent.getStringArrayListExtra("especialidades")
                ?: arrayListOf()
        val disponibilidades =
            intent.getSerializableExtra("disponibilidades") as? ArrayList<Disponibilidad>
                ?: arrayListOf()

        findViewById<TextView>(R.id.tvDetNombre).text = "🌙 $nombre"
        findViewById<TextView>(R.id.tvDetDescripcion).text = descripcion
        findViewById<TextView>(R.id.tvDetPrecio).text = "$ $precio / hora"
        findViewById<TextView>(R.id.tvDetEspecialidades).text =
            especialidades.joinToString(", ")

        // Mostrar disponibilidad traducida y formateada
        val tvDisponibilidad = findViewById<TextView>(R.id.tvDetDisponibilidad)
        if (disponibilidades.isEmpty()) {
            tvDisponibilidad.text = "Sin disponibilidad registrada"
        } else {
            val disponibilidadTexto = disponibilidades.joinToString("\n") { disp ->
                val diaEspanol = traducirDia(disp.diaSemana)
                "$diaEspanol: ${disp.horaInicio} - ${disp.horaFin}"
            }
            tvDisponibilidad.text = disponibilidadTexto
        }

        findViewById<Button>(R.id.btnAgendar).setOnClickListener {

            val intentAgendar = Intent(
                this,
                AgendarActivity::class.java
            ).apply {

                putExtra(
                    "tarotistaId",
                    intent.getIntExtra("tarotistaId", 0)
                )

                putExtra(
                    "nombre",
                    intent.getStringExtra("nombre")
                )

                putStringArrayListExtra(
                    "especialidades",
                    especialidades
                )
            }

            startActivity(intentAgendar)
        }

        findViewById<TextView>(R.id.tvVolver).setOnClickListener {
            finish()
        }
    }

    private fun traducirDia(diaIngles: String): String {
        return when (diaIngles.uppercase()) {
            "MONDAY" -> "Lunes"
            "TUESDAY" -> "Martes"
            "WEDNESDAY" -> "Miércoles"
            "THURSDAY" -> "Jueves"
            "FRIDAY" -> "Viernes"
            "SATURDAY" -> "Sábado"
            "SUNDAY" -> "Domingo"
            else -> diaIngles
        }
    }
}