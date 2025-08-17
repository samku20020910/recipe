package com.example.recipeapp

import androidx.annotation.DrawableRes

data class Recipe(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int,
    val description: String
)
