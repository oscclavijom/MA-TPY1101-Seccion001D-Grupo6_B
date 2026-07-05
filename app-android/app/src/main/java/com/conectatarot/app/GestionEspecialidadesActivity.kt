package com.conectatarot.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conectatarot.app.network.ActualizarEspecialidadRequest
import com.conectatarot.app.network.CrearEspecialidadRequest
import com.conectatarot.app.network.EspecialidadAdmin
import com.conectatarot.app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class GestionEspecialidadesActivity : AppCompatActivity() {

    private lateinit var rvEspecialidades: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_especialidades)

        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        if (token.isNullOrEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_especialidades

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                R.id.nav_usuarios -> {
                    startActivity(Intent(this, GestionUsuariosActivity::class.java))
                    true
                }
                R.id.nav_tarotistas -> {
                    startActivity(Intent(this, GestionTarotistasActivity::class.java))
                    true
                }
                R.id.nav_pagos -> {
                    startActivity(Intent(this, GestionPagosActivity::class.java))
                    true
                }
                else -> true
            }
        }

        rvEspecialidades = findViewById(R.id.rvEspecialidades)
        rvEspecialidades.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnNuevaEspecialidad).setOnClickListener {
            mostrarDialogoCrear()
        }

        cargarEspecialidades()
    }

    private fun getToken(): String {
        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        return prefs.getString("token", "") ?: ""
    }

    private fun cargarEspecialidades() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getAdminEspecialidades("Bearer ${getToken()}")

                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!.data ?: emptyList()
                    rvEspecialidades.adapter = EspecialidadAdminAdapter(
                        lista,
                        onEditar = { especialidad -> mostrarDialogoEditar(especialidad) },
                        onEliminar = { id -> confirmarEliminar(id) }
                    )
                } else {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "Error ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionEspecialidadesActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarDialogoCrear() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_especialidad, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreEspecialidad)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionEspecialidad)

        AlertDialog.Builder(this, R.style.ThemeOverlay_GestionEspecialidades_AlertDialog)
            .setTitle("Nueva especialidad")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                if (nombre.isEmpty()) {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }

                val descripcion = etDescripcion.text.toString().trim()
                crearEspecialidad(nombre, descripcion.ifEmpty { null })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(especialidad: EspecialidadAdmin) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_especialidad, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreEspecialidad)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcionEspecialidad)
        val cbActiva = dialogView.findViewById<CheckBox>(R.id.cbActivaEspecialidad)

        cbActiva.visibility = android.view.View.VISIBLE
        etNombre.setText(especialidad.nombre ?: "")
        etDescripcion.setText(especialidad.descripcion ?: "")
        cbActiva.isChecked = especialidad.activa != false

        val id = especialidad.id ?: return

        AlertDialog.Builder(this, R.style.ThemeOverlay_GestionEspecialidades_AlertDialog)
            .setTitle("Editar especialidad")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                if (nombre.isEmpty()) {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }

                val descripcion = etDescripcion.text.toString().trim()
                actualizarEspecialidad(
                    id,
                    nombre,
                    descripcion.ifEmpty { null },
                    cbActiva.isChecked
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminar(id: Int) {
        AlertDialog.Builder(this, R.style.ThemeOverlay_GestionEspecialidades_AlertDialog)
            .setTitle("Eliminar especialidad")
            .setMessage("¿Estás seguro de eliminar esta especialidad?")
            .setPositiveButton("Eliminar") { _, _ -> eliminarEspecialidad(id) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearEspecialidad(nombre: String, descripcion: String?) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.crearEspecialidad(
                    "Bearer ${getToken()}",
                    CrearEspecialidadRequest(nombre, descripcion)
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "Especialidad creada",
                        Toast.LENGTH_LONG
                    ).show()
                    cargarEspecialidades()
                } else {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "Error al crear",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionEspecialidadesActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun actualizarEspecialidad(
        id: Int,
        nombre: String,
        descripcion: String?,
        activa: Boolean
    ) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.actualizarEspecialidad(
                    "Bearer ${getToken()}",
                    id,
                    ActualizarEspecialidadRequest(nombre, descripcion, activa)
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "Especialidad actualizada",
                        Toast.LENGTH_LONG
                    ).show()
                    cargarEspecialidades()
                } else {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "Error al actualizar",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionEspecialidadesActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun eliminarEspecialidad(id: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarEspecialidad(
                    "Bearer ${getToken()}",
                    id
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "Especialidad eliminada",
                        Toast.LENGTH_LONG
                    ).show()
                    cargarEspecialidades()
                } else {
                    Toast.makeText(
                        this@GestionEspecialidadesActivity,
                        "No se puede eliminar (puede tener tarotistas o sesiones asociadas)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionEspecialidadesActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
