package com.example.recipeapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<MaterialToolbar>(R.id.detailToolbar)
        setSupportActionBar(toolbar)

        val title = intent.getStringExtra("title") ?: ""
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val imgRes = intent.getIntExtra("imageRes", 0)
        val desc = intent.getStringExtra("desc") ?: ""
        val subtitle = intent.getStringExtra("subtitle") ?: ""

        findViewById<ImageView>(R.id.detailImage).setImageResource(imgRes)
        findViewById<TextView>(R.id.detailTitle).text = "$title｜$subtitle"
        findViewById<TextView>(R.id.detailDesc).text = desc
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
