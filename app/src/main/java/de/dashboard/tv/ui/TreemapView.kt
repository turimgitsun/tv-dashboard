package de.dashboard.tv.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import de.dashboard.tv.models.HeatmapItem

class TreemapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var items: List<HeatmapItem> = emptyList()
    private val cells = mutableListOf<Cell>()

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xFF0F1115.toInt()
        strokeWidth = 3f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    private data class Cell(val rect: RectF, val item: HeatmapItem)

    fun setData(data: List<HeatmapItem>) {
        items = data.sortedByDescending { it.marketCap }
        computeLayout()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computeLayout()
    }

    private fun computeLayout() {
        cells.clear()
        if (width <= 0 || height <= 0 || items.isEmpty()) return
        val pad = 2f
        val container = RectF(pad, pad, width.toFloat() - pad, height.toFloat() - pad)
        layoutItems(items, container)
    }

    private fun layoutItems(items: List<HeatmapItem>, rect: RectF) {
        if (items.isEmpty()) return
        if (items.size == 1) {
            cells.add(Cell(RectF(rect), items[0]))
            return
        }

        val total = items.sumOf { it.marketCap }
        if (total <= 0) return

        // Find split point closest to half
        var running = 0.0
        val half = total / 2.0
        var splitIdx = 0
        for (i in items.indices) {
            running += items[i].marketCap
            splitIdx = i
            if (running >= half) break
        }
        // Ensure both groups non-empty
        if (splitIdx >= items.lastIndex) splitIdx = items.lastIndex - 1

        val group1 = items.subList(0, splitIdx + 1)
        val group2 = items.subList(splitIdx + 1, items.size)
        val ratio = (group1.sumOf { it.marketCap } / total).toFloat()

        if (rect.width() >= rect.height()) {
            val mid = rect.left + rect.width() * ratio
            layoutItems(group1, RectF(rect.left, rect.top, mid, rect.bottom))
            layoutItems(group2, RectF(mid, rect.top, rect.right, rect.bottom))
        } else {
            val mid = rect.top + rect.height() * ratio
            layoutItems(group1, RectF(rect.left, rect.top, rect.right, mid))
            layoutItems(group2, RectF(rect.left, mid, rect.right, rect.bottom))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (cell in cells) {
            drawCell(canvas, cell)
        }
    }

    private fun drawCell(canvas: Canvas, cell: Cell) {
        val r = cell.rect
        val item = cell.item

        // Background
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = colorForChange(item.change24h)
        canvas.drawRoundRect(r, 6f, 6f, fillPaint)

        // Border
        canvas.drawRoundRect(r, 6f, 6f, borderPaint)

        val w = r.width()
        val h = r.height()
        if (w < 50 || h < 40) return

        val cx = r.centerX()
        val cy = r.centerY()

        // Scale text to fit box
        val symbolSize = (w * 0.22f).coerceIn(16f, 48f).coerceAtMost(h * 0.28f)
        val priceSize = (symbolSize * 0.6f).coerceAtLeast(12f)
        val changeSize = (symbolSize * 0.52f).coerceAtLeast(11f)

        val totalTextH = symbolSize + priceSize + changeSize + 8f
        val startY = cy - totalTextH / 2f + symbolSize

        // Symbol
        textPaint.textSize = symbolSize
        canvas.drawText(item.symbol, cx, startY, textPaint)

        // Price
        subPaint.textSize = priceSize
        val cur = when (item.currency.uppercase()) {
            "EUR" -> "€"; "USD" -> "$"; "GBP" -> "£"; else -> item.currency
        }
        val priceStr = if (item.price >= 1.0) {
            String.format("%,.2f %s", item.price, cur)
        } else {
            String.format("%.4f %s", item.price, cur)
        }
        canvas.drawText(priceStr, cx, startY + priceSize + 4f, subPaint)

        // Change %
        val arrow = if (item.change24h >= 0) "▲" else "▼"
        val changeStr = "$arrow %.2f%%".format(kotlin.math.abs(item.change24h))
        subPaint.textSize = changeSize
        canvas.drawText(changeStr, cx, startY + priceSize + changeSize + 10f, subPaint)
    }

    private fun colorForChange(change: Double): Int {
        // Map change to color: -7% → deep red, 0% → dark, +7% → deep green
        val norm = (change / 7.0).coerceIn(-1.0, 1.0).toFloat()
        return if (norm >= 0f) {
            blendColor(0xFF1B2A1B.toInt(), 0xFF00C853.toInt(), norm)
        } else {
            blendColor(0xFF2A1B1B.toInt(), 0xFFD50000.toInt(), -norm)
        }
    }

    private fun blendColor(from: Int, to: Int, t: Float): Int {
        val f = t.coerceIn(0f, 1f)
        fun ch(c: Int, shift: Int) = (c shr shift) and 0xFF
        val a = lerp(ch(from, 24), ch(to, 24), f)
        val r = lerp(ch(from, 16), ch(to, 16), f)
        val g = lerp(ch(from, 8), ch(to, 8), f)
        val b = lerp(ch(from, 0), ch(to, 0), f)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun lerp(a: Int, b: Int, t: Float): Int = (a + (b - a) * t).toInt()
}
