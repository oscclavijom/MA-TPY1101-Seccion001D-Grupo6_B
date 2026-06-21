package com.conectatarot.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.conectatarot.app.network.LoginRequest
import com.conectatarot.app.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvIrRegistro = findViewById<TextView>(R.id.tvIrRegistro)
        val tvIrRegistroTarotista = findViewById<TextView>(R.id.tvIrRegistroTarotista)



        tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        tvIrRegistroTarotista.setOnClickListener {
            startActivity(Intent(this, RegistroTarotistaActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validación básica para la entrega de la U (que no esté vacío)
            if (email.isEmpty() || password.isEmpty()) {
                tvError.text = "Completa todos los campos"
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Iniciando..."

            // 💡 Petición directa a tu Spring Boot con Retrofit
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(LoginRequest(email, password))
                    if (response.isSuccessful) {
                        val body = response.body()!!
                        val prefs = getSharedPreferences("conectatarot", MODE_PRIVATE)

                        // Guardamos localmente los datos de tu tabla MySQL devueltos por Spring Boot
                        prefs.edit()
                            .putString("token", body.token)
                            .putString("nombre", body.nombre)
                            .putString("rol", body.rol)
                            .putString("email", body.email)
                            .putInt("idUsuario", body.idUsuario)
                            .apply()

                        val rol = body.rol

                        when (rol) {

                            "ADMIN" -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        AdminActivity::class.java
                                    )
                                )
                            }

                            "TAROTISTA" -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        TarotistaHomeActivity::class.java
                                    )
                                )
                            }

                            else -> {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        ClienteActivity::class.java
                                    )
                                )
                            }
                        }

                        finish()
                    } else {
                        tvError.text = "Credenciales incorrectas"
                        btnLogin.isEnabled = true
                        btnLogin.text = "Iniciar sesión"
                    }
                } catch (e: Exception) {
                    // Muestra error si tu servidor Spring Boot local o Railway está apagado
                    tvError.text = "Error de conexión con el servidor"
                    btnLogin.isEnabled = true
                    btnLogin.text = "Iniciar sesión"
                }
            }
        }
    }
}
