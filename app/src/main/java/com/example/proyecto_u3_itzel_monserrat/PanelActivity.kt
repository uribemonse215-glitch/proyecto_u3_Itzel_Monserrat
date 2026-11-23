package com.example.proyecto_u3_itzel_monserrat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_u3_itzel_monserrat.data.Pedido
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityPanelBinding
import com.example.proyecto_u3_itzel_monserrat.ui.adapter.PedidoAdapter
import com.google.firebase.firestore.FirebaseFirestore

class PanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelBinding
    private lateinit var pedidoAdapter: PedidoAdapter
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var usuarioActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioActual = intent.getStringExtra("USUARIO_NOMBRE").orEmpty()
        if (usuarioActual.isNotEmpty()) {
            binding.tvBienvenida.text = "Hola, $usuarioActual"
        }

        configurarBiometriaSalida()

        pedidoAdapter = PedidoAdapter(
            pedidos = mutableListOf(),
            onItemClick = { pedido ->
                val intent = Intent(this, FormPedidoActivity::class.java).apply {
                    putExtra("PEDIDO_ID", pedido.id)
                    putExtra("PEDIDO_PRODUCTO", pedido.producto)
                    putExtra("PEDIDO_DOMICILIO", pedido.domicilio)
                    putExtra("PEDIDO_PIEZAS", pedido.piezas)
                    putExtra("PEDIDO_TOTAL", pedido.total)
                }
                startActivity(intent)
            },
            onItemLongClick = { pedido ->
                db.collection("pedidos").document(pedido.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al eliminar pedido", Toast.LENGTH_SHORT).show()
                    }
            }
        )

        binding.rvPedidosPanel.layoutManager = LinearLayoutManager(this)
        binding.rvPedidosPanel.adapter = pedidoAdapter

        binding.btnNavCitas.setOnClickListener {
            startActivity(Intent(this, CitasActivity::class.java))
        }

        binding.btnNavPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        binding.btnNavCuotas.setOnClickListener {
            startActivity(Intent(this, CuotasActivity::class.java))
        }

        binding.btnCerrarSesion.setOnClickListener {
            if (puedeUsarBiometria()) {
                biometricPrompt.authenticate(promptInfo)
            } else {
                volverAInicioAutenticacion()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                volverAInicioAutenticacion()
            }
        })

        escucharPedidos()
    }

    private fun configurarBiometriaSalida() {
        biometricPrompt = BiometricPrompt(
            this,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val mensaje = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON ->
                            "Salida cancelada."
                        BiometricPrompt.ERROR_LOCKOUT ->
                            "Demasiados intentos fallidos. Espera unos segundos."
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                            "Sensor bloqueado temporalmente por seguridad."
                        else -> "Error al verificar huella: $errString"
                    }
                    Toast.makeText(applicationContext, mensaje, Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Sesión cerrada",
                        Toast.LENGTH_SHORT
                    ).show()
                    volverAInicioAutenticacion()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Huella no reconocida",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirmar salida")
            .setSubtitle("Usa tu huella digital para cerrar sesión")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun puedeUsarBiometria(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val estado = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return when (estado) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }

    private fun volverAInicioAutenticacion() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun escucharPedidos() {
        db.collection("pedidos").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, "Error al cargar pedidos", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            val lista = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Pedido::class.java)?.apply { id = doc.id }
            }.orEmpty()
            pedidoAdapter.actualizarLista(lista)
        }
    }
}