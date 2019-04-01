package com.fanchen.mbase.dialog

import android.graphics.Color

/**
 * Created by FengTing on 2017/5/8.
 * https://www.github.com/limxing
 */

class Builder {
    internal var backColor = Color.BLACK
    internal var backAlpha = 90
    internal var textColor = Color.WHITE
    internal var textSize = 14f
    internal var padding = 15f
    internal var round = 8f
    internal var roundColor = Color.BLACK
    internal var roundAlpha = 120
    internal var touchAble = false
    internal var withAnim = true//
    internal var stayDuration: Long = 1000
    internal var cancleAble: Boolean = false
    internal var icon: Int = 0
    internal var text: String = ""
    internal var loadingDuration: Long = 0

    internal var sheetPressAlph = 15
    internal var sheetCellHeight = 48
    internal var sheetCellPad = 13

    fun sheetCellPad(pad: Int): Builder {
        this.sheetCellPad = pad
        return this
    }

    fun sheetCellHeight(height: Int): Builder {
        this.sheetCellHeight = height
        return this
    }


    fun sheetPressAlph(alpha: Int): Builder {
        this.sheetPressAlph = alpha
        return this
    }

    fun backColor(backColor: Int): Builder {
        this.backColor = backColor
        return this
    }

    fun backAlpha(backAlpha: Int): Builder {
        this.backAlpha = backAlpha
        return this
    }

    fun textColor(textColor: Int): Builder {
        this.textColor = textColor
        return this
    }

    fun textSize(textSize: Float): Builder {
        this.textSize = textSize
        return this
    }

    fun padding(padding: Float): Builder {
        this.padding = padding
        return this
    }

    fun round(round: Float): Builder {
        this.round = round
        return this
    }

    fun roundColor(roundColor: Int): Builder {
        this.roundColor = roundColor
        return this
    }

    fun roundAlpha(roundAlpha: Int): Builder {
        this.roundAlpha = roundAlpha
        return this
    }

    fun touchAble(touchAble: Boolean): Builder {
        this.touchAble = touchAble
        return this
    }

    fun withAnim(withAnim: Boolean): Builder {
        this.withAnim = withAnim
        return this
    }

    fun stayDuration(time: Long): Builder {
        this.stayDuration = time
        return this
    }

    fun cancleAble(time: Boolean): Builder {
        this.cancleAble = time
        return this
    }

    fun icon(icon: Int): Builder {
        this.icon = icon
        return this
    }

    fun text(msg: String?): Builder {
        this.text = msg ?: ""
        return this
    }

    fun loadingDuration(duration: Long): Builder {
        this.loadingDuration = duration
        return this
    }

    companion object {
        private var defaultBuilder: Builder? = null
        private var alertDefaultBuilder: Builder? = null

        /**
         * @return
         */
        internal fun getDefaultBuilder(): Builder {
            if (defaultBuilder == null)
                defaultBuilder = Builder()
            return defaultBuilder!!
        }

        internal fun getAlertDefaultBuilder(): Builder? {
            if (alertDefaultBuilder == null)
                alertDefaultBuilder = Builder().roundColor(Color.WHITE).roundAlpha(255).textColor(Color.GRAY).textSize(15f).cancleAble(true)
            return alertDefaultBuilder
        }
    }
}
