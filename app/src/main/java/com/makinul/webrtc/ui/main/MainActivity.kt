package com.makinul.webrtc.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.makinul.webrtc.R
import com.makinul.webrtc.base.BaseActivity
import com.makinul.webrtc.data.repository.MainRepository
import com.makinul.webrtc.databinding.ActivityMainBinding
import com.makinul.webrtc.ui.CallActivity
import com.makinul.webrtc.ui.login.LoginActivity.Companion.DEVICE_USER
import com.makinul.webrtc.ui.login.LoginActivity.Companion.EMULATOR_USER
import com.makinul.webrtc.utils.DataModel
import com.makinul.webrtc.utils.DataModelType
import com.makinul.webrtc.utils.getCameraAndMicPermission
import com.makinul.webrtc.utils.isValid
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), MainRepository.Listener {

    @Inject
    lateinit var mainRepository: MainRepository

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
            val target = DEVICE_USER
            val sender = EMULATOR_USER
            mainRepository.setSender(sender)
            mainRepository.setTarget(target)
            mainRepository.sendConnectionRequest(target, true) {
                if (it) {
                    //we have to start video call
                    //we wanna create an intent to move to call activity
                    startActivity(Intent(this, CallActivity::class.java).apply {
                        putExtra("username", sender)
                        putExtra("target", target)
                        putExtra("isVideoCall", true)
                        putExtra("isCaller", true)
                    })
                }
            }
        }
        init()
    }

    private var username: String? = null

    private fun init() {
        username = intent.getStringExtra("username")
        if (username == null) finish()

        binding.toolbar.title = username

        //1. observe other users status
        subscribeObservers()

//        //2. start foreground service to listen negotiations and calls.
//        startMyService()

        //setup my clients
        mainRepository.listener = this
        mainRepository.initFirebase()
    }

    private fun subscribeObservers() {
//        mainRepository.observeUsersStatus {
//            showLog("subscribeObservers $it")
//        }
    }

//    private fun startMyService() {
//        mainServiceRepository.startService(username!!)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun onCallReceived(model: DataModel) {
        runOnUiThread {
            binding.apply {
                val isVideoCall = model.type == DataModelType.StartVideoCall
                val isVideoCallText = if (isVideoCall) "Video" else "Audio"
                incomingCallTitleTv.text = "${model.sender} is $isVideoCallText Calling you"
                incomingCallLayout.isVisible = true
                acceptButton.setOnClickListener {
                    getCameraAndMicPermission {
                        incomingCallLayout.isVisible = false
                        // create an intent to go to video call activity

                        val target = model.sender
                        val sender = model.target
                        mainRepository.setSender(sender)
                        mainRepository.setTarget(target)

                        startActivity(Intent(this@MainActivity, CallActivity::class.java).apply {
                            putExtra("username", model.target)
                            putExtra("target", model.sender)
                            putExtra("isVideoCall", isVideoCall)
                            putExtra("isCaller", false)
                        })
                    }
                }

                declineButton.setOnClickListener {
                    incomingCallLayout.isVisible = false

                    val target = EMULATOR_USER
                    val sender = DEVICE_USER
                    mainRepository.setSender(sender)
                    mainRepository.setTarget(target)

                    mainRepository.endCall()
                    mainRepository.sendEndCall()
                }
            }
        }
    }

    private fun onCallEnded(model: DataModel) {
        runOnUiThread {
            binding.apply {
                incomingCallLayout.isVisible = false
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mainServiceRepository.stopService()
//    }

    companion object {
        private val TAG = "FirebaseWebRTC MainActivity"
    }

    override fun onLatestEventReceived(data: DataModel) {
        showLog("data.type ${data.type}")
        when (data.type) {
            DataModelType.StartVideoCall,
            DataModelType.StartAudioCall -> {
                onCallReceived(data)
            }

            DataModelType.EndCall -> {
                onCallEnded(data)
            }

            else -> Unit
        }
    }

    override fun endCall() {
        showLog("endCall")
        mainRepository.endCall()
    }
}