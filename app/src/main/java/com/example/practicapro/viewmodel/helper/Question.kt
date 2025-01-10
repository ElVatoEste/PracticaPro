package com.example.practicapro.viewmodel.helper

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

