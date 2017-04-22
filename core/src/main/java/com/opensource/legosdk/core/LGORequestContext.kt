package com.opensource.legosdk.core

import android.content.Context
import android.webkit.WebView

/**
 * Created by PonyCui_Home on 2017/4/9.
 */

open class LGORequestContext(val sender: Any?) {

    fun requestContentContext(): Context? {
        (sender as? WebView)?.let {
            return it.context
        }
        return null
    }

}
