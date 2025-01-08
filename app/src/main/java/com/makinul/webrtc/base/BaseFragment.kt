package com.makinul.webrtc.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.makinul.webrtc.R

open class BaseFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLog()
    }

    fun showLog(message: String = getString(R.string.test_message)) {
        Log.v(TAG, message)
    }

    fun showLog(@StringRes message: Int) {
        showLog(getString(message))
    }

    fun showToast(message: String = getString(R.string.test_message)) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(@StringRes message: Int) {
        showToast(getString(message))
    }

    fun showErrorAlert(message: String = getString(R.string.unknown_error)) {
        (activity as? BaseActivity)?.showErrorAlert(message)
    }

    fun getErrorMessage(@StringRes messageResId: Int?, message: String?): String {
        if (messageResId == null) {
            return message ?: getString(R.string.unknown_error)
        }
        return getString(messageResId)
    }

    companion object {
        private const val TAG = "BaseFragment"
    }

}