package com.example.proyecto_u3_itzel_monserrat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_u3_itzel_monserrat.data.Cita
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityCitasBinding
import com.example.proyecto_u3_itzel_monserrat.ui.adapter.CitaAdapter
import com.google.firebase.firestore.FirebaseFirestore

class CitasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCitasBinding
    private lateinit var citaAdapter: CitaAdapter
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var listaCitas: List<Cita> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaAdapter = CitaAdapter(
            citas = mutableListOf(),
            onItemClick = { cita -> abrirEditarCita(cita) },
            onItemLongClick = { cita -> confirmarEliminarCita(cita) }
        )

        binding.rvCitas.layoutManager = LinearLayoutManager(this)
        binding.rvCitas.adapter = citaAdapter

        binding.btnIrAgendarCita.setOnClickListener {
            startActivity(Intent(this, FormCitaActivity::class.java))
        }

        binding.btnEditarCita.setOnClickListener {
            if (listaCitas.isEmpty()) {
                Toast.makeText(this, "No hay citas para editar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nombres = listaCitas.map { "${it.cliente} - ${it.animal}" }.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Selecciona una cita para editar")
                .setItems(nombres) { _, which ->
                    abrirEditarCita(listaCitas[which])
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnEliminarCita.setOnClickListener {
            if (listaCitas.isEmpty()) {
                Toast.makeText(this, "No hay citas para eliminar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nombres = listaCitas.map { "${it.cliente} - ${it.animal}" }.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Selecciona una cita para eliminar")
                .setItems(nombres) { _, which ->
                    confirmarEliminarCita(listaCitas[which])
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        escucharCitas()
    }

    private fun abrirEditarCita(cita: Cita) {
        val intent = Intent(this, FormCitaActivity::class.java).apply {
            putExtra("CITA_ID", cita.id)
            putExtra("CITA_CLIENTE", cita.cliente)
            putExtra("CITA_ANIMAL", cita.animal)
            putExtra("CITA_DOMICILIO", cita.domicilio)
            putExtra("CITA_SINTOMAS", cita.sintomas)
        }
        startActivity(intent)
    }

    private fun confirmarEliminarCita(cita: Cita) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar cita")
            .setMessage("¿Eliminar la cita de ${cita.cliente}?")
            .setPositiveButton("Eliminar") { _, _ ->
                db.collection("citas").document(cita.id).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al eliminar cita", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun escucharCitas() {
        db.collection("citas").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(this, "Error al cargar citas", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            val lista = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Cita::class.java)?.apply { id = doc.id }
            }.orEmpty()
            listaCitas = lista
            citaAdapter.actualizarLista(lista)
        }
    }
}