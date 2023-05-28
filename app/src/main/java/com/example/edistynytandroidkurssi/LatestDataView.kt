package com.example.edistynytandroidkurssi

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView


class LatestDataView  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr) {

        var macRows = 5


        init {
            // vaihdetaan linearlayoutin orientaation pystysyynnaksi
            // jotta TextViewit rakentuvat allekkain
            this.orientation = VERTICAL

            // Tehdään uusi TextView muistiin ja käsketään androidia mittaamaan se tällä näytöllä
            var someTextView : TextView = TextView(context)
            someTextView.measure(0,0)
            var rowHeight = someTextView.measuredHeight

            // Mitataan myäs itse LinearLayout
            this.measure(0,0)

            // lasketaan kaikki yhteen ja asetetaan korkeus LinearLayoutissa
            var additionalHeight = this.measuredHeight + (macRows * rowHeight)
            this.minimumHeight = additionalHeight

        }

    // apufunktio, joka lisää linearlayotin uuden textviewin lennosta.
    fun addData(message : String)
    {
        // ennenkuin lisätään uusi TextView, huolehditaaan
        // että LinearLayoutissa ei ole ylimääräisiä TextViewejä (MAX 5 KPL!)

        // niin kauan kuin lukumäärä on liian suuri ---> poista vanhin TextView
        if(this.childCount >= macRows) {
            this.removeViewAt(0)
        }

        // lisätään uusi TextView
        var newTextView : TextView = TextView(context) as TextView
        newTextView.setText(message)
        newTextView.setBackgroundColor(Color.BLACK)
        newTextView.setTextColor(Color.YELLOW)
        this.addView(newTextView)

        // fade-in animaatio päälle
        val animation = AnimationUtils.loadAnimation(context, R.anim.customfade)
        //starting the animation
        newTextView.startAnimation(animation)
    }
    }
