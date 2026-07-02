package com.conectatarot.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PagosTarotistaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos_tarotista)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        setupTarotistaBottomNavigation(bottomNav)
        bottomNav.selectedItemId = R.id.nav_pagos
    }
}
