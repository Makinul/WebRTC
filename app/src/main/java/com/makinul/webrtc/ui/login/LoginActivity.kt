package com.makinul.webrtc.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.makinul.webrtc.base.BaseActivity
import com.makinul.webrtc.data.repository.MainRepository
import com.makinul.webrtc.databinding.ActivityLoginBinding
import com.makinul.webrtc.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var views: ActivityLoginBinding

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init() {
        views.apply {
            usernameEt.setText(EMULATOR_USER)
            passwordEt.setText("123456")
            btn.setOnClickListener {
                gotoMainView(usernameEt.text.toString(), passwordEt.text.toString())
            }
            gotoMainView(usernameEt.text.toString(), passwordEt.text.toString())
        }
    }

    private fun gotoMainView(userName: String, password: String) {
        mainRepository.login(userName, password) { isDone, reason ->
            if (!isDone) {
                Toast.makeText(this@LoginActivity, reason, Toast.LENGTH_SHORT).show()
            } else {
                // start moving to our main activity
                startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra("username", userName)
                })
                finish()
            }
        }
    }

    companion object {
        const val DEVICE_USER = "emulator"
        const val EMULATOR_USER = "device"
    }
}