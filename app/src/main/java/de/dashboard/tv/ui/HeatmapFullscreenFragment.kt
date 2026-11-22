package de.dashboard.tv.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.data.HeatmapRepository
import de.dashboard.tv.models.HeatmapItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HeatmapFullscreenFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: HeatmapAdapter
    private lateinit var progressBar: ProgressBar
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var items: List<HeatmapItem> = emptyList()
    private var focused = 0

    companion object {
        fun newInstance(kind: String): HeatmapFullscreenFragment = HeatmapFullscreenFragment().apply {
            arguments = Bundle().apply { putString("kind", kind) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_heatmap_fullscreen, container, false)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        rv = view.findViewById(R.id.fullscreenMap)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = HeatmapAdapter(emptyList())
        rv.adapter = adapter
        rv.isFocusable = true
        rv.isFocusableInTouchMode = true
        rv.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> { move(-1); true }
                KeyEvent.KEYCODE_DPAD_DOWN -> { move(1); true }
                KeyEvent.KEYCODE_DPAD_LEFT -> { move(-1); true }
                KeyEvent.KEYCODE_DPAD_RIGHT -> { move(1); true }
                KeyEvent.KEYCODE_DPAD_CENTER -> { openDetails(); true }
                else -> false
            }
        }
        view.requestFocus()
        rv.requestFocus()
        load()
        return view
    }

    private fun move(delta: Int): Boolean {
        if (items.isEmpty()) return true
        val next = (focused + delta).coerceIn(0, items.size - 1)
        if (next == focused) return true
        focused = next
        rv.scrollToPosition(focused)
        return true
    }

    private fun openDetails() {
        if (items.isEmpty()) return
        val item = items[focused]
        val symbol = if (item.symbol.endsWith("-USD")) item.symbol.removeSuffix("-USD") else item.symbol
        val kind = arguments?.getString("kind") ?: "stocks"
        val next = AssetDetailsFragment.newInstance(kind, symbol)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, next)
            .addToBackStack(null)
            .commit()
    }

    private fun load() {
        val kind = arguments?.getString("kind") ?: "stocks"
        progressBar.visibility = View.VISIBLE
        scope.launch {
            val list = if (kind == "crypto") HeatmapRepository.getCryptoHeatmap() else HeatmapRepository.getStockHeatmap()
            items = list
            progressBar.visibility = View.GONE
            adapter.submitList(list)
            focused = 0
            rv.scrollToPosition(0)
        }
    }
}
