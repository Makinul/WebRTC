package com.makinul.webrtc.base

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.http.SslError
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.makinul.webrtc.R

open class BaseActivity : AppCompatActivity() {

//    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onCreate(savedInstanceState, persistentState)
//
//        val displayMetrics = resources.displayMetrics
//        AppConstants.deviceDensity = displayMetrics.density
//    }

    fun showLog(message: String = getString(R.string.test_message)) {
        Log.v(TAG, message)
    }

    fun showLog(@StringRes message: Int) {
        showLog(getString(message))
    }

    fun showToast(message: String = getString(R.string.test_message)) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(@StringRes message: Int) {
        showToast(getString(message))
    }

    fun getErrorMessage(message: String?, @StringRes messageResId: Int?): String {
        if (messageResId == null) {
            return message ?: getString(R.string.unknown_error)
        }
        return getString(messageResId)
    }

    fun showErrorAlert(message: String = getString(R.string.unknown_error)) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.alert)
            .setMessage(message)
            .setPositiveButton(R.string.ok, null)
        alertDialog.show()
    }

    companion object {
        private const val TAG = "FirebaseWebRTC BaseActivity"
    }
}