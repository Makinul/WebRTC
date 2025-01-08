package com.makinul.webrtc.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.codewithkael.firebasevideocall.service.MainServiceRepository
import com.google.android.material.snackbar.Snackbar
import com.makinul.webrtc.R
import com.makinul.webrtc.base.BaseActivity
import com.makinul.webrtc.data.repository.MainRepository
import com.makinul.webrtc.databinding.ActivityMainBinding
import com.makinul.webrtc.service.MainService
import com.makinul.webrtc.ui.CallActivity
import com.makinul.webrtc.utils.DataModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), MainService.Listener {

    @Inject
    lateinit var mainRepository: MainRepository

    @Inject
    lateinit var mainServiceRepository: MainServiceRepository

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
            startActivity(Intent(this@MainActivity, CallActivity::class.java).apply {
                putExtra("target", "-2222222")
                putExtra("isVideoCall", true)
                putExtra("isCaller", true)
            })
        }
        init()
    }

    private var username: String? = null

    private fun init() {
        username = intent.getStringExtra("username")
        if (username == null) finish()

        MainService.listener = this

        //2. start foreground service to listen negotiations and calls.
        startMyService()
    }

    private fun startMyService() {
        mainServiceRepository.startService(username!!)
    }

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

    override fun onCallReceived(model: DataModel) {
        runOnUiThread {
//            views.apply {
//                val isVideoCall = model.type == DataModelType.StartVideoCall
//                val isVideoCallText = if (isVideoCall) "Video" else "Audio"
//                incomingCallTitleTv.text = "${model.sender} is $isVideoCallText Calling you"
//                incomingCallLayout.isVisible = true
//                acceptButton.setOnClickListener {
//                    getCameraAndMicPermission {
//                        incomingCallLayout.isVisible = false
//                        // create an intent to go to video call activity
//                        startActivity(Intent(this@MainActivity, CallActivity::class.java).apply {
//                            putExtra("target", model.sender)
//                            putExtra("isVideoCall", isVideoCall)
//                            putExtra("isCaller", false)
//                        })
//                    }
//                }
//
//                declineButton.setOnClickListener {
//                    incomingCallLayout.isVisible = false
//                }
//            }
            showToast("Call Received ${model.sender}")
        }
    }

    override fun onCallEnded(model: DataModel) {
        runOnUiThread {
            showToast("Call Ended ${model.sender}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainServiceRepository.stopService()
    }

    companion object {
        private val TAG = "FirebaseWebRTC MainActivity"
    }
}