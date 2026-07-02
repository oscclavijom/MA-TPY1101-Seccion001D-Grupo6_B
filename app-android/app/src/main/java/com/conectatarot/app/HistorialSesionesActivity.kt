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

class HistorialSesionesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_sesiones)

        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        val rvSesiones = findViewById<RecyclerView>(R.id.rvHistorialSesiones)
        val tvVolver = findViewById<TextView>(R.id.tvVolverHistorialSesiones)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyHistorialSesiones)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        rvSesiones.layoutManager = LinearLayoutManager(this)

        setupClienteBottomNavigation(bottomNav)
        bottomNav.selectedItemId = R.id.nav_historial

        tvVolver.setOnClickListener { finish() }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getMisSesionesHistorial("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val sesiones = response.body()!!.data ?: emptyList()
                    if (sesiones.isEmpty()) {
                        tvEmpty.visibility = android.view.View.VISIBLE
                        rvSesiones.visibility = android.view.View.GONE
                    } else {
                        tvEmpty.visibility = android.view.View.GONE
                        rvSesiones.visibility = android.view.View.VISIBLE
                        rvSesiones.adapter = HistorialSesionAdapter(sesiones)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistorialSesionesActivity, "Error al cargar historial", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
