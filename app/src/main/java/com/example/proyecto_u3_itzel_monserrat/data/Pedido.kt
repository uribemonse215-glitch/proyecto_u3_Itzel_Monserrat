package com.example.proyecto_u3_itzel_monserrat.data

data class Pedido(
    var id: String = "",
    var producto: String = "",
    var domicilio: String = "",
    var piezas: Int = 0,
    var total: Double = 0.0
)