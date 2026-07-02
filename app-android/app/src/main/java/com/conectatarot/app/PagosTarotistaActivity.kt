package com.conectatarot.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conectatarot.app.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class PagosTarotistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos_tarotista)

        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        val rvPagos = findViewById<RecyclerView>(R.id.rvPagosTarotista)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyPagosTarotista)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        rvPagos.layoutManager = LinearLayoutManager(this)

        setupTarotistaBottomNavigation(bottomNav)
        bottomNav.selectedItemId = R.id.nav_pagos

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getPagosTarotistaHistorial("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val pagos = response.body()!!.data ?: emptyList()
                    if (pagos.isEmpty()) {
                        tvEmpty.visibility = android.view.View.VISIBLE
                        rvPagos.visibility = android.view.View.GONE
                    } else {
                        tvEmpty.visibility = android.view.View.GONE
                        rvPagos.visibility = android.view.View.VISIBLE
                        rvPagos.adapter = PagoAdapter(pagos, isTarotistaView = true)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@PagosTarotistaActivity, "Error al cargar historial de pagos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
