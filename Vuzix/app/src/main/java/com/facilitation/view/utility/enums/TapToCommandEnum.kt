package com.facilitation.view.utility.enums

import android.view.KeyEvent

enum class TapToCommandEnum {
    XXOOO { override fun keyCode() = KeyEvent.KEYCODE_ENTER },
    XOXXO { override fun keyCode() = KeyEvent.KEYCODE_HOME },
    OXXXX { override fun keyCode() = KeyEvent.KEYCODE_BACK },
    XOXOO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_LEFT},
    XOOXO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_RIGHT },
    OXXOO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_UP },
    OOOXX { override fun keyCode() = KeyEvent.KEYCODE_DPAD_DOWN },
    XXXOO { override fun keyCode() = KeyEvent.KEYCODE_SPACE };

    abstract fun keyCode():Int
}