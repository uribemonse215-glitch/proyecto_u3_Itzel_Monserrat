package com.example.proyecto_u3_itzel_monserrat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_u3_itzel_monserrat.data.Cuota
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityCuotasBinding
import com.example.proyecto_u3_itzel_monserrat.ui.adapter.CuotaAdapter
import com.google.firebase.firestore.FirebaseFirestore

class CuotasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCuotasBinding
    private lateinit var cuotaAdapter: CuotaAdapter
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCuotasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cuotaAdapter = CuotaAdapter(
            cuotas = mutableListOf(),
            onItemClick = { cuota ->
                val intent = Intent(this, FormCuotaActivity::class.java).apply {
                    putExtra("CUOTA_ID", cuota.id)
                    putExtra("CUOTA_CLIENTE", cuota.cliente)
                    putExtra("CUOTA_DEBE", cuota.debe)
                    putExtra("CUOTA_PRODUCTOS", cuota.productos)
                }
                startActivity(intent)
            },
            onItemLongClick = { cuota ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar cuota")
                    .setMessage("¿Eliminar la cuota de ${cuota.cliente}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        db.collection("cuotas").document(cuota.id).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cuota eliminada", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al eliminar cuota", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.rvCuotas.layoutManager = LinearLayoutManager(this)
        binding.rvCuotas.adapter = cuotaAdapter

        binding.btnIrAgregarCuota.setOnClickListener {
            startActivity(Intent(this, FormCuotaActivity::class.java))
        }

        escucharCuotas()
    }

    private fun escucharCuotas() {
        db.collection("cuotas").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, "Error al cargar cuotas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            val lista = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Cuota::class.java)?.apply { id = doc.id }
            }.orEmpty()
            cuotaAdapter.actualizarLista(lista)
        }
    }
}