package com.conectatarot.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.conectatarot.app.network.EditarPerfilTarotistaRequest
import com.conectatarot.app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PerfilTarotistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_tarotista)

        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        val etNombrePro = findViewById<EditText>(R.id.etEditNombrePro)
        val etDescripcion = findViewById<EditText>(R.id.etEditDescripcion)
        val etPrecio = findViewById<EditText>(R.id.etEditPrecio)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarPerfilTarotista)
        val tvResultado = findViewById<TextView>(R.id.tvResultadoPerfilTarotista)
        val tvVolver = findViewById<TextView>(R.id.tvVolverPerfilTarotista)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        setupTarotistaBottomNavigation(bottomNav)
        bottomNav.selectedItemId = R.id.nav_perfil

        tvVolver.setOnClickListener { finish() }

        // Cargar datos desde SharedPreferences primero
        val nombreProGuardado = prefs.getString("nombreProfesional", "") ?: ""
        val descripcionGuardada = prefs.getString("descripcion", "") ?: ""
        val precioGuardado = prefs.getString("precioBase", "") ?: ""

        etNombrePro.setText(nombreProGuardado)
        etDescripcion.setText(descripcionGuardada)
        etPrecio.setText(precioGuardado)

        // Si SharedPreferences está vacío, cargar desde backend
        if (nombreProGuardado.isEmpty() || descripcionGuardada.isEmpty() || precioGuardado.isEmpty()) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.obtenerMiPerfilTarotista("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        val perfil = response.body()!!
                        etNombrePro.setText(perfil.nombreProfesional)
                        etDescripcion.setText(perfil.descripcion ?: "")
                        etPrecio.setText(perfil.precioBase?.toString() ?: "")
                        
                        // Guardar en SharedPreferences para futuros usos
                        prefs.edit()
                            .putString("nombreProfesional", perfil.nombreProfesional)
                            .putString("descripcion", perfil.descripcion ?: "")
                            .putString("precioBase", perfil.precioBase?.toString() ?: "")
                            .apply()
                    }
                } catch (e: Exception) {
                    // Silencioso: mantener valores de SharedPreferences si falla backend
                }
            }
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

            btnGuardar.isEnabled = false
            btnGuardar.text = "Guardando..."

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.editarMiPerfilTarotista(
                        "Bearer $token",
                        EditarPerfilTarotistaRequest(nombrePro, descripcion, precio)
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
}