package com.conectatarot.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupClienteBottomNavigation(bottomNav: BottomNavigationView) {
    bottomNav.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_inicio -> {
                if (this !is ClienteActivity) {
                    startActivity(Intent(this, ClienteActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_sesiones -> {
                if (this !is MisSesionesActivity) {
                    startActivity(Intent(this, MisSesionesActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_historial -> {
                if (this !is HistorialSesionesActivity) {
                    startActivity(Intent(this, HistorialSesionesActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_pagos -> {
                if (this !is HistorialPagosActivity) {
                    startActivity(Intent(this, HistorialPagosActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_perfil -> {
                if (this !is PerfilActivity) {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    finish()
                }
                true
            }
            else -> true
        }
    }
}

fun AppCompatActivity.setupTarotistaBottomNavigation(bottomNav: BottomNavigationView) {
    bottomNav.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_agenda -> {
                if (this !is TarotistaHomeActivity) {
                    startActivity(Intent(this, TarotistaHomeActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_historial -> {
                if (this !is HistorialTarotistaActivity) {
                    startActivity(Intent(this, HistorialTarotistaActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_pagos -> {
                if (this !is PagosTarotistaActivity) {
                    startActivity(Intent(this, PagosTarotistaActivity::class.java))
                    finish()
                }
                true
            }
            R.id.nav_perfil -> {
                if (this !is PerfilTarotistaActivity) {
                    startActivity(Intent(this, PerfilTarotistaActivity::class.java))
                    finish()
                }
                true
            }
            else -> true
        }
    }
}
