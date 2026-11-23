package com.example.proyecto_u3_itzel_monserrat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_u3_itzel_monserrat.data.Cuota
import com.example.proyecto_u3_itzel_monserrat.databinding.ItemCuotaBinding

class CuotaAdapter(
    private var cuotas: MutableList<Cuota>,
    private val onItemClick: (Cuota) -> Unit,
    private val onItemLongClick: (Cuota) -> Unit
) : RecyclerView.Adapter<CuotaAdapter.CuotaViewHolder>() {

    inner class CuotaViewHolder(val binding: ItemCuotaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuotaViewHolder {
        val binding = ItemCuotaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CuotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CuotaViewHolder, position: Int) {
        val cuota = cuotas[position]
        holder.binding.tvClienteCuota.text = "Cliente: ${cuota.cliente}"
        holder.binding.tvDebeCuota.text = "Debe: $${String.format("%.2f", cuota.debe)}"
        holder.binding.tvProductosCuota.text = "Producto(s): ${cuota.productos}"

        holder.itemView.setOnClickListener { onItemClick(cuota) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(cuota)
            true
        }
    }

    override fun getItemCount(): Int = cuotas.size

    fun actualizarLista(nuevaLista: List<Cuota>) {
        cuotas = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}