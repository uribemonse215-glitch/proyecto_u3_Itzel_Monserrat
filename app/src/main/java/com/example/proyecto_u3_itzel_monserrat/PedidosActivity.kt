package com.example.proyecto_u3_itzel_monserrat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_u3_itzel_monserrat.data.Pedido
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityPedidosBinding
import com.example.proyecto_u3_itzel_monserrat.ui.adapter.PedidoAdapter
import com.google.firebase.firestore.FirebaseFirestore

class PedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidosBinding
    private lateinit var pedidoAdapter: PedidoAdapter
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                                Toast.makeText(this, "Error al eliminar pedido", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.rvPedidos.layoutManager = LinearLayoutManager(this)
        binding.rvPedidos.adapter = pedidoAdapter

        binding.btnIrPedir.setOnClickListener {
            startActivity(Intent(this, FormPedidoActivity::class.java))
        }

        escucharPedidos()
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