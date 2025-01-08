package com.makinul.webrtc.ui

import android.os.Bundle
import com.makinul.webrtc.base.BaseActivity

class CloseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishAffinity()
    }
}