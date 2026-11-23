package com.example.proyecto_u3_itzel_monserrat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_u3_itzel_monserrat.data.Cuota
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityFormCuotasBinding
import com.google.firebase.firestore.FirebaseFirestore

class FormCuotaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormCuotasBinding
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var cuotaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCuotasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cuotaId = intent.getStringExtra("CUOTA_ID").orEmpty()
        if (cuotaId.isNotEmpty()) {
            binding.etClienteCuota.setText(intent.getStringExtra("CUOTA_CLIENTE").orEmpty())
            binding.etDebe.setText(intent.getDoubleExtra("CUOTA_DEBE", 0.0).toString())
            binding.etProductosCuota.setText(intent.getStringExtra("CUOTA_PRODUCTOS").orEmpty())
            binding.btnGuardarCuota.text = "Actualizar"
        }

        binding.btnGuardarCuota.setOnClickListener {
            val cliente = binding.etClienteCuota.text.toString().trim()
            val debe = binding.etDebe.text.toString().trim().toDoubleOrNull()
            val productos = binding.etProductosCuota.text.toString().trim()

            if (cliente.isEmpty()) {
                binding.etClienteCuota.error = "Campo obligatorio"
                return@setOnClickListener
            }
            if (debe == null || debe < 0.0) {
                binding.etDebe.error = "Ingresa un monto válido"
                return@setOnClickListener
            }
            if (productos.isEmpty()) {
                binding.etProductosCuota.error = "Campo obligatorio"
                return@setOnClickListener
            }

            val docRef = if (cuotaId.isEmpty()) {
                db.collection("cuotas").document()
            } else {
                db.collection("cuotas").document(cuotaId)
            }

            val cuota = Cuota(
                id = docRef.id,
                cliente = cliente,
                debe = debe,
                productos = productos
            )

            docRef.set(cuota)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cuota guardada", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar la cuota", Toast.LENGTH_SHORT).show()
                }
        }
    }
}