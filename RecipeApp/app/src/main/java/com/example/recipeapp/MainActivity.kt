package com.example.recipeapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val list = findViewById<RecyclerView>(R.id.recipeList)
        list.layoutManager = LinearLayoutManager(this)

        val data = listOf(
            Recipe("番茄羅勒義大利麵", "清爽義式風味", R.drawable.recipe_1, getString(R.string.lorem)),
            Recipe("和風雞腿排", "鹹甜醬香下飯", R.drawable.recipe_2, getString(R.string.lorem)),
            Recipe("青醬燉飯", "羅勒香濃滑順", R.drawable.recipe_3, getString(R.string.lorem)),
            Recipe("牛肉燉菜", "濃郁湯汁暖胃", R.drawable.recipe_4, getString(R.string.lorem)),
            Recipe("煎鮭魚排", "外酥內嫩富含DHA", R.drawable.recipe_5, getString(R.string.lorem)),
            Recipe("酪梨沙拉", "清爽低負擔", R.drawable.recipe_6, getString(R.string.lorem)),
        )

        list.adapter = RecipeAdapter(data) { recipe ->
            val i = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("title", recipe.title)
                putExtra("subtitle", recipe.subtitle)
                putExtra("imageRes", recipe.imageRes)
                putExtra("desc", recipe.description)
            }
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cart -> startActivity(Intent(this, CartActivity::class.java))
            R.id.action_chat -> startActivity(Intent(this, ChatActivity::class.java))
            R.id.action_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
