package com.example.storyapp.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class CameraButton : AppCompatButton {
    private lateinit var mediaButton: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = mediaButton
        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
        text = context.resources.getString(R.string.camera)
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        mediaButton = ContextCompat.getDrawable(context, R.drawable.bg_media_button) as Drawable
    }
}