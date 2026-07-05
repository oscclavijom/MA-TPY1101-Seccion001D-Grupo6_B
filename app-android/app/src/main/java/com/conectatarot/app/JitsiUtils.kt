package com.conectatarot.app

import android.content.Context
import android.content.Intent
import com.conectatarot.app.network.SesionItem

fun abrirVideollamada(context: Context, sesionId: Int) {
    val url = "https://meet.jit.si/ConectaTarot-Sesion$sesionId"
    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
    context.startActivity(intent)
}

fun ventanaVideollamada(sesion: SesionItem, minutosAntes: Int): Boolean {
    if (sesion.estado != "CONFIRMADA" || sesion.estadoPago != "PAGADO") return false
    return try {
        val fechaSesion = java.time.LocalDateTime.parse(sesion.fecha)
        val inicio = fechaSesion.minusMinutes(minutosAntes.toLong())
        val fin = fechaSesion.plusMinutes(sesion.duracionMinutos.toLong())
        val ahora = java.time.LocalDateTime.now()
        ahora.isAfter(inicio) && ahora.isBefore(fin)
    } catch (e: Exception) { false }
}

fun esSesionFutura(sesion: SesionItem): Boolean {
    if (sesion.estado != "CONFIRMADA") return false
    return try {
        val fechaSesion = java.time.LocalDateTime.parse(sesion.fecha)
        fechaSesion.isAfter(java.time.LocalDateTime.now())
    } catch (e: Exception) { false }
}

fun sesionCompletada(sesion: SesionItem): Boolean {
    if (sesion.estado != "CONFIRMADA") return false
    return try {
        val fechaSesion = java.time.LocalDateTime.parse(sesion.fecha)
        val fin = fechaSesion.plusMinutes(sesion.duracionMinutos.toLong())
        fin.isBefore(java.time.LocalDateTime.now())
    } catch (e: Exception) { false }
}

fun sesionNoComenzada(sesion: SesionItem): Boolean {
    return try {
        val fechaSesion = java.time.LocalDateTime.parse(sesion.fecha)
        fechaSesion.isAfter(java.time.LocalDateTime.now())
    } catch (e: Exception) { false }
}
