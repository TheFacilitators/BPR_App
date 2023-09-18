package com.facilitation.view

import com.vuzix.hud.resources.*


class ViewApplication : DynamicThemeApplication() {
    override fun getNormalThemeResId(): Int {
        return R.style.AppTheme
    }

    override fun getLightThemeResId(): Int {
        return R.style.AppTheme_Light
    }
}