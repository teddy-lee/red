package com.benoitquenaudon.tvfoot.red.app.data.entity

data class Tag(
    val name: String,
    val desc: String,
    val type: String,
    val display: Boolean,
    val targets: List<String>
)