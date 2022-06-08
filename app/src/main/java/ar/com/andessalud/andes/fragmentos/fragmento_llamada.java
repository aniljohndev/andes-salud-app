package ar.com.andessalud.andes.fragmentos;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.principal;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static android.content.Context.WINDOW_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmento_llamada extends Fragment implements SensorEventListener {

    FragmentChangeTrigger trigger;
    private RtcEngine mRtcEngine;
    private static final String LOG_TAG = fragmento_llamada.class.getSimpleName();
    String idLlamada="", tipoLlamada="", signalRDestino="";
    Boolean videoActivo=true;
    ImageView videoBtn, audioBtn, leaveBtn;
    FrameLayout videoContainer;
    SurfaceView videoSurface;
    principal pantPrincipal = (principal) getActivity();
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;
//    private PowerManager pm;
//    private PowerManager.WakeLock mWakeLock;
//    private WindowManager mWindowManager;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    public fragmento_llamada() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_llamada, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (idLlamada.equals("")) {
            Bundle extras = getArguments();
            if (extras != null) {
                idLlamada = extras.getString("idLlamada", "");
                tipoLlamada = extras.getString("tipoLlamada", "");
                signalRDestino = extras.getString("signalRDestino", "");
            }
        }
        pantPrincipal = (principal) getActivity();
        pantPrincipal.cambiaHayLlamadaActiva(false);
        videoBtn = (ImageView) view.findViewById(R.id.videoBtn);
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaVideo();
            }
        });

        audioBtn = (ImageView) view.findViewById(R.id.audioBtn);
        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAudioMuteClicked(v);
            }
        });

        leaveBtn = (ImageView) view.findViewById(R.id.leaveBtn);
        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeaveChannelClicked(v);
            }
        });

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        videoContainer = getView().findViewById(R.id.floating_video_container);
        initAgoraEngine();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mSensorManager = (SensorManager) getActivity().getSystemService(getContext().SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
//        pm = (PowerManager) getActivity().getSystemService(getContext().POWER_SERVICE);
//        mWindowManager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
//        mWindowManager.getDefaultDisplay();
//
//        // Create a bright wake lock
//        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
//                .getName());
//        mWakeLock.acquire();
        mPowerManager=(PowerManager) getActivity().getSystemService(getContext().POWER_SERVICE);
    }

    private void initAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
        setupSession();
    }

    private void setupSession() {
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        if (tipoLlamada.equals("voz")){
            mRtcEngine.enableAudio();
            mRtcEngine.disableVideo();
        }else{
            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
            setupLocalVideoFeed();
        }

        mRtcEngine.joinChannel(null, idLlamada, "Extra Optional Data", 0);

        getView().findViewById(R.id.audioBtn).setVisibility(View.VISIBLE); // set the audio button hidden
        getView().findViewById(R.id.leaveBtn).setVisibility(View.VISIBLE); // set the leave button hidden
        getView().findViewById(R.id.videoBtn).setVisibility(View.VISIBLE); // set the video button hidden
    }

    private void setupLocalVideoFeed() {
        // setup the container for the local user
        videoContainer = getView().findViewById(R.id.floating_video_container);
        SurfaceView videoSurface = RtcEngine.CreateRendererView(getContext());
        videoSurface.setZOrderMediaOverlay(true);
        videoContainer.addView(videoSurface);
        mRtcEngine.setupLocalVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    private void setupRemoteVideoStream(int uid) {
        // setup ui element for the remote stream
        videoContainer = getView().findViewById(R.id.bg_video_container);
        // ignore any new streams that join the session
        if (videoContainer.getChildCount() >= 1) {
            return;
        }

        videoSurface = RtcEngine.CreateRendererView(getContext());
        videoContainer.addView(videoSurface);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(videoSurface, VideoCanvas.RENDER_MODE_FIT, uid));
        mRtcEngine.setRemoteSubscribeFallbackOption(io.agora.rtc.Constants.STREAM_FALLBACK_OPTION_AUDIO_ONLY);

    }

    private void cambiaVideo(){
        if (tipoLlamada.equals("voz")){
            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
            setupLocalVideoFeed();
            videoBtn.setSelected(true);
            tipoLlamada="video";
        }else{
            tipoLlamada="voz";
        }
        if (videoBtn.isSelected()) {
            videoBtn.setSelected(false);
            videoBtn.setImageResource(R.drawable.video_toggle_btn);
        } else {
            videoBtn.setSelected(true);
            videoBtn.setImageResource(R.drawable.video_toggle_active_btn);
        }
        mRtcEngine.muteLocalVideoStream(videoBtn.isSelected());
        //container = getView().findViewById(R.id.floating_video_container);
        videoContainer.setVisibility(videoBtn.isSelected() ? View.GONE : View.VISIBLE);
        videoSurface = (SurfaceView) videoContainer.getChildAt(0);
        videoSurface.setZOrderMediaOverlay(!videoBtn.isSelected());
        videoSurface.setVisibility(videoBtn.isSelected() ? View.GONE : View.VISIBLE);
    }

    public void onAudioMuteClicked(View view) {
        ImageView btn = (ImageView) view;
        if (btn.isSelected()) {
            btn.setSelected(false);
            btn.setImageResource(R.drawable.audio_toggle_btn);
        } else {
            btn.setSelected(true);
            btn.setImageResource(R.drawable.audio_toggle_active_btn);
        }

        mRtcEngine.muteLocalAudioStream(btn.isSelected());
    }

    public void onLeaveChannelClicked(View view) {
        leaveChannel();
        removeVideo(R.id.floating_video_container);
        removeVideo(R.id.bg_video_container);
        if (getView()!=null) {
            getView().findViewById(R.id.audioBtn).setVisibility(View.GONE); // set the audio button hidden
            getView().findViewById(R.id.leaveBtn).setVisibility(View.GONE); // set the leave button hidden
            getView().findViewById(R.id.videoBtn).setVisibility(View.GONE); // set the video button hidden
        }
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        idLlamada="";
        trigger.fireChange("llamada_finalizada");
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    private void removeVideo(int containerID) {
        if (getView()!=null) {
            FrameLayout videoContainer = getView().findViewById(containerID);
            videoContainer.removeAllViews();
        }
    }

    private void onRemoteUserVideoToggle(int uid, boolean toggle) {
        FrameLayout videoContainer = getView().findViewById(R.id.bg_video_container);

        SurfaceView videoSurface = (SurfaceView) videoContainer.getChildAt(0);
        videoSurface.setVisibility(toggle ? View.GONE : View.VISIBLE);

        // add an icon to let the other user know remote video has been disabled
        if(toggle){
            ImageView noCamera = new ImageView(getContext());
            noCamera.setImageResource(R.drawable.video_disabled);
            videoContainer.addView(noCamera);
        } else {
            ImageView noCamera = (ImageView) videoContainer.getChildAt(1);
            if(noCamera != null) {
                videoContainer.removeView(noCamera);
            }
        }
    }

    private void onRemoteUserLeft() {
        removeVideo(R.id.bg_video_container);
        onLeaveChannelClicked(getView());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            AudioManager audioManager = (AudioManager) getActivity().getSystemService(getContext().AUDIO_SERVICE);
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                    //near
                    //Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Intent.ACTION_SCREEN_OFF);
//                    startActivity(intent);
                    /*WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.screenBrightness = -1;
                    getActivity().getWindow().setAttributes(params);*/
                    mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "myapp:mywakelogtag");
                    mWakeLock.acquire();
                    audioManager.setSpeakerphoneOn(false);
                } else {
                    //far
                    //Toast.makeText(getApplicationContext(), "far", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Intent.ACTION_SCREEN_ON);
//                    startActivity(intent);
                    /*WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                    params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    params.screenBrightness = 1;
                    getActivity().getWindow().setAttributes(params);*/
                    mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myapp:mywakelogtag");
                    mWakeLock.acquire();
                    audioManager.setSpeakerphoneOn(true);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPause() {
        super.onPause();
        pantPrincipal = (principal) getActivity();
        if (!idLlamada.equals("")){
            pantPrincipal.cambiaHayLlamadaActiva(true);
        }else{
            pantPrincipal.cambiaHayLlamadaActiva(false);
        }
        mSensorManager.unregisterListener(this);
        //Log.e("Fragment is not visible", "Fragment is not visible");

        //Application Class.isFragmentShow = false;
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

    // Handle SDK Events
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onRemoteVideoStateChanged(final int uid, int state, int reason, int elapsed) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set first remote user to the main bg video container
                    setupRemoteVideoStream(uid);
                }
            });
        }

        @Override
        public void onRemoteAudioStateChanged(final int uid, int state, int reason, int elapsed) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set first remote user to the main bg video container
                    if (state==1
                        && tipoLlamada.equals("voz")){
                        return;
                        //videoBtn.setSelected(true);
                        //mRtcEngine.muteLocalVideoStream(videoBtn.isSelected());
                    }
                }
            });
        }

        // remote user has left channel
        @Override
        public void onUserOffline(int uid, int reason) { // Tutorial Step 7
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

        // remote user has toggled their video
        @Override
        public void onUserMuteVideo(final int uid, final boolean toggle) { // Tutorial Step 10
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoToggle(uid, toggle);
                }
            });
        }
    };
}
