package com.example.proyecto_u3_itzel_monserrat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_u3_itzel_monserrat.data.Pedido
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityFormPedidoBinding
import com.google.firebase.firestore.FirebaseFirestore

class FormPedidoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormPedidoBinding
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var pedidoId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPedidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoId = intent.getStringExtra("PEDIDO_ID").orEmpty()
        if (pedidoId.isNotEmpty()) {
            binding.etProducto.setText(intent.getStringExtra("PEDIDO_PRODUCTO").orEmpty())
            binding.etDomicilioPedido.setText(intent.getStringExtra("PEDIDO_DOMICILIO").orEmpty())
            binding.etPiezas.setText(intent.getIntExtra("PEDIDO_PIEZAS", 0).toString())
            binding.etTotal.setText(intent.getDoubleExtra("PEDIDO_TOTAL", 0.0).toString())
            binding.btnAgregarPedido.text = "Actualizar"
        }

        binding.btnAgregarPedido.setOnClickListener {
            val producto = binding.etProducto.text.toString().trim()
            val domicilio = binding.etDomicilioPedido.text.toString().trim()
            val piezas = binding.etPiezas.text.toString().trim().toIntOrNull()
            val total = binding.etTotal.text.toString().trim().toDoubleOrNull()

            if (producto.isEmpty()) {
                binding.etProducto.error = "Campo obligatorio"
                return@setOnClickListener
            }
            if (domicilio.isEmpty()) {
                binding.etDomicilioPedido.error = "Campo obligatorio"
                return@setOnClickListener
            }
            if (piezas == null || piezas <= 0) {
                binding.etPiezas.error = "Ingresa piezas válidas"
                return@setOnClickListener
            }
            if (total == null || total <= 0.0) {
                binding.etTotal.error = "Ingresa un total válido"
                return@setOnClickListener
            }

            val docRef = if (pedidoId.isEmpty()) {
                db.collection("pedidos").document()
            } else {
                db.collection("pedidos").document(pedidoId)
            }

            val pedido = Pedido(
                id = docRef.id,
                producto = producto,
                domicilio = domicilio,
                piezas = piezas,
                total = total
            )

            docRef.set(pedido)
                .addOnSuccessListener {
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}