package com.example.proyecto_u3_itzel_monserrat.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_u3_itzel_monserrat.data.Pedido
import com.example.proyecto_u3_itzel_monserrat.databinding.ItemPedidoBinding

class PedidoAdapter(
    private var pedidos: MutableList<Pedido>,
    private val onItemClick: (Pedido) -> Unit,
    private val onItemLongClick: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(val binding: ItemPedidoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.binding.tvProductoPedido.text = "Producto: ${pedido.producto}"
        holder.binding.tvDomicilioPedido.text = "Domicilio: ${pedido.domicilio}"
        holder.binding.tvDetallePedido.text =
            "Piezas: ${pedido.piezas}  |  Total: $${String.format("%.2f", pedido.total)}"

        holder.itemView.setOnClickListener { onItemClick(pedido) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(pedido)
            true
        }
    }

    override fun getItemCount(): Int = pedidos.size

    fun actualizarLista(nuevaLista: List<Pedido>) {
        pedidos = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}