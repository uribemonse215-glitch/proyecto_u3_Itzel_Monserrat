package com.example.proyecto_u3_itzel_monserrat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val mensaje = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON ->
                            "Autenticación cancelada por el usuario."
                        BiometricPrompt.ERROR_LOCKOUT ->
                            "Demasiados intentos fallidos. Espera unos segundos."
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                            "Sensor bloqueado temporalmente por seguridad."
                        else -> "Error de autenticación: $errString"
                    }
                    binding.tvEstadoHuella.text = mensaje
                    Toast.makeText(applicationContext, mensaje, Toast.LENGTH_LONG).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val usuario = binding.etUsuario.text.toString().trim()
                    Toast.makeText(
                        applicationContext,
                        "¡Autenticación exitosa!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intento = Intent(this@MainActivity, PanelActivity::class.java).apply {
                        putExtra("USUARIO_NOMBRE", usuario)
                    }
                    startActivity(intento)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val msg = "Huella no reconocida"
                    binding.tvEstadoHuella.text = msg
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Inicio de sesión biométrico")
            .setSubtitle("Usa tu huella digital para entrar")
            .setNegativeButtonText("Cancelar")
            .build()

        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            if (usuario.isEmpty()) {
                binding.etUsuario.error = "Ingresa tu usuario"
                return@setOnClickListener
            }
            if (puedeUsarBiometria()) {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    private fun puedeUsarBiometria(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val estado = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return when (estado) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                mostrarMensaje("Este teléfono no tiene sensor de huella.")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                mostrarMensaje("El sensor no está disponible por ahora.")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                mostrarMensaje("No hay huellas registradas. Agrega una en Ajustes.")
                false
            }
            else -> {
                mostrarMensaje("La biometría no está disponible.")
                false
            }
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        binding.tvEstadoHuella.text = mensaje
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}