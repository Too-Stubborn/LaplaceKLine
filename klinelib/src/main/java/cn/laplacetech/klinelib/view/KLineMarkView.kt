package cn.laplacetech.klinelib.view

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.laplacetech.klinelib.R
import cn.laplacetech.klinelib.model.HisData
import cn.laplacetech.klinelib.util.DateUtils
import cn.laplacetech.klinelib.util.DoubleUtil
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.highlight.Highlight
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by laplace on 2018/8/3.
 * K线图的 点击后数据展示小窗
 */
class KLineMarkView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    protected var mRunnable: Runnable = Runnable {
        visibility = View.GONE
        if (mLineCharts != null) {
            for (chart in mLineCharts!!) {
                chart.highlightValue(null)
            }
        }
    }
    private var mLineCharts: Array<out Chart<*>>? = null

    fun setChart(vararg chart: Chart<*>) {
        mLineCharts = chart
    }

    private var paint: Paint = Paint()
    private var bacRect = Rect()
    private var density: Int = 1
    private var textR: Rect = Rect()
    private var hightTimeRect = Rect()
    private var hightValueRect = Rect()
    var isRight = true

    var bacLeft = 0
    var bacRight = 0

    var pTop = 0


    private var titleList: Array<String>


    init {

        paint.isAntiAlias = true
        density = context.resources.displayMetrics.density.toInt()
        paint.textSize = 10f * density

        titleList = resources.getStringArray(R.array.kline_mark_title)

    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
//        if (newConfig?.orientation == ORIENTATION_LANDSCAPE) {
//            this.pTop = 20
//        } else {
//            this.pTop = 0
//        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (info.size == 0) return


        highlight?.yPx?.toInt()?.let {
            val value = info[3].text
            paint.getTextBounds(value, 0, value.length, textR)

            hightValueRect.left = 0
            hightValueRect.right = textR.width() + 6 * density
            hightValueRect.top = it - 8 * density
            hightValueRect.bottom = it + 8 * density
            paint.style = Paint.Style.FILL
            paint.color = resources.getColor(R.color.ma5)
            canvas.drawRect(hightValueRect, paint)

            paint.color = Color.WHITE
            val timeXX = hightValueRect.left + (hightValueRect.width() - textR.width()) / 2f
            val timeYY = hightValueRect.top + (hightValueRect.height() + textR.height()) / 2f

            canvas.drawText(value, timeXX, timeYY, paint)
        }

        highlight?.xPx?.toInt()?.let {
            paint.getTextBounds(time, 0, time.length, textR)
            paint.color = resources.getColor(R.color.ma5)
            paint.style = Paint.Style.FILL
            val timeY = measuredHeight - 90 * density
            val textW2 = textR.width() / 2 + 3 * density

            hightTimeRect.left = it - textW2
            hightTimeRect.top = timeY - 8 * density
            hightTimeRect.right = it + textW2
            hightTimeRect.bottom = timeY + 8 * density

            if (hightTimeRect.left < 0) {
                hightTimeRect.left = 0
                hightTimeRect.right = textW2 * 2
            }
            if (hightTimeRect.right > measuredWidth) {
                hightTimeRect.right = measuredWidth
                hightTimeRect.left = measuredWidth - textW2 * 2
            }
            canvas.drawRect(hightTimeRect, paint)
            paint.color = Color.WHITE
            val timeXX = hightTimeRect.left + (hightTimeRect.width() - textR.width()) / 2f
            val timeYY = hightTimeRect.top + (hightTimeRect.height() + textR.height()) / 2f

            canvas.drawText(time, timeXX, timeYY, paint)
        }










        if (!isRight) {
            bacRect.left = measuredWidth - bacRight - 100 * density
            bacRect.right = measuredWidth - bacRight

        } else {
            bacRect.left = bacLeft
            bacRect.right = bacLeft + 100 * density
        }
        paint.color = resources.getColor(R.color.kline_mark_bac)
        paint.style = Paint.Style.FILL
        bacRect.top = pTop * density
        bacRect.bottom = (105 + pTop) * density

        canvas.drawRect(bacRect, paint)

        paint.color = resources.getColor(R.color.kline_mark_border)
        paint.strokeWidth = 0.5f * density
        paint.style = Paint.Style.STROKE
        canvas.drawRect(Rect(bacRect), paint)

        paint.alpha = 1
        paint.style = Paint.Style.FILL
//        头部
        paint.color = resources.getColor(R.color.kline_mark_text)


        var x = bacRect.left + (bacRect.width() - textR.width()) / 2f
        var y = bacRect.top + 10f * density



        canvas.drawText(time, x, y, paint)

//        超大单
//        paint.style = Paint.Style.FILL
//        x = bacRect.left + 5f * density
        val fen = 14f * density
//        paint.color = resources.getColor(R.color.giant_lc_color)
//        y += fen
//        canvas.drawCircle(x, y, 3f * density, paint)
//
//        paint.color = resources.getColor(R.color.large_lc_color)
//        y += fen
//        canvas.drawCircle(x, y, 3f * density, paint)
//
//        paint.color = resources.getColor(R.color.middle_lc_color)
//        y += fen
//        canvas.drawCircle(x, y, 3f * density, paint)
//
//        paint.color = resources.getColor(R.color.small_lc_color)
//        y += fen
//        canvas.drawCircle(x, y, 3f * density, paint)


        paint.color = Color.parseColor("#878B96")

        x = bacRect.left + 5f * density
        y += textR.height() / 2

        for (s in titleList) {
            y += fen
            canvas.drawText(s, x, y, paint)

        }



        y = bacRect.top + 10f * density + textR.height() / 2


        for (i in 0 until (info.size)) {
            val money = info[i].text
            paint.color = info[i].color
            paint.getTextBounds(money, 0, money.length, textR)
            y += +fen
            x = bacRect.right - textR.width() - 3f * density
            canvas.drawText(money, x, y, paint)
        }


    }


    private var info: ArrayList<TextBean> = ArrayList<TextBean>()

    private var time: String = ""

    open fun update(info: Array<Double>?, time: String) {
        if (this.info.size == 0 && info == null) return
        this.info.clear()
        if (info != null) {
            for (i in 0 until info.size) {
                this.info.add(when (i) {
                    4 -> TextBean(getValue(info[i]), getColorByRatio(info[i]))
                    5 -> TextBean("${getValue(info[i])}%", getColorByRatio(info[i]))
                    else -> TextBean(DoubleUtil.amountConversion(info[i]), resources.getColor(R.color.kline_mark_text))
                })

            }
        }
        this.time = time
        invalidate()

    }

//    private var timeX: Int = 0

    private var highlight: Highlight? = null

    /**
     *
     */
    fun update(info: HisData?, h: Highlight, mDateFormat: String) {
        if (this.info.size == 0 && info == null) return
        this.info.clear()

        this.isRight = h.xPx > measuredWidth / 2

        val normalColor = resources.getColor(R.color.kline_mark_text)
        this.info.add(TextBean(info?.open.toString(), normalColor))
        this.info.add(TextBean(info?.high.toString(), normalColor))
        this.info.add(TextBean(info?.low.toString(), normalColor))
        this.info.add(TextBean(info?.close.toString(), normalColor))
        this.info.add(TextBean(DoubleUtil.amountConversion(info?.change ?: 0.0), getColorByRatio(info?.change ?: 0.0)))
        this.info.add(TextBean(DoubleUtil.amountConversion(info?.change_ratio ?: 0.0), getColorByRatio(info?.change_ratio ?: 0.0)))
        this.time = DateUtils.formatDate(info?.date ?: 0, dataFormatString)

        highlight = h
        Log.i("highlight", "" + h.xPx + "==" + h.yPx)


        invalidate()

        removeCallbacks(mRunnable)
        postDelayed(mRunnable, 2000)

    }

    private var mRiseColor = R.color.increasing_color
    private var mDropColor = R.color.decreasing_color

    private fun getColorByRatio(num: Double): Int {
        return when {
            num > 0 -> resources.getColor(mRiseColor)
            num < 0 -> resources.getColor(mDropColor)
            else -> resources.getColor(R.color.kline_mark_text)
        }
    }

    private fun getValue(value: Double): String {

        var text = DoubleUtil.amountConversion(value)
//        var text = NumberUtils.amountConversion(value)
        if (value > 0) {
            text = "+$text"
        } else if (value < 0) {
            text = "-$text"
        }
        return text
    }

    data class TextBean(var text: String, var color: Int)

    private var dataFormatString: String = "yyyy-MM-dd HH:mm"

    fun setDataFormatString(mDateFormat: String) {
        this.dataFormatString = mDateFormat
    }

    fun getDataFormatString(): String {
        return dataFormatString
    }

    fun closeHightLight() {
        if (this.highlight == null) return
        this.highlight = null
        invalidate()
    }
}