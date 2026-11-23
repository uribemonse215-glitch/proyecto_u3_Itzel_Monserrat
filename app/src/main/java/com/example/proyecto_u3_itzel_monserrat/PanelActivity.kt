package com.example.proyecto_u3_itzel_monserrat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_u3_itzel_monserrat.data.Pedido
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityPanelBinding
import com.example.proyecto_u3_itzel_monserrat.ui.adapter.PedidoAdapter
import com.google.firebase.firestore.FirebaseFirestore

class PanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelBinding
    private lateinit var pedidoAdapter: PedidoAdapter
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                volverAInicioAutenticacion()
            }
        })

        val usuario = intent.getStringExtra("USUARIO_NOMBRE").orEmpty()
        if (usuario.isNotEmpty()) {
            binding.tvBienvenida.text = "\"EL WERO\" - $usuario"
        }

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
                AlertDialog.Builder(this)
                    .setTitle("Eliminar pedido")
                    .setMessage("¿Eliminar ${pedido.producto}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        db.collection("pedidos").document(pedido.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
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
            volverAInicioAutenticacion()
        }

        escucharPedidos()
    }

    private fun volverAInicioAutenticacion() {
        val intent = Intent(this, MainActivity::class.java)
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