package com.makinul.webrtc.ui

import android.os.Bundle
import androidx.core.view.isVisible
import com.makinul.webrtc.base.BaseActivity
import com.makinul.webrtc.data.repository.MainRepository
import com.makinul.webrtc.databinding.ActivityCallBinding
import com.makinul.webrtc.ui.login.LoginActivity.Companion.DEVICE_USER
import com.makinul.webrtc.ui.login.LoginActivity.Companion.EMULATOR_USER
import com.makinul.webrtc.utils.DataModel
import com.makinul.webrtc.utils.DataModelType
import com.makinul.webrtc.utils.convertToHumanTime
import com.makinul.webrtc.utils.isValid
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class CallActivity : BaseActivity(), MainRepository.Listener {

    private var target: String? = null
    private var isVideoCall: Boolean = true
    private var isCaller: Boolean = true

    private var isMicrophoneMuted = false
    private var isCameraMuted = false
    private var isSpeakerMode = true

    @Inject
    lateinit var mainRepository: MainRepository

    private lateinit var views: ActivityCallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityCallBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init() {
        intent.getStringExtra("target")?.let {
            this.target = it
        } ?: kotlin.run {
            finish()
        }

        isVideoCall = intent.getBooleanExtra("isVideoCall", true)
        isCaller = intent.getBooleanExtra("isCaller", true)

        views.apply {
            callTitleTv.text = "In call with $target"
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0..3600) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        //convert this int to human readable time
                        callTimerTv.text = i.convertToHumanTime()
                    }
                }
            }

            if (!isVideoCall) {
                toggleCameraButton.isVisible = false
                screenShareButton.isVisible = false
                switchCameraButton.isVisible = false
            }

//            MainService.remoteSurfaceView = remoteView
//            MainService.localSurfaceView = localView
//            serviceRepository.setupViews(isVideoCall, isCaller, target!!)
            endCallButton.setOnClickListener {
                dismissCall()
                finish()
            }
//            switchCameraButton.setOnClickListener {
//                serviceRepository.switchCamera()
//            }
        }
        setupMicToggleClicked()
        setupCameraToggleClicked()
        setupToggleAudioDevice()
//        MainService.endCallListener = this

        //setup my clients
        mainRepository.listener = this
        mainRepository.initFirebase()
        mainRepository.initWebrtcClient(target!!)
    }

    private fun setupMicToggleClicked() {
        views.apply {
            toggleMicrophoneButton.setOnClickListener {
//                if (!isMicrophoneMuted) {
//                    //we should mute our mic
//                    //1. send a command to repository
//                    serviceRepository.toggleAudio(true)
//                    //2. update ui to mic is muted
//                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_on)
//                } else {
//                    //we should set it back to normal
//                    //1. send a command to repository to make it back to normal status
//                    serviceRepository.toggleAudio(false)
//                    //2. update ui
//                    toggleMicrophoneButton.setImageResource(R.drawable.ic_mic_off)
//                }
                isMicrophoneMuted = !isMicrophoneMuted
            }
        }
    }

    override fun onBackPressed() {
        dismissCall()
        super.onBackPressed()
    }

    private fun dismissCall() {
        mainRepository.sendEndCall()
        mainRepository.endCall()
    }

    private fun setupToggleAudioDevice() {
        views.apply {
            toggleAudioDevice.setOnClickListener {
//                if (isSpeakerMode) {
//                    //we should set it to earpiece mode
//                    toggleAudioDevice.setImageResource(R.drawable.ic_speaker)
//                    //we should send a command to our service to switch between devices
//                    serviceRepository.toggleAudioDevice(RTCAudioManager.AudioDevice.EARPIECE.name)
//                } else {
//                    //we should set it to speaker mode
//                    toggleAudioDevice.setImageResource(R.drawable.ic_ear)
//                    serviceRepository.toggleAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE.name)
//
//                }
                isSpeakerMode = !isSpeakerMode
            }
        }
    }

    private fun setupCameraToggleClicked() {
        views.apply {
            toggleCameraButton.setOnClickListener {
//                if (!isCameraMuted) {
//                    serviceRepository.toggleVideo(true)
//                    toggleCameraButton.setImageResource(R.drawable.ic_camera_on)
//                } else {
//                    serviceRepository.toggleVideo(false)
//                    toggleCameraButton.setImageResource(R.drawable.ic_camera_off)
//                }
                isCameraMuted = !isCameraMuted
            }
        }
    }

    override fun onLatestEventReceived(data: DataModel) {
        showLog("data.type ${data.type}")
        if (data.isValid()) {
            when (data.type) {
                DataModelType.StartVideoCall,
                DataModelType.StartAudioCall -> {
                    setupViews()
                }

                DataModelType.EndCall -> {
                    endCall()
                }

                else -> Unit
            }
        }
    }

    private var isPreviousCallStateVideo = true

    private fun setupViews() {
        val isCaller = intent.getBooleanExtra("isCaller", false)
        val isVideoCall = intent.getBooleanExtra("isVideoCall", true)
        val target = intent.getStringExtra("target")
        val username = intent.getStringExtra("username")

        this.isPreviousCallStateVideo = isVideoCall
        mainRepository.setTarget(target!!)
        //initialize our widgets and start streaming our video and audio source
        //and get prepared for call
        mainRepository.initLocalSurfaceView(views.localView, isVideoCall)
        mainRepository.initRemoteSurfaceView(views.remoteView)

        if (!isCaller) {
            //start the video call
            mainRepository.startCall()
        }

        showLog("username $username")
//        //setup my clients
//        mainRepository.listener = this
//        mainRepository.initFirebase()
//        mainRepository.initWebrtcClient(username!!)
    }

    override fun endCall() {
        dismissCall()
        finish()
    }

//    override fun onCallEnded() {
//        finish()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        MainService.remoteSurfaceView?.release()
//        MainService.remoteSurfaceView = null
//
//        MainService.localSurfaceView?.release()
//        MainService.localSurfaceView = null
//    }
}