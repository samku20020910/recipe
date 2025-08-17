package com.example.recipeapp

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {

    data class CartItem(
        val id: Int,
        val name: String,
        val price: Int,
        val imageResId: Int,
        var quantity: Int = 1
    )

    private val PREF = "cart_pref"
    private val KEY_CART = "cart_items"
    private val gson = Gson()

    private lateinit var rv: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var adapter: CartAdapter
    private val data: MutableList<CartItem> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        title = "購物車"

        rv = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)

        loadCart()

        if (data.isEmpty()) {
            val fallbackImg = safeDrawableId("recipe_1") ?: android.R.drawable.ic_menu_gallery
            data.add(CartItem(1, "招牌牛肉麵", 120, fallbackImg, 1))
            data.add(CartItem(2, "花蓮剝皮辣椒雞", 180, fallbackImg, 2))
            persist()
        }

        adapter = CartAdapter(
            items = data,
            onChanged = {
                updateTotal()
                persist()
            },
            onDeleted = {
                updateTotal()
                persist()
            }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        updateTotal()

        btnCheckout.setOnClickListener {
            if (data.isEmpty()) {
                Toast.makeText(this, "購物車是空的", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "已送出訂單（示範）", Toast.LENGTH_SHORT).show()
                data.clear()
                adapter.notifyDataSetChanged()
                updateTotal()
                persist()
            }
        }
    }

    private fun updateTotal() {
        val total = data.sumOf { it.price * it.quantity }
        tvTotal.text = "總計：$${total}"
    }

    private fun persist() {
        val sp = getSharedPreferences(PREF, Context.MODE_PRIVATE)
        sp.edit().putString(KEY_CART, gson.toJson(data)).apply()
    }

    private fun loadCart() {
        val sp = getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val json = sp.getString(KEY_CART, null) ?: return
        val type = object : TypeToken<MutableList<CartItem>>() {}.type
        val saved: MutableList<CartItem> = gson.fromJson(json, type)
        data.clear()
        data.addAll(saved)
    }

    private fun safeDrawableId(name: String): Int? {
        val id = resources.getIdentifier(name, "drawable", packageName)
        return if (id != 0) id else null
    }

    inner class CartAdapter(
        private val items: MutableList<CartItem>,
        private val onChanged: () -> Unit,
        private val onDeleted: () -> Unit
    ) : RecyclerView.Adapter<CartAdapter.VH>() {

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val img: ImageView = v.findViewById(R.id.img)
            val tvName: TextView = v.findViewById(R.id.tvName)
            val tvPrice: TextView = v.findViewById(R.id.tvPrice)
            val tvQty: TextView = v.findViewById(R.id.tvQty)
            val btnMinus: ImageButton = v.findViewById(R.id.btnMinus)
            val btnPlus: ImageButton = v.findViewById(R.id.btnPlus)
            val btnDelete: ImageButton = v.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
            return VH(v)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(h: VH, position: Int) {
            val item = items[position]

            val fallback = safeDrawableId("recipe_1") ?: android.R.drawable.ic_menu_gallery
            try {
                h.img.setImageResource(item.imageResId)
            } catch (_: Resources.NotFoundException) {
                h.img.setImageResource(fallback)
            }

            h.tvName.text = item.name
            h.tvPrice.text = "單價：$${item.price}"
            h.tvQty.text = item.quantity.toString()

            h.btnPlus.setOnClickListener {
                item.quantity++
                val idx = h.adapterPosition
                if (idx == RecyclerView.NO_POSITION) return@setOnClickListener
                notifyItemChanged(idx)
                onChanged()
            }

            h.btnMinus.setOnClickListener {
                if (item.quantity > 1) {
                    item.quantity--
                    val idx = h.adapterPosition
                    if (idx == RecyclerView.NO_POSITION) return@setOnClickListener
                    notifyItemChanged(idx)
                    onChanged()
                }
            }

            h.btnDelete.setOnClickListener {
                val idx = h.adapterPosition
                if (idx == RecyclerView.NO_POSITION) return@setOnClickListener
                items.removeAt(idx)
                notifyItemRemoved(idx)
                onDeleted()
            }
        }
    }
}
