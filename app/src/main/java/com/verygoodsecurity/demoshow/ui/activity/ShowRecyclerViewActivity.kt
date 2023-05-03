package com.verygoodsecurity.demoshow.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.logs.VGSShowLogger
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.VGSTextView

class ShowRecyclerViewActivity : AppCompatActivity(R.layout.activity_show_recycler_view),
    Adapter.OnItemClick, VGSOnResponseListener {

    // Init Show SDK, make sure that Context already available
    private val show: VGSShow by lazy {
        VGSShow(this, MainActivity.TENANT_ID, VGSEnvironment.Sandbox())
    }

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }

    private lateinit var adapter: Adapter

    private var view: VGSTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable logs for debug
        VGSShowLogger.isEnabled = true
        VGSShowLogger.level = VGSShowLogger.Level.DEBUG

        // Setup RecyclerView adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = Adapter(generateData(), this)
        recyclerView.adapter = adapter
    }

    override fun onOtherClick(item: OtherItem) {
        Log.d(this::class.simpleName, item.data)
    }

    override fun onShowClick(view: VGSTextView, item: ShowItem) {
        Log.d(this::class.simpleName, item.alias)
        if (!view.isEmpty()) return
        // Subscribe VGS view so SDK will be able to update value
        subscribe(view)
        // Make reveal request
        updateLoadingState(true)
        show.requestAsync(
            VGSRequest.Builder("/post", VGSHttpMethod.POST)
                .body(mapOf("payment_card_number" to item.alias)).build()
        )
    }

    override fun onResponse(response: VGSResponse) {
        // Unsubscribe VGS view so SDK will no hold link to it
        updateLoadingState(false)
        unsubscribe()
    }

    private fun subscribe(view: VGSTextView) {
        view.let { show.subscribe(it) }
        this.view = view
    }

    private fun unsubscribe() {
        view?.let { show.unsubscribe(it) }
        view = null
    }

    private fun updateLoadingState(isLoading: Boolean) {
        recyclerView.isNestedScrollingEnabled = !isLoading
    }

    private fun generateData() = mutableListOf<Item>().apply {
        for (i in 1..10) {
            add(OtherItem(i, "Test text"))
        }
        // Only one ShowItem can be added to RecyclerView at this moment, because currently there is no
        // possibility to reset text in view after recycling.
        add(ShowItem(11, "tok_sandbox_jRQAuK76GMGu3d5Q78JuJP"))
        for (i in 12..100) {
            add(OtherItem(i, "Test text"))
        }
    }
}

class Adapter constructor(
    private val items: List<Item>, private val listener: OnItemClick
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> OtherViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_other, parent, false)
            )

            1 -> ShowViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_show, parent, false)
            )

            else -> throw IllegalArgumentException("Not implemented!")
        }
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is OtherViewHolder -> holder.bind(items[position] as OtherItem)
            is ShowViewHolder -> holder.bind(items[position] as ShowItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is OtherItem -> 0
            is ShowItem -> 1
            else -> throw IllegalArgumentException("Not implemented!")
        }
    }

    inner class OtherViewHolder constructor(view: View) : ViewHolder(view) {

        private val view: TextView = view.findViewById(R.id.otherTextView)

        init {

            view.setOnClickListener {
                listener.onOtherClick(items[adapterPosition] as OtherItem)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: OtherItem) {
            view.text = "id: ${item.id}, ${item.data}"
        }
    }

    inner class ShowViewHolder constructor(view: View) : ViewHolder(view) {

        private val showView: VGSTextView = view.findViewById(R.id.showTextView)

        init {

            view.setOnClickListener {
                listener.onShowClick(showView, items[adapterPosition] as ShowItem)
            }
        }

        fun bind(item: ShowItem) {
            showView.setHint(item.alias)
        }
    }

    interface OnItemClick {

        fun onOtherClick(item: OtherItem)

        fun onShowClick(view: VGSTextView, item: ShowItem)
    }
}

abstract class Item {

    abstract val id: Int
}

data class OtherItem(override val id: Int, val data: String) : Item()

data class ShowItem(override val id: Int, val alias: String) : Item()