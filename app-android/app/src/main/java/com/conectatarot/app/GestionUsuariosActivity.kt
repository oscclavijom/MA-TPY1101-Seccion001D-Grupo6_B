package com.conectatarot.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conectatarot.app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var rvUsuarios: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gestion_usuarios)

        val prefs =
            getSharedPreferences(
                "conectatarot",
                MODE_PRIVATE
            )

        val token =
            prefs.getString(
                "token",
                null
            )

        if (token.isNullOrEmpty()) {

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()

            return
        }

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.nav_usuarios

        bottomNav.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_dashboard -> {

                    startActivity(
                        Intent(
                            this,
                            AdminActivity::class.java
                        )
                    )

                    true
                }

                R.id.nav_tarotistas -> {

                    startActivity(
                        Intent(
                            this,
                            GestionTarotistasActivity::class.java
                        )
                    )

                    true
                }

                R.id.nav_pagos -> {

                    startActivity(
                        Intent(
                            this,
                            GestionPagosActivity::class.java
                        )
                    )

                    true
                }

                R.id.nav_especialidades -> {

                    startActivity(
                        Intent(
                            this,
                            GestionEspecialidadesActivity::class.java
                        )
                    )

                    true
                }

                else -> true
            }
        }

        rvUsuarios =
            findViewById(R.id.rvUsuarios)

        rvUsuarios.layoutManager =
            LinearLayoutManager(this)

        cargarUsuarios()
    }

    private fun cargarUsuarios() {

        val prefs =
            getSharedPreferences(
                "conectatarot",
                MODE_PRIVATE
            )

        val token =
            prefs.getString(
                "token",
                ""
            ) ?: ""

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.instance
                        .getAdminUsuarios(
                            "Bearer $token"
                        )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    val usuarios =
                        response.body()!!.data ?: emptyList()

                    rvUsuarios.adapter =
                        UsuarioAdapter(
                            usuarios,
                            onBloquear = { id ->
                                cambiarEstadoUsuario(id, bloquear = true)
                            },
                            onDesbloquear = { id ->
                                cambiarEstadoUsuario(id, bloquear = false)
                            }
                        )

                } else {

                    Toast.makeText(
                        this@GestionUsuariosActivity,
                        "Error ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@GestionUsuariosActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()

            }

        }
    }

    private fun cambiarEstadoUsuario(
        id: Int,
        bloquear: Boolean
    ) {

        val prefs =
            getSharedPreferences(
                "conectatarot",
                MODE_PRIVATE
            )

        val token =
            prefs.getString(
                "token",
                ""
            ) ?: ""

        lifecycleScope.launch {

            try {

                val response =
                    if (bloquear) {
                        RetrofitClient.instance.bloquearUsuario(
                            "Bearer $token",
                            id
                        )
                    } else {
                        RetrofitClient.instance.desbloquearUsuario(
                            "Bearer $token",
                            id
                        )
                    }

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@GestionUsuariosActivity,
                        if (bloquear) "Usuario bloqueado" else "Usuario desbloqueado",
                        Toast.LENGTH_LONG
                    ).show()

                    cargarUsuarios()

                } else {

                    Toast.makeText(
                        this@GestionUsuariosActivity,
                        "Error al cambiar estado",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@GestionUsuariosActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()

            }

        }
    }
}
