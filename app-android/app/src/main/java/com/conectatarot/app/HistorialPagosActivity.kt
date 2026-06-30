package com.conectatarot.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conectatarot.app.network.RetrofitClient
import kotlinx.coroutines.launch

class HistorialPagosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_pagos)

        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        val rvPagos = findViewById<RecyclerView>(R.id.rvHistorialPagos)
        val tvVolver = findViewById<TextView>(R.id.tvVolverHistorialPagos)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyHistorialPagos)

        rvPagos.layoutManager = LinearLayoutManager(this)

        tvVolver.setOnClickListener { finish() }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMisPagos("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val pagos = response.body()!!.data ?: emptyList()
                    if (pagos.isEmpty()) {
                        tvEmpty.visibility = android.view.View.VISIBLE
                        rvPagos.visibility = android.view.View.GONE
                    } else {
                        tvEmpty.visibility = android.view.View.GONE
                        rvPagos.visibility = android.view.View.VISIBLE
                        rvPagos!!.adapter = PagoAdapter(pagos)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistorialPagosActivity, "Error al cargar historial de pagos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
