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

class GestionPagosActivity : AppCompatActivity() {

    private lateinit var rvPagos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gestion_pagos)

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

        bottomNav.selectedItemId = R.id.nav_pagos

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

        rvPagos =
            findViewById(R.id.rvPagos)

        rvPagos.layoutManager =
            LinearLayoutManager(this)

        cargarPagos()
    }

    private fun cargarPagos() {

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
                        .getAdminPagos(
                            "Bearer $token"
                        )

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    val pagos =
                        response.body()!!.data ?: emptyList()

                    rvPagos.adapter =
                        PagoAdminAdapter(pagos)

                } else {

                    Toast.makeText(
                        this@GestionPagosActivity,
                        "Error ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@GestionPagosActivity,
                    "Error de conexión",
                    Toast.LENGTH_LONG
                ).show()

            }

        }
    }
}
