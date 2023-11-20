package com.facilitation.view.utility.enums

import android.view.KeyEvent

enum class TapToCommandEnum {
    XXOOO { override fun keyCode() = KeyEvent.KEYCODE_ENTER },
    XOXXO { override fun keyCode() = KeyEvent.KEYCODE_HOME },
    OXXXX { override fun keyCode() = KeyEvent.KEYCODE_BACK },
    XOXOO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_LEFT },
    XOOXO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_RIGHT },
    OOXOO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_UP },
    OXOOO { override fun keyCode() = KeyEvent.KEYCODE_DPAD_DOWN },
    XXXOO { override fun keyCode() = KeyEvent.KEYCODE_SPACE },
    OXXOO { override fun keyCode() = KeyEvent.KEYCODE_VOLUME_UP },
    OOOXX { override fun keyCode() = KeyEvent.KEYCODE_VOLUME_DOWN };

    abstract fun keyCode():Int
}