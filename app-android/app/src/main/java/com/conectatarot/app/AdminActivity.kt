package com.conectatarot.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.conectatarot.app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin)

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

        val tvUsuarios =
            findViewById<TextView>(R.id.tvUsuarios)

        val tvTarotistas =
            findViewById<TextView>(R.id.tvTarotistas)

        val tvPendientes =
            findViewById<TextView>(R.id.tvPendientes)

        val tvSesiones =
            findViewById<TextView>(R.id.tvSesiones)

        val tvLogout =
            findViewById<TextView>(R.id.tvLogout)

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId =
            R.id.nav_dashboard

        bottomNav.setOnItemSelectedListener { item ->

            when(item.itemId) {

                R.id.nav_usuarios -> {

                    startActivity(
                        Intent(
                            this,
                            GestionUsuariosActivity::class.java
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

        tvLogout.setOnClickListener {
            logout()
        }

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.instance
                        .getAdminDashboard(
                            "Bearer $token"
                        )

                if (
                    response.isSuccessful &&
                    response.body() != null &&
                    response.body()!!.data != null
                ) {

                    val dashboard =
                        response.body()!!.data!!

                    tvUsuarios.text =
                        "Usuarios: ${dashboard.usuarios}"

                    tvTarotistas.text =
                        "Tarotistas: ${dashboard.tarotistas}"

                    tvPendientes.text =
                        "Pendientes: ${dashboard.pendientes}"

                    tvSesiones.text =
                        "Sesiones: ${dashboard.sesiones}"

                } else {

                    Toast.makeText(
                        this@AdminActivity,
                        "Error ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@AdminActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}