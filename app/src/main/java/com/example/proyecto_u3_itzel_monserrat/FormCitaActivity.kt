package com.example.proyecto_u3_itzel_monserrat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_u3_itzel_monserrat.data.Cita
import com.example.proyecto_u3_itzel_monserrat.databinding.ActivityFormCitaBinding
import com.google.firebase.firestore.FirebaseFirestore

class FormCitaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormCitaBinding
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var citaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCitaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        citaId = intent.getStringExtra("CITA_ID").orEmpty()
        if (citaId.isNotEmpty()) {
            binding.etCliente.setText(intent.getStringExtra("CITA_CLIENTE").orEmpty())
            binding.etAnimal.setText(intent.getStringExtra("CITA_ANIMAL").orEmpty())
            binding.etDomicilio.setText(intent.getStringExtra("CITA_DOMICILIO").orEmpty())
            binding.etSintomas.setText(intent.getStringExtra("CITA_SINTOMAS").orEmpty())
            binding.btnGuardarCita.text = "Actualizar"
        }

        binding.btnGuardarCita.setOnClickListener {
            val cliente = binding.etCliente.text.toString().trim()
            val animal = binding.etAnimal.text.toString().trim()
            val domicilio = binding.etDomicilio.text.toString().trim()
            val sintomas = binding.etSintomas.text.toString().trim()

            if (cliente.isEmpty()) {
                binding.etCliente.error = "Campo obligatorio"
                return@setOnClickListener
            }
            if (animal.isEmpty()) {
                binding.etAnimal.error = "Campo obligatorio"
                return@setOnClickListener
            }
            if (domicilio.isEmpty()) {
                binding.etDomicilio.error = "Campo obligatorio"
                return@setOnClickListener
            }
            if (sintomas.isEmpty()) {
                binding.etSintomas.error = "Campo obligatorio"
                return@setOnClickListener
            }

            val docRef = if (citaId.isEmpty()) {
                db.collection("citas").document()
            } else {
                db.collection("citas").document(citaId)
            }

            val cita = Cita(
                id = docRef.id,
                cliente = cliente,
                animal = animal,
                domicilio = domicilio,
                sintomas = sintomas
            )

            docRef.set(cita)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cita guardada", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar la cita", Toast.LENGTH_SHORT).show()
                }
        }
    }
}