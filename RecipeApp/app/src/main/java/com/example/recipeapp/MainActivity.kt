package com.example.recipeapp

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    private lateinit var list: RecyclerView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var allData: List<Recipe>
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        list = findViewById(R.id.recipeList)

        // 直向 2 欄、橫向 3 欄；若失敗則退回 Linear
        val span = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 3
            else -> 2
        }
        try {
            list.layoutManager = GridLayoutManager(this, span)
        } catch (_: Throwable) {
            list.layoutManager = LinearLayoutManager(this)
        }

        // 間距 + 滑動優化
        if (list.itemDecorationCount == 0) list.addItemDecoration(GridSpacingDecoration(12))
        LinearSnapHelper().attachToRecyclerView(list)
        list.itemAnimator?.apply {
            addDuration = 120; changeDuration = 120; moveDuration = 120; removeDuration = 100
        }

        // 原本資料（保留你的內容）
        allData = listOf(
            Recipe("番茄羅勒義大利麵", "清爽義式風味", R.drawable.recipe_1, getString(R.string.lorem)),
            Recipe("和風雞腿排", "鹹甜醬香下飯", R.drawable.recipe_2, getString(R.string.lorem)),
            Recipe("青醬燉飯", "羅勒香濃滑順", R.drawable.recipe_3, getString(R.string.lorem)),
            Recipe("牛肉燉菜", "濃郁湯汁暖胃", R.drawable.recipe_4, getString(R.string.lorem)),
            Recipe("煎鮭魚排", "外酥內嫩富含DHA", R.drawable.recipe_5, getString(R.string.lorem)),
            Recipe("酪梨沙拉", "清爽低負擔", R.drawable.recipe_6, getString(R.string.lorem)),
        )
        render(allData)

        list.doOnPreDraw { list.visibility = View.VISIBLE }
    }

    private fun render(data: List<Recipe>) {
        list.adapter = RecipeAdapter(data) { recipe ->
            val i = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("title", recipe.title)
                putExtra("subtitle", recipe.subtitle)
                putExtra("imageRes", recipe.imageRes)
                putExtra("desc", recipe.description)
            }
            startActivity(i)
        }
        list.scrollToPosition(0)
    }

    // === 動態加入 SearchView（不需要 R.id.*） ===
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.add("搜尋")
        searchItem.setShowAsAction(
            MenuItem.SHOW_AS_ACTION_ALWAYS or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
        )
        val searchView = SearchView(this)
        searchView.queryHint = "搜尋菜名或關鍵字"
        searchItem.actionView = searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query.orEmpty()
                filterAndRender()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                filterAndRender()
                return true
            }
        })

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean = true
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                currentQuery = ""
                filterAndRender()
                return true
            }
        })

        return true
    }

    private fun filterAndRender() {
        val q = currentQuery.trim()
        if (q.isEmpty()) {
            render(allData)
        } else {
            val filtered = allData.filter {
                it.title.contains(q, true) ||
                        it.subtitle.contains(q, true) ||
                        it.description.contains(q, true)
            }
            render(filtered)
        }
    }

    // 你原有的 menu 點擊保留
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> { startActivity(Intent(this, CartActivity::class.java)); true }
            R.id.action_chat -> { startActivity(Intent(this, ChatActivity::class.java)); true }
            R.id.action_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Grid 間距（內嵌，不需新增檔案）
    private class GridSpacingDecoration(private val spaceDp: Int) : RecyclerView.ItemDecoration() {
        private fun Int.dp(): Int =
            (this * Resources.getSystem().displayMetrics.density).toInt()

        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val pos = parent.getChildAdapterPosition(view)
            val span = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
            val space = spaceDp.dp()
            val col = if (span > 0) pos % span else 0

            outRect.left  = space - col * space / span
            outRect.right = (col + 1) * space / span
            outRect.top = if (pos < span) space else 0
            outRect.bottom = space
        }
    }
}
