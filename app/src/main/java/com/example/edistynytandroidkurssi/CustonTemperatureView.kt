package com.example.edistynytandroidkurssi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View


    class CustonTemperatureView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // muuttuja joka pitää kirjaa aktiivisesta lämpötilasta
        private var temperature : Int = 0

        // tämän metodin kautta voimme muuttaa aktiivista lämpötilaa
        // esim. fragmentista käsin
        public fun changeTemperature (temp : Int ){
            temperature = temp


            // muutetaan ympyrän väri riippuen lämpötilasta
            if (temperature < -20) {
                paint.color = Color.BLUE
            } else if (temperature in -20..-10) {
                paint.color = Color.parseColor("#ADD8E6") // Vaaleansininen
            } else if (temperature in -10..10) {
                paint.color = Color.YELLOW
            } else if (temperature in 10..20) {
                paint.color = Color.parseColor("#FFA500") // Oranssi
            } else {
                paint.color = Color.RED
            }

            // android ei oletuksena piirrä customviewiä uusiksi jotetn töllä me tehdään se
            invalidate()
            requestLayout()
        }


        init {

            paint.color = Color.BLACK
            textPaint.color = Color.WHITE
            textPaint.textSize= 90f
            textPaint.textAlign= Paint.Align.CENTER

            // asetetaan tekstille lihavointi.
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));



        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // parameters: x-coordinate, y-coordinate, size, color
            canvas.drawCircle(width.toFloat() / 2, width.toFloat() / 2, width.toFloat() / 2, paint)

            // parameters: content, x, y, color
            canvas.drawText("${temperature}℃", width.toFloat() / 2, width.toFloat() / 2 + 30 , textPaint);
        }


        // tämän custonviewin oletuskoko, 200 x 200
        val size = 200

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            // Try for a width based on our minimum
            val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
            var w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)

            // if no exact size given (either dp or match_parent)
            // use this one instead as default (wrap_content)
            if (w == 0)
            {
                w = size * 2
            }

            // Whatever the width ends up being, ask for a height that would let the view
            // get as big as it can
            // val minh: Int = View.MeasureSpec.getSize(w) + paddingBottom + paddingTop
            // in this case, we use the height the same as our width, since it's a circle
            val h: Int = View.resolveSizeAndState(
                View.MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
            )

            setMeasuredDimension(w, h)
        }

// note, if you use this version, in your onDraw-method, remember to use the
// canvas size when drawing the circle. for example:
// canvas.width.toFloat()

            // Android uses this to determine the exact size of your component on screen
        }

