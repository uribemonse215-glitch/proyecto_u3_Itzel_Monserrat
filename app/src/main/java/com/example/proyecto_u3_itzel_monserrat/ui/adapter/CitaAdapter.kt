package com.example.proyecto_u3_itzel_monserrat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_u3_itzel_monserrat.data.Cita
import com.example.proyecto_u3_itzel_monserrat.databinding.ItemCitaBinding

class CitaAdapter(
    private var citas: MutableList<Cita>,
    private val onItemClick: (Cita) -> Unit,
    private val onItemLongClick: (Cita) -> Unit
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    inner class CitaViewHolder(val binding: ItemCitaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        holder.binding.tvClienteCita.text = "Cliente: ${cita.cliente}"
        holder.binding.tvAnimalCita.text = "Animal: ${cita.animal}"
        holder.binding.tvDomicilioCita.text = "Domicilio: ${cita.domicilio}"
        holder.binding.tvSintomasCita.text = "Síntomas: ${cita.sintomas}"

        holder.itemView.setOnClickListener { onItemClick(cita) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(cita)
            true
        }
    }

    override fun getItemCount(): Int = citas.size

    fun actualizarLista(nuevaLista: List<Cita>) {
        citas = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}