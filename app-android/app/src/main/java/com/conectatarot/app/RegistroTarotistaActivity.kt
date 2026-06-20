package com.conectatarot.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.conectatarot.app.network.RegistroTarotistaRequest
import com.conectatarot.app.network.RetrofitClient
import kotlinx.coroutines.launch
import com.conectatarot.app.network.DisponibilidadRequest

class RegistroTarotistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_tarotista)

        val etNombre = findViewById<EditText>(R.id.etNombreTarotista)
        val etEmail = findViewById<EditText>(R.id.etEmailTarotista)
        val etPassword = findViewById<EditText>(R.id.etPasswordTarotista)
        val etNombrePro = findViewById<EditText>(R.id.etNombreProfesional)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcionTarotista)
        val etPrecio = findViewById<EditText>(R.id.etPrecioTarotista)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrarTarotista)
        val tvResultado = findViewById<TextView>(R.id.tvResultadoTarotista)
        val tvVolver = findViewById<TextView>(R.id.tvVolverTarotista)
        val cbTarotGeneral = findViewById<CheckBox>(R.id.cbTarotGeneral)
        val cbTarotEgipcio = findViewById<CheckBox>(R.id.cbTarotEgipcio)
        val cbAstrologia = findViewById<CheckBox>(R.id.cbAstrologia)
        val cbAmor = findViewById<CheckBox>(R.id.cbAmor)
        val cbNumerologia = findViewById<CheckBox>(R.id.cbNumerologia)
        val cbLunes = findViewById<CheckBox>(R.id.cbLunes)
        val cbMartes = findViewById<CheckBox>(R.id.cbMartes)
        val cbMiercoles = findViewById<CheckBox>(R.id.cbMiercoles)
        val cbJueves = findViewById<CheckBox>(R.id.cbJueves)
        val cbViernes = findViewById<CheckBox>(R.id.cbViernes)
        val cbSabado = findViewById<CheckBox>(R.id.cbSabado)
        val cbDomingo = findViewById<CheckBox>(R.id.cbDomingo)

        val etHoraInicio = findViewById<EditText>(R.id.etHoraInicio)
        val etHoraFin = findViewById<EditText>(R.id.etHoraFin)

        tvVolver.setOnClickListener { finish() }

        etHoraInicio.setOnClickListener {

            val cal = java.util.Calendar.getInstance()

            android.app.TimePickerDialog(
                this,
                { _, hour, minute ->
                    etHoraInicio.setText(
                        String.format("%02d:%02d", hour, minute)
                    )
                },
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }

        etHoraFin.setOnClickListener {

            val cal = java.util.Calendar.getInstance()

            android.app.TimePickerDialog(
                this,
                { _, hour, minute ->
                    etHoraFin.setText(
                        String.format("%02d:%02d", hour, minute)
                    )
                },
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val nombrePro = etNombrePro.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val precioStr = etPrecio.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() ||
                nombrePro.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                tvResultado.text = "Por favor completa todos los campos"
                tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                return@setOnClickListener
            }

            val precio = precioStr.toDoubleOrNull() ?: run {
                tvResultado.text = "El precio debe ser un número válido"
                tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                return@setOnClickListener
            }

            val especialidades = mutableListOf<Int>()

            if (cbTarotGeneral.isChecked) especialidades.add(1)
            if (cbTarotEgipcio.isChecked) especialidades.add(2)
            if (cbAstrologia.isChecked) especialidades.add(3)
            if (cbAmor.isChecked) especialidades.add(4)
            if (cbNumerologia.isChecked) especialidades.add(5)

            if (especialidades.isEmpty()) {
                tvResultado.text = "Selecciona al menos una especialidad"
                tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                return@setOnClickListener
            }

            val disponibilidades = mutableListOf<DisponibilidadRequest>()

            val horaInicio = etHoraInicio.text.toString().trim()
            val horaFin = etHoraFin.text.toString().trim()

            val inicio =
                java.time.LocalTime.parse(horaInicio)

            val fin =
                java.time.LocalTime.parse(horaFin)

            if (!fin.isAfter(inicio)) {

                tvResultado.text =
                    "La hora de fin debe ser mayor que la hora de inicio"

                tvResultado.setTextColor(
                    getColor(android.R.color.holo_red_light)
                )

                return@setOnClickListener
            }

            if (horaInicio.isEmpty() || horaFin.isEmpty()) {

                tvResultado.text = "Selecciona horario de atención"

                tvResultado.setTextColor(
                    getColor(android.R.color.holo_red_light)
                )

                return@setOnClickListener
            }

            if (cbLunes.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "MONDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (cbMartes.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "TUESDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (cbMiercoles.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "WEDNESDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (cbJueves.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "THURSDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (cbViernes.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "FRIDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (cbSabado.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "SATURDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (cbDomingo.isChecked)
                disponibilidades.add(
                    DisponibilidadRequest(
                        "SUNDAY",
                        horaInicio,
                        horaFin
                    )
                )

            if (disponibilidades.isEmpty()) {

                tvResultado.text =
                    "Selecciona al menos un día disponible"

                tvResultado.setTextColor(
                    getColor(android.R.color.holo_red_light)
                )

                return@setOnClickListener
            }

            btnRegistrar.isEnabled = false
            btnRegistrar.text = "Registrando..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.registrarTarotista(
                        RegistroTarotistaRequest(
                            nombre,
                            email,
                            password,
                            nombrePro,
                            descripcion,
                            precio,
                            especialidades,
                            disponibilidades
                        )
                    )
                    if (response.isSuccessful) {
                        tvResultado.text = "✅ Registro exitoso. Tu cuenta está pendiente de aprobación."
                        tvResultado.setTextColor(getColor(android.R.color.holo_green_light))
                        btnRegistrar.text = "Registrado"
                    } else {
                        tvResultado.text = "❌ El email ya está registrado"
                        tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                        btnRegistrar.isEnabled = true
                        btnRegistrar.text = "Registrarme como Tarotista"
                    }
                } catch (e: Exception) {
                    tvResultado.text = "❌ Error de conexión"
                    tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                    btnRegistrar.isEnabled = true
                    btnRegistrar.text = "Registrarme como Tarotista"
                }
            }
        }
    }
}