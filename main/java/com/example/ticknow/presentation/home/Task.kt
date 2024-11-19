package com.example.ticknow.presentation.home

import java.util.UUID

data class Task(
    val id:  String = "",
    val title: String = "",
    val description: String? = null,
    val userId: String = "",
    val isCompleted: Boolean = false // Campo obligatorio"

////    val id: String = UUID.randomUUID().toString(), // Generar ID único por defecto
////    val title: String,
////    val description: String? = null,
////    val isCompleted: Boolean = false



//
//    val id: String = "",
//    val title: String = "",
//    val description: String = "",
//    val isCompleted: Boolean = false


)
