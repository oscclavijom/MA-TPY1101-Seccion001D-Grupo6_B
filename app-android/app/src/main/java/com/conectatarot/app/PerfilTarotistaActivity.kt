package com.conectatarot.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.conectatarot.app.network.DisponibilidadRequest
import com.conectatarot.app.network.EditarPerfilTarotistaRequest
import com.conectatarot.app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PerfilTarotistaActivity : AppCompatActivity() {

    private val checkboxesEspecialidades = mutableListOf<CheckBox>()
    private var especialidadesCargadas = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_tarotista)

        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val idUsuario = 1

        val etNombrePro = findViewById<EditText>(R.id.etEditNombrePro)
        val etDescripcion = findViewById<EditText>(R.id.etEditDescripcion)
        val etPrecio = findViewById<EditText>(R.id.etEditPrecio)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarPerfilTarotista)
        val tvResultado = findViewById<TextView>(R.id.tvResultadoPerfilTarotista)
        val tvVolver = findViewById<TextView>(R.id.tvVolverPerfilTarotista)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        val llEspecialidades = findViewById<LinearLayout>(R.id.llEspecialidadesPerfil)
        val tvCargandoEspecialidades = findViewById<TextView>(R.id.tvCargandoEspecialidadesPerfil)

        val cbLunes = findViewById<CheckBox>(R.id.cbLunesPerfil)
        val cbMartes = findViewById<CheckBox>(R.id.cbMartesPerfil)
        val cbMiercoles = findViewById<CheckBox>(R.id.cbMiercolesPerfil)
        val cbJueves = findViewById<CheckBox>(R.id.cbJuevesPerfil)
        val cbViernes = findViewById<CheckBox>(R.id.cbViernesPerfil)
        val cbSabado = findViewById<CheckBox>(R.id.cbSabadoPerfil)
        val cbDomingo = findViewById<CheckBox>(R.id.cbDomingoPerfil)

        val etHoraInicio = findViewById<EditText>(R.id.etHoraInicioPerfil)
        val etHoraFin = findViewById<EditText>(R.id.etHoraFinPerfil)

        // Cargar datos guardados
        etNombrePro.setText(prefs.getString("nombreProfesional", "") ?: "")
        etDescripcion.setText(prefs.getString("descripcion", "") ?: "")
        etPrecio.setText(prefs.getString("precioBase", "") ?: "")

        setupTarotistaBottomNavigation(bottomNav)
        bottomNav.selectedItemId = R.id.nav_perfil

        tvVolver.setOnClickListener { finish() }

        cargarEspecialidades(llEspecialidades, tvCargandoEspecialidades, tvResultado, btnGuardar)

        etHoraInicio.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            android.app.TimePickerDialog(
                this,
                { _, hour, minute ->
                    etHoraInicio.setText(String.format("%02d:%02d", hour, minute))
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
                    etHoraFin.setText(String.format("%02d:%02d", hour, minute))
                },
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }

        btnGuardar.setOnClickListener {
            val nombrePro = etNombrePro.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()
            val precioStr = etPrecio.text.toString().trim()

            if (nombrePro.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                tvResultado.text = "Por favor completa todos los campos"
                tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                return@setOnClickListener
            }

            val precio = precioStr.toDoubleOrNull() ?: run {
                tvResultado.text = "El precio debe ser un número válido"
                tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                return@setOnClickListener
            }

            val especialidades = checkboxesEspecialidades
                .filter { it.isChecked }
                .map { it.tag as Int }

            val horaInicio = etHoraInicio.text.toString().trim()
            val horaFin = etHoraFin.text.toString().trim()

            val disponibilidades = mutableListOf<DisponibilidadRequest>()

            if (horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {
                val inicio = try {
                    java.time.LocalTime.parse(horaInicio)
                } catch (e: Exception) {
                    tvResultado.text = "Hora de inicio inválida"
                    tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                    return@setOnClickListener
                }

                val fin = try {
                    java.time.LocalTime.parse(horaFin)
                } catch (e: Exception) {
                    tvResultado.text = "Hora de fin inválida"
                    tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                    return@setOnClickListener
                }

                if (!fin.isAfter(inicio)) {
                    tvResultado.text = "La hora de fin debe ser mayor que la hora de inicio"
                    tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                    return@setOnClickListener
                }

                if (cbLunes.isChecked)
                    disponibilidades.add(DisponibilidadRequest("MONDAY", horaInicio, horaFin))
                if (cbMartes.isChecked)
                    disponibilidades.add(DisponibilidadRequest("TUESDAY", horaInicio, horaFin))
                if (cbMiercoles.isChecked)
                    disponibilidades.add(DisponibilidadRequest("WEDNESDAY", horaInicio, horaFin))
                if (cbJueves.isChecked)
                    disponibilidades.add(DisponibilidadRequest("THURSDAY", horaInicio, horaFin))
                if (cbViernes.isChecked)
                    disponibilidades.add(DisponibilidadRequest("FRIDAY", horaInicio, horaFin))
                if (cbSabado.isChecked)
                    disponibilidades.add(DisponibilidadRequest("SATURDAY", horaInicio, horaFin))
                if (cbDomingo.isChecked)
                    disponibilidades.add(DisponibilidadRequest("SUNDAY", horaInicio, horaFin))
            }

            btnGuardar.isEnabled = false
            btnGuardar.text = "Guardando..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.editarPerfilTarotista(
                        "Bearer $token",
                        idUsuario,
                        EditarPerfilTarotistaRequest(
                            nombrePro,
                            descripcion,
                            precio,
                            especialidades,
                            disponibilidades
                        )
                    )
                    if (response.isSuccessful) {
                        prefs.edit()
                            .putString("nombreProfesional", nombrePro)
                            .putString("descripcion", descripcion)
                            .putString("precioBase", precioStr)
                            .apply()
                        tvResultado.text = "✅ Perfil actualizado correctamente"
                        tvResultado.setTextColor(getColor(android.R.color.holo_green_light))
                        btnGuardar.text = "Guardado ✓"
                    } else {
                        tvResultado.text = "❌ Error al actualizar"
                        tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                        btnGuardar.isEnabled = true
                        btnGuardar.text = "Guardar cambios"
                    }
                } catch (e: Exception) {
                    tvResultado.text = "❌ Error de conexión"
                    tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                    btnGuardar.isEnabled = true
                    btnGuardar.text = "Guardar cambios"
                }
            }
        }
    }

    private fun cargarEspecialidades(
        llEspecialidades: LinearLayout,
        tvCargando: TextView,
        tvResultado: TextView,
        btnGuardar: Button
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getEspecialidades()

                if (response.isSuccessful && response.body()?.data != null) {
                    val especialidades = response.body()!!.data!!

                    llEspecialidades.removeAllViews()
                    checkboxesEspecialidades.clear()

                    for (esp in especialidades) {
                        val checkBox = LayoutInflater.from(this@PerfilTarotistaActivity)
                            .inflate(R.layout.item_checkbox_registro, llEspecialidades, false) as CheckBox
                        checkBox.text = esp.nombre
                        checkBox.tag = esp.id
                        checkboxesEspecialidades.add(checkBox)
                        llEspecialidades.addView(checkBox)
                    }

                    tvCargando.visibility = View.GONE
                    especialidadesCargadas = true

                    if (especialidades.isEmpty()) {
                        tvResultado.text = "No hay especialidades disponibles"
                        tvResultado.setTextColor(getColor(android.R.color.holo_red_light))
                    }
                } else {
                    tvCargando.text = "Error al cargar especialidades"
                    tvCargando.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                tvCargando.text = "Error de conexión"
                tvCargando.visibility = View.VISIBLE
            }
        }
    }
}