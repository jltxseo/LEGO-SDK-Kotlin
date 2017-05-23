package com.opensource.legosdk.core

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.json.JSONObject

open class LGOWebViewActivity : Activity() {

    companion object {
        var navigationBarDrawable: Drawable? = null
    }

    var contentView: ViewGroup? = null
    open var urlString: String? = null
    var pageSetting: Any? = null
    var navigationItems = LGONavigationItem()
    var args: JSONObject? = null
    lateinit var navigationBar: LGONavigationBar
    lateinit var webView: LGOWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationBar = LGONavigationBar(this)
        navigationItems.activity = this
        intent?.let {
            urlString = it.getStringExtra("LGONavigationController.RequestPath") ?: it.getStringExtra("LGOModalController.RequestPath")
            (it.getStringExtra("LGONavigationController.args") ?: it.getStringExtra("LGOModalController.args"))?.let {
                args = try {
                    JSONObject(it)
                } catch (e: Exception) { null }
            }
        }
        title = null
        webView = LGOWebView(this)
        urlString?.let {
            webView.loadUrl(it)
            applyPageSetting()
        }
        resetLayouts()
    }

    fun resetLayouts() {
        contentView?.removeAllViews()
        if (!navigationBar.hidden) {
            if (navigationBar.statusBarTranslucent) {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val layout = RelativeLayout(this)
                val webViewParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                layout.addView(webView, webViewParams)
                val navigationBarParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (68 * resources.displayMetrics.density).toInt())
                layout.addView(navigationBar, navigationBarParams)
                contentView = layout
            }
            else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val layout = RelativeLayout(this)
                val webViewParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                webViewParams.topMargin = (48 * resources.displayMetrics.density).toInt()
                layout.addView(webView, webViewParams)
                val navigationBarParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (48 * resources.displayMetrics.density).toInt())
                layout.addView(navigationBar, navigationBarParams)
                contentView = layout
            }
        }
        else {
            if (navigationBar.statusBarTranslucent) {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val layout = RelativeLayout(this)
                val webViewParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                layout.addView(webView, webViewParams)
                contentView = layout
            }
            else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val layout = RelativeLayout(this)
                layout.addView(webView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                contentView = layout
            }
        }
        setContentView(contentView)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        navigationBar.reload()
    }

    override fun onResume() {
        super.onResume()
        applyPageSetting()
    }

    fun applyPageSetting() {
        LGOCore.modules.moduleWithName("UI.Page")?.let {
            val module = it
            try {
                it::class.java.getDeclaredMethod("apply", LGOWebViewActivity::class.java)?.let {
                    it.invoke(module, this)
                }
            } catch (e: Exception) {}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            intent?.let {
                if (it.getBooleanExtra("LGONavigationController.Class", false)) {
                    finish()
                }
                else if (it.getBooleanExtra("LGOModalController.Class", false)) {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
