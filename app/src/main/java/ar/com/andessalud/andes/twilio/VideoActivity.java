package ar.com.andessalud.andes.twilio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;

import
        androidx.appcompat.app.ActionBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.twilio.audioswitch.AudioDevice;
import com.twilio.audioswitch.AudioSwitch;
import com.twilio.video.AudioCodec;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.G722Codec;
import com.twilio.video.H264Codec;
import com.twilio.video.IsacCodec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.PcmaCodec;
import com.twilio.video.PcmuCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;
import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.BuildConfig;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.fragmentos.fragmento_contact_center;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.util.CameraCapturerCompat;
import kotlin.Unit;
import tvi.webrtc.VideoSink;

import static ar.com.andessalud.andes.fragmentos.fragmento_contact_center.sendingJson;
import static ar.com.andessalud.andes.splash.myCustomKey;
import static ar.com.andessalud.andes.splash.myCustomKeyfordoctor;


public class VideoActivity extends AppCompatActivity {
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "VideoActivity";
    AudioManager audioManager;
    String accesstoken = "";
    //    String roomname =  "";
//    String tokenNoformeeting = "";
    String accesstokenForMeeting = "";
    String backtag = "";
    /*
     * Audio and video tracks can be created with names. This feature is useful for categorizing
     * tracks of participants. For example, if one participant publishes a video track with
     * ScreenCapturer and CameraCapturer with the names "screen" and "camera" respectively then
     * other participants can use RemoteVideoTrack#getName to determine which video track is
     * produced from the other participant's screen or camera.
     */
    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";
    android.app.Dialog dialogo;
    Boolean isDialogShow = false;
    //    android.app.Dialog dialogDisconnect,dialogdiscfrommob;
    Activity context;
    /*
     * You must provide a Twilio Access Token to connect to the Video service
     */
//    private static final String TWILIO_ACCESS_TOKEN = BuildConfig.TWILIO_ACCESS_TOKEN;
//    private static final String ACCESS_TOKEN_SERVER = BuildConfig.TWILIO_ACCESS_TOKEN_SERVER;F

    /*
     * Access token used to connect. This field will be set either from the console generated token
     * or the request to the token server.
     */
    private String accessToken;

    /*
     * A Room represents communication between a local participant and one or more participants.
     */
    private Room room;
    private LocalParticipant localParticipant;

    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
    private AudioCodec audioCodec;
    private VideoCodec videoCodec;

    /*
     * Encoding parameters represent the sender side bandwidth constraints.
     */
    private EncodingParameters encodingParameters;

    /*
     * A VideoView receives frames from a local or remote video track and renders them
     * to an associated view.
     */
    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;

    /*
     * Android shared preferences used for settings
     */
    private SharedPreferences preferences;

    /*
     * Android application UI elements
     */
    private CameraCapturerCompat cameraCapturerCompat;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private FloatingActionButton connectActionFab;
    private FloatingActionButton switchCameraActionFab;
    private FloatingActionButton localVideoActionFab;
    private FloatingActionButton muteActionFab;
    private ProgressBar reconnectingProgressBar;
    private AlertDialog connectDialog;
    private String remoteParticipantIdentity;
    Window window;
    /*
     * Audio management
     */
    private AudioSwitch audioSwitch;
    private int savedVolumeControlStream;
    private MenuItem audioDeviceMenuItem;

    private VideoSink localVideoView;
    private boolean disconnectedFromOnDestroy;
    private boolean enableAutomaticSubscription;
    String tokenid, idAfiliadoRegistro;
    String name_edt, email_edt;
    Toolbar toolbar;
    SharedPreferences appPref, tref;
    final Activity actActual = this;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_video);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        /*if (getIntent().hasExtra("joinappointment")){
            backtag = "webtoappdoctor";
            Log.d("checkignResultVideo",getIntent().getExtras().getString("joinappointment"));
            try {
                getConnectToWebAppointment(getIntent().getExtras().getString("joinappointment"));
             } catch (JSONException e) {
                e.printStackTrace();
            }

        }*/
            if (myCustomKeyfordoctor.contains("meeting")) {
                Log.d("chekckingdatamycustom",myCustomKeyfordoctor);
                myCustomKey = "";
                try {
                    backtag = "webtoappdoctor";
                    getConnectToWebAppointment(myCustomKeyfordoctor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().hide();
        toolbar = findViewById(R.id.toolbarvid);
        setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.backbtn);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Andes Salud Soporte");
        appPref = actActual.getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        prefs = getSharedPreferences("datosAplicacion", MODE_PRIVATE);
        idAfiliadoRegistro = prefs.getString("idAfiliadoRegistro", "");
//        tokenid = FirebaseMessaging.INSTANCE_ID_SCOPE;
        tokenid = appPref.getString("senderid", "");
        Log.d("affiliateId", idAfiliadoRegistro);
//        CollectData(idAfiliadoRegistro);
//        this.getSupportActionBar().show();
        primaryVideoView = findViewById(R.id.primary_video_view);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);
        reconnectingProgressBar = findViewById(R.id.reconnecting_progress_bar);
//        Log.d("InformatioNGuide", BuildConfig.TWILIO_ACCOUNT_SID + AccessTokenClass.generateToken());
        connectActionFab = findViewById(R.id.connect_action_fab);
        switchCameraActionFab = findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = findViewById(R.id.local_video_action_fab);
        muteActionFab = findViewById(R.id.mute_action_fab);
        tref = this.getSharedPreferences("accesstokenroom", MODE_PRIVATE);


        /*
         * Get shared preferences to read settings
         * Get shared preferences to read settings
         */

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*
         * Setup audio management and set the volume control stream
         */
        audioSwitch = new AudioSwitch(getApplicationContext());
        savedVolumeControlStream = getVolumeControlStream();
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        audioManager.setSpeakerphoneOn(true);
        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            createAudioAndVideoTracks();
            setAccessToken();
        }


        if (myCustomKey.contains("meeting")) {
            Log.d("chekckingdatamycustom",myCustomKey);
            try {
                getaccesstokenfromRoom();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }









        try {
            String valfromIntent = getIntent().getStringExtra("connectMobileToWeb");
            if (valfromIntent.contains("connectedcall")) {
//
                connectMobileToWeb();

                Log.d("conecrtdgif", valfromIntent);

            } else {
                Log.d("conecrtdgel", valfromIntent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        else {
//            dialogo.dismiss();
//
//        }

        /*
         * Set the initial state of the UI
         */
        intializeUI();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_activity, menu);
        audioDeviceMenuItem = menu.findItem(R.id.menu_audio_device);

        /*
         * Start the audio device selector after the menu is created and update the icon when the
         * selected audio device changes.
         */
        audioSwitch.start((audioDevices, audioDevice) -> {
            updateAudioDeviceIcon(audioDevice);
            return Unit.INSTANCE;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.menu_audio_device:
                showAudioDevices();
                return true;
//            case R.id.home:
//                onBackPressed();
//                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;

            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks();
                setAccessToken();
            } else {
                Toast.makeText(this,
                        R.string.permissions_needed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Update preferred audio and video codec in case changed in settings
         */
        audioCodec = getAudioCodecPreference(
        );
        videoCodec = getVideoCodecPreference(
        );
        enableAutomaticSubscription = getAutomaticSubscriptionPreference(
        );
        /*
         * Get latest encoding parameters
         */
        final EncodingParameters newEncodingParameters = getEncodingParameters();

        /*
         * If the local video track was released when the app was put in the background, recreate.
         */
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(this,
                    true,
                    cameraCapturerCompat,
                    LOCAL_VIDEO_TRACK_NAME);
            localVideoTrack.addSink(localVideoView);

            /*
             * If connected to a Room then share the local video track.
             */
            if (localParticipant != null) {
                localParticipant.publishTrack(localVideoTrack);

                /*
                 * Update encoding parameters if they have changed.
                 */
                if (!newEncodingParameters.equals(encodingParameters)) {
                    localParticipant.setEncodingParameters(newEncodingParameters);
                }
            }
        }

        /*
         * Update encoding parameters
         */
        encodingParameters = newEncodingParameters;

        /*
         * Update reconnecting UI
         */
        if (room != null) {
            reconnectingProgressBar.setVisibility((room.getState() != Room.State.RECONNECTING) ?
                    View.GONE :
                    View.VISIBLE);
        }

    }

    @Override
    protected void onPause() {
        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        if (localVideoTrack != null) {
            /*
             * If this local video track is being shared in a Room, unpublish from room before
             * releasing the video track. Participants will be notified that the track has been
             * unpublished.
             */
            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
            }

            localVideoTrack.release();
            localVideoTrack = null;
        }


        SharedPreferences.Editor editor = tref.edit();
        editor.putString("roomnamecheck", myCustomKey);
        editor.commit();


        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /*
         * Tear down audio management and restore previous volume stream
         */
        audioSwitch.stop();
        setVolumeControlStream(savedVolumeControlStream);
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
            disconnectedFromOnDestroy = true;


        }

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }

        super.onDestroy();
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    R.string.permissions_needed,
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void createAudioAndVideoTracks() {
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);

        // Share your camera
        cameraCapturerCompat = new CameraCapturerCompat(this, CameraCapturerCompat.Source.FRONT_CAMERA);
        localVideoTrack = LocalVideoTrack.create(this,
                true,
                cameraCapturerCompat,
                LOCAL_VIDEO_TRACK_NAME);
        primaryVideoView.setMirror(true);
        localVideoTrack.addSink(primaryVideoView);
        localVideoView = primaryVideoView;
    }


    private void getaccesstokenfromRoom() throws JSONException {
//        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
        dialogo = fabrica_dialogos.dlgBuscandowait(this, "Espere por favor...");
        dialogo.show();


        String URLs =
//                "http://discoveritech.org/staff-dashboard/api/attend-call";
//
                this.getResources().getString(R.string.srv_localdiscoveritech).concat("attend-call");
        Log.d("urldata", "" + URLs);
        JSONObject deviceobj = new JSONObject();
        Log.d("room_name ", myCustomKey);

        deviceobj.put("room_name", myCustomKey);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, deviceobj, response -> {

            try {
                Boolean stats = response.getBoolean("status");
                Log.d("statsval", "" + stats);
                Log.d("responseasa", String.valueOf(response));
                if (stats.equals(true)) {
                    JSONObject responseObject = response.getJSONObject("response");
                    JSONObject detailObject = responseObject.getJSONObject("detail");
                    accesstokenForMeeting = detailObject.getString("accessToken");
                    connectToRoom(myCustomKey);

                }


            } catch (JSONException e) {
                Log.e("catchingJson", "" + e);

                e.printStackTrace();
            }
        }, error -> {
            Log.d("casdad", String.valueOf(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }

        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void getConnectToWebAppointment(String appointment) throws JSONException {
//        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
        dialogo = fabrica_dialogos.dlgBuscandowait(this, "Espere por favor...");
        dialogo.show();


        String URLs = "https://digitalech.com/doctor-panel/api/".concat("connect-to-web-for-appointment");
        Log.d("urldata", "" + URLs);
        JSONObject deviceobj = new JSONObject();
        Log.d("room_name ", appointment);

        deviceobj.put("room_name", appointment);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, deviceobj, response -> {

            try {
                Boolean stats = response.getBoolean("status");
                Log.d("statsval", "" + stats);
                Log.d("responseasa", String.valueOf(response));
                if (stats.equals(true)) {
                    JSONObject responseObject = response.getJSONObject("response");
                    JSONObject detailObject = responseObject.getJSONObject("detail");
                    accesstokenForMeeting = detailObject.getString("accessToken");
                    connectToRoom(appointment);

                }


            } catch (JSONException e) {
                Log.e("catchingJson", "" + e);

                e.printStackTrace();
            }
        }, error -> {
            Log.d("casdad", String.valueOf(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }

        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void setAccessToken() {
    }

    private void connectToRoom(String roomName) {
//        audioSwitch.activate();
        Log.d("accesstokensdadasd", accesstokenForMeeting);

        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accesstokenForMeeting)
                .roomName(roomName);

        /*
         * Add local audio track to connect options to share with participants.
         */
        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }

        /*
         * Add local video track to connect options to share with participants.
         */
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }

        /*
         * Set the preferred audio and video codec for media.
         */
        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));
        connectOptionsBuilder.preferVideoCodecs(Collections.singletonList(videoCodec));

        /*
         * Set the sender side encoding parameters.
         */
        connectOptionsBuilder.encodingParameters(encodingParameters);

        /*
         * Toggles automatic track subscription. If set to false, the LocalParticipant will receive
         * notifications of track publish events, but will not automatically subscribe to them. If
         * set to true, the LocalParticipant will automatically subscribe to tracks as they are
         * published. If unset, the default is true. Note: This feature is only available for Group
         * Rooms. Toggling the flag in a P2P room does not modify subscription behavior.
         */
        connectOptionsBuilder.enableAutomaticSubscription(enableAutomaticSubscription);

        room = Video.connect(this, connectOptionsBuilder.build(), roomListener());
        setDisconnectAction();
    }

    /*
     * The initial state when there is no active room.
     */
    private void intializeUI() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_call_decline));
        connectActionFab.show();
        connectActionFab.setOnClickListener(connectActionClickListener());
        switchCameraActionFab.show();
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        localVideoActionFab.show();
        localVideoActionFab.setOnClickListener(localVideoClickListener());
        muteActionFab.show();
        muteActionFab.setOnClickListener(muteClickListener());
    }

    /*
     * Show the current available audio devices.
     */
    private void showAudioDevices() {
        AudioDevice selectedDevice = audioSwitch.getSelectedAudioDevice();
        List<AudioDevice> availableAudioDevices = audioSwitch.getAvailableAudioDevices();

        if (selectedDevice != null) {
            int selectedDeviceIndex = availableAudioDevices.indexOf(selectedDevice);

            ArrayList<String> audioDeviceNames = new ArrayList<>();
            String[] addingDevice =  new String[2];
            addingDevice[0] = "Telefono";
            addingDevice[1] = "Altavoces";
/*
            for (AudioDevice a : availableAudioDevices) {
                audioDeviceNames.add(a.getName());

            }*/
            Collections.addAll(audioDeviceNames, addingDevice);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.room_screen_select_device)
                    .setSingleChoiceItems(
                            audioDeviceNames.toArray(new CharSequence[0]),
                            selectedDeviceIndex,
                            (dialog, index) -> {
                                if (index == 0)
                                {

                                    audioManager.setSpeakerphoneOn(false);
                                }
                                else if (index == 1)
                                {
                                    audioManager.setSpeakerphoneOn(true);
                                }
                                dialog.dismiss();
                                AudioDevice selectedAudioDevice = availableAudioDevices.get(index);
                                updateAudioDeviceIcon(selectedAudioDevice);
                                audioSwitch.selectDevice(selectedAudioDevice);
                            }).create().show();
        }
    }

    /*
     * Update the menu icon based on the currently selected audio device.
     */
    private void updateAudioDeviceIcon(AudioDevice selectedAudioDevice) {
        int audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp;

        if (selectedAudioDevice instanceof AudioDevice.BluetoothHeadset) {
            audioDeviceMenuIcon = R.drawable.ic_bluetooth_white_24dp;
        } else if (selectedAudioDevice instanceof AudioDevice.WiredHeadset) {
            audioDeviceMenuIcon = R.drawable.ic_headset_mic_white_24dp;
        } else if (selectedAudioDevice instanceof AudioDevice.Earpiece) {
            audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp;
        } else if (selectedAudioDevice instanceof AudioDevice.Speakerphone) {
            audioDeviceMenuIcon = R.drawable.ic_volume_up_white_24dp;
        }

        audioDeviceMenuItem.setIcon(audioDeviceMenuIcon);
    }

    /*
     * Get the preferred audio codec from shared preferences
     */
    private AudioCodec getAudioCodecPreference() {
        final String audioCodecName = preferences.getString(SettingActivity.PREF_AUDIO_CODEC, SettingActivity.PREF_AUDIO_CODEC_DEFAULT);

        switch (audioCodecName) {
            case IsacCodec.NAME:
                return new IsacCodec();
            case OpusCodec.NAME:
                return new OpusCodec();
            case PcmaCodec.NAME:
                return new PcmaCodec();
            case PcmuCodec.NAME:
                return new PcmuCodec();
            case G722Codec.NAME:
                return new G722Codec();
            default:
                return new OpusCodec();
        }
    }

    /*
     * Get the preferred video codec from shared preferences
     */
    private VideoCodec getVideoCodecPreference() {
        final String videoCodecName = preferences.getString(SettingActivity.PREF_VIDEO_CODEC, SettingActivity.PREF_VIDEO_CODEC_DEFAULT);

        switch (videoCodecName) {
            case Vp8Codec.NAME:
                boolean simulcast = preferences.getBoolean(SettingActivity.PREF_VP8_SIMULCAST,
                        SettingActivity.PREF_VP8_SIMULCAST_DEFAULT);
                return new Vp8Codec(simulcast);
            case H264Codec.NAME:
                return new H264Codec();
            case Vp9Codec.NAME:
                return new Vp9Codec();
            default:
                return new Vp8Codec();
        }
    }

    private boolean getAutomaticSubscriptionPreference() {
        return preferences.getBoolean(SettingActivity.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION, SettingActivity.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION_DEFAULT);
    }

    private EncodingParameters getEncodingParameters() {
        final int maxAudioBitrate = Integer.parseInt(
                preferences.getString(SettingActivity.PREF_SENDER_MAX_AUDIO_BITRATE,
                        SettingActivity.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT));
        final int maxVideoBitrate = Integer.parseInt(
                preferences.getString(SettingActivity.PREF_SENDER_MAX_VIDEO_BITRATE,
                        SettingActivity.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT));

        return new EncodingParameters(maxAudioBitrate, maxVideoBitrate);
    }

    /*
     * The actions performed during disconnect.
     */
    private void setDisconnectAction() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_call_decline));
        connectActionFab.show();
        connectActionFab.setOnClickListener(disconnectClickListener());
    }

    /*
     * Creates an connect UI dialog
     */
    private void showConnectDialog() {
//        EditText roomEditText = new EditText(this);
//        connectDialog = Dialog.createConnectDialog(roomEditText,
//                connectClickListener(roomEditText),
//                cancelConnectDialogClickListener(),
//                this);
//        connectDialog.show();
    }

    /*
     * Called when remote participant joins the room
     */
    @SuppressLint("SetTextI18n")
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            Snackbar.make(connectActionFab,
                    "Multiple participants are not currently support in this UI",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        remoteParticipantIdentity = remoteParticipant.getIdentity();

        /*
         * Add remote participant renderer
         */
        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Only render video tracks that are subscribed to
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(Objects.requireNonNull(remoteVideoTrackPublication.getRemoteVideoTrack()));
            }
        }

        /*
         * Start listening for participant events
         */
        remoteParticipant.setListener(remoteParticipantListener());
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        primaryVideoView.setMirror(false);
        videoTrack.addSink(primaryVideoView);
    }

    private void moveLocalVideoToThumbnailView() {
        if (thumbnailVideoView.getVisibility() == View.GONE) {
            thumbnailVideoView.setVisibility(View.VISIBLE);
            localVideoTrack.removeSink(primaryVideoView);

            localVideoTrack.addSink(thumbnailVideoView);
            localVideoView = thumbnailVideoView;
            thumbnailVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                    CameraCapturerCompat.Source.FRONT_CAMERA);
        }

    }

    /*
     * Called when remote participant leaves the room
     */
    @SuppressLint("SetTextI18n")
    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {
            return;
        }

        /*
         * Remove remote participant renderer
         */
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Remove video only if subscribed to participant track
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(Objects.requireNonNull(remoteVideoTrackPublication.getRemoteVideoTrack()));
            }
        }
        moveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeSink(primaryVideoView);
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            thumbnailVideoView.setVisibility(View.GONE);
            if (localVideoTrack != null) {
                localVideoTrack.removeSink(thumbnailVideoView);
                localVideoTrack.addSink(primaryVideoView);
            }
            localVideoView = primaryVideoView;
            primaryVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                    CameraCapturerCompat.Source.FRONT_CAMERA);
        }
    }

    /*
     * Room events listener
     */
    @SuppressLint("SetTextI18n")
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(@NotNull Room room) {
                localParticipant = room.getLocalParticipant();
                setTitle(room.getName());
                Log.d("res-Roomconnected", room.getName());
//                connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//
//
//                if(connectActionFab.getBackgroundTintList() == ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)))
//                {
//                    connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//
//                }
                if (room.getRemoteParticipants().size() > 0) {
                    connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                }

                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
//                mTextField.setText("done!");
                        try {
                            if (room.getRemoteParticipants().size() == 0) {

                                dialogo.dismiss();
                                EventBus.getDefault().post("callmissed");
                            }
                        }
                        catch (Exception ignored)
                        {

                        }

                    }

                }.start();

//                dialogo.dismiss();




                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
//                    connectActionFab.setBackgroundColor(Color.parseColor(String.valueOf(R.color.colorAccent)));


                    dialogo.dismiss();
                    break;
                }
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                reconnectingProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                reconnectingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onConnectFailure(@NotNull Room room, @NotNull TwilioException e) {
                audioSwitch.deactivate();

                intializeUI();
            }

            @Override
            public void onDisconnected(@NotNull Room room, TwilioException e) {
//                myCustomKey = "";
//                dialogo.dismiss();
                localParticipant = null;
                reconnectingProgressBar.setVisibility(View.GONE);
                if (connectActionFab.getBackgroundTintList() == ColorStateList.valueOf(getResources().getColor(R.color.colorAccent))) {
                    connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                }
                VideoActivity.this.room = null;
                if (!disconnectedFromOnDestroy) {
             /*       dialogo = fabrica_dialogos.dlgBuscandowait(context, "Personal desconectado desconectada..");
                    dialogo.show();
*/
                    new CountDownTimer(3000, 1000) {
                        public void onTick(long millisUntilFinished) {
//                            VideoActivity.this.room = null;

                        }

                        public void onFinish() {
                            try {
                                dialogo.dismiss();
                                audioSwitch.deactivate();
                                intializeUI();
                                moveLocalVideoToPrimaryView();
                             moveBackToMain();
                            } catch (Exception ignored) {

                            }

                        }

                    }.start();


                }

            }

            @Override
            public void onParticipantConnected(@NotNull Room room, @NotNull RemoteParticipant remoteParticipant) {
                Log.d("res-Participant", remoteParticipant.getIdentity());
                addRemoteParticipant(remoteParticipant);
                connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                isDialogShow = true;
                dialogo.dismiss();
            }

            @Override
            public void onParticipantDisconnected(@NotNull Room room, @NotNull RemoteParticipant remoteParticipant) {
                removeRemoteParticipant(remoteParticipant);

            }


            @Override
            public void onRecordingStarted(Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "res-recording");
            }

            @Override
            public void onRecordingStopped(Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d(TAG, "res-recStop");
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(@NotNull RemoteParticipant remoteParticipant,
                                              @NotNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
            }

            @Override
            public void onAudioTrackUnpublished(@NotNull RemoteParticipant remoteParticipant,
                                                @NotNull RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
            }

            @Override
            public void onDataTrackPublished(@NotNull RemoteParticipant remoteParticipant,
                                             @NotNull RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
            }

            @Override
            public void onDataTrackUnpublished(@NotNull RemoteParticipant remoteParticipant,
                                               @NotNull RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
            }

            @Override
            public void onVideoTrackPublished(@NotNull RemoteParticipant remoteParticipant,
                                              @NotNull RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {
                Log.i(TAG, String.format("onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                removeParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                Snackbar.make(connectActionFab,
                        String.format("Failed to subscribe to %s video track",
                                remoteParticipant.getIdentity()),
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }

    private DialogInterface.OnClickListener connectClickListener(final EditText roomEditText) {
        return (dialog, which) -> {
            /*
             * Connect to room
             */
//            connectToRoom(roomEditText.getText().toString());
        };
    }

    private View.OnClickListener disconnectClickListener() {
        return v -> {
            /*
             * Disconnect from room
             */
            if (room != null) {
                room.disconnect();


            }
            intializeUI();
        };
    }

    private View.OnClickListener connectActionClickListener() {
        return v -> showConnectDialog();
    }

    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
        return (dialog, which) -> {
            intializeUI();
            connectDialog.dismiss();
        };
    }

    private View.OnClickListener switchCameraClickListener() {
        return v -> {
            if (cameraCapturerCompat != null) {
                CameraCapturerCompat.Source cameraSource = cameraCapturerCompat.getCameraSource();
                cameraCapturerCompat.switchCamera();
                if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                    thumbnailVideoView.setMirror(cameraSource == CameraCapturerCompat.Source.BACK_CAMERA);
                } else {
                    primaryVideoView.setMirror(cameraSource == CameraCapturerCompat.Source.BACK_CAMERA);
                }
            }
        };
    }

    private View.OnClickListener localVideoClickListener() {
        return v -> {
            /*
             * Enable/disable the local video track
             */
            if (localVideoTrack != null) {
                boolean enable = !localVideoTrack.isEnabled();
                localVideoTrack.enable(enable);
                int icon;
                if (enable) {
                    icon = R.drawable.ic_videocam_white_24dp;
                    switchCameraActionFab.show();
                } else {
                    icon = R.drawable.ic_videocam_off_black_24dp;
                    switchCameraActionFab.hide();
                }
                localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(VideoActivity.this, icon));
            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return v -> {
            /*
             * Enable/disable the local audio track. The results of this operation are
             * signaled to other Participants in the same Room. When an audio track is
             * disabled, the audio is muted.
             */
            if (localAudioTrack != null) {
                boolean enable = !localAudioTrack.isEnabled();
                localAudioTrack.enable(enable);
                int icon = enable ?
                        R.drawable.ic_mic_white_24dp : R.drawable.ic_mic_off_black_24dp;
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                        VideoActivity.this, icon));
            }
        };
    }

    /*private void retrieveAccessTokenfromServer() {
        Ion.with(this)
                .load(String.format("%s?identity=%s", ACCESS_TOKEN_SERVER,
                        UUID.randomUUID().toString()))
                .asString()
                .setCallback((e, token) -> {
                    if (e == null) {
                        VideoActivity.this.accessToken = token;
                    } else {
                        Toast.makeText(VideoActivity.this,
                                R.string.error_retrieving_access_token, Toast.LENGTH_LONG)
                                .show();
                    }
                });
    }*/


    /*  private void getCustomers() {
  //        dialogo = fabrica_dialogos.dlgBuscando(this, "Espere por favor...");
          dialogo = fabrica_dialogos.dlgBuscandowait(this, "Espere por favor...");

          dialogo.show();
          String URLs = "https://digitalech.com/staff-dashboard/api/get-customer";
          Log.d("urldata", "" + URLs);
          JSONObject deviceobjs = new JSONObject();
          Log.d("gtidAfiliado",idAfiliadoRegistro);
          Log.d("gtidAfiliado",idAfiliadoRegistro);
  //        Log.d("devictoken",tokenid);
          try {
              deviceobjs.put("idAfiliado", idAfiliadoRegistro);
          } catch (JSONException e) {
              e.printStackTrace();
          }
          Log.d("devicetag",""+deviceobjs);

          JsonObjectRequest jsonObjectRequests = new JsonObjectRequest(Request.Method.POST, URLs, deviceobjs, response -> {

              try {
                  Log.d("gtidAfiliado", "" + response);
                  Boolean stats = response.getBoolean("status");
                  Log.d("sdadasda", "" + stats);
                  Log.d("statsdadsval", "" + response);
                  if (stats.equals(true)) {

                      connectMobileToWeb();
                  }

              } catch (JSONException e) {
                  Log.d("catchingJson", "" + e);
                  //                    createCustomer(sendingJson);

                  e.printStackTrace();
              }
          }, error -> {
  //            dialogo.dismiss();
              try {

                  Log.d("datasdas",""+error.getMessage());
                  createCustomer(sendingJson);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
              Log.d("getError", String.valueOf(error.networkResponse.statusCode));
          }) {

          };
          MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequests);

      }


      private void createCustomer(JSONObject params) throws JSONException {
          Log.d("createadata",""+params);

          String URLs = "http://discoveritech.org/staff-dashboard/api/customers";
          Log.d("urldata", "" + URLs);

          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {

              try {
                  Log.d("responseError",""+response);
                  boolean stats = response.getBoolean("status");
                  Log.d("createCustomer", "" + stats);
                  if (stats) {

                  }
              } catch (JSONException e) {

                  e.printStackTrace();
              }


          }, error -> {
              dialogo.dismiss();
              Log.d("eDASDADS", ""+error.getMessage());
          }) {
              @Override
              public Map<String, String> getHeaders() {
                  Map<String, String> pars = new HashMap<String, String>();
                  pars.put("Content-Type", "application/form-data");
                  return pars;
              }
          };
          MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

      }
  */
    private void connectMobileToWeb()
            throws JSONException {
//        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
//        dialogo.show();
//        getCustomers();
        dialogo = fabrica_dialogos.dlgBuscandowait(this, "Espere por favor...");
        dialogo.show();

//        dialogo.show();
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("app-meeting");

//                "http://discoveritech.org/staff-dashboard/api/app-meeting";
        Log.d("urldata", "" + URLs);
        JSONObject deviceobj = new JSONObject();
        Log.d("idAfiliado", tokenid);
        deviceobj.put("idAfiliado", idAfiliadoRegistro);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, deviceobj, response -> {

            try {
                Log.d("res-mobiletoweb", String.valueOf(response));
//                dialogo.dismiss();
////                dialogo.dismiss();
//                JSONObject jsonObject = response.getJSONObject("meeting_detail");
//                JSONObject jsonObject1data = jsonObject.getJSONObject("data");
//                numberInput = jsonObject1data.getString("id");
//                passwordInput = jsonObject1data.getString("password");
//                dialogo.dismiss();
                Boolean stats = response.getBoolean("status");
                Log.d("statsvalsss", "" + stats);
                if (stats.equals(true)) {
                    JSONObject responseObject = response.getJSONObject("response");
                    JSONObject detailObject = responseObject.getJSONObject("detail");

                    accesstokenForMeeting = detailObject.getString("accessToken");

//                    editor.putString("senderidEnviado", "0");
                    //                    getCurrentStaffMember();
                    Log.d("roomanameeeee", accesstokenForMeeting);
//                    dialogo.dismiss();

                    connectToRoom(detailObject.getString("roomName"));
                }

//                createJoinMeetingDialog();

            } catch (JSONException e) {
                Log.e("res-catchingmob", "" + e);

                e.printStackTrace();
            }
        }, error -> {
//            dialogo.dismiss();
//            showDialogOpener();
//            dialogo.dismiss();
//            dialogo = fabrica_dialogos.dlgError(this,"Something went wrong Try again later");
//            dialogo.show();
            Log.d("res-moberror", String.valueOf(error));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }

        };
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }


    @Override
    public void onBackPressed() {
        if (isDialogShow.equals(false)) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("¿Abandonar Video llamada?")
                    .setMessage("¿Estas seguro de abandonar la llamada?")
                    .setPositiveButton("SI", (dialog, which) -> moveBackToMain())
                    .setNegativeButton("No", null)
                    .show();
        }


    }
    private  void moveBackToMain(){
        if (backtag.equals("webtoappdoctor"))
        {
            Intent intentforsend = new Intent(this, principal.class);
//            intentforsend.putExtra("joinappointment",result);
            startActivity(intentforsend);
        }
        else {
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (dialogo != null) {
            dialogo.dismiss();
            dialogo = null;
        }
        if (myCustomKey.contains("meeting")) {
            myCustomKey = "";
        }

        if (myCustomKeyfordoctor.contains("meeting")) {
            myCustomKeyfordoctor = "";
        }
    }


    @Subscribe
    public void onEvent(String evento) {
        Log.d("messageaad",evento);
        if (evento.equals("callended")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    btnNotificaciones.setImageResource(R.drawable.notificacion_red);
                    dialogo = fabrica_dialogos.dlgBuscandowait(context, "Personal desconectado Rredirecting..");
                    dialogo.show();


                    if (connectActionFab.getBackgroundTintList() == ColorStateList.valueOf(getResources().getColor(R.color.colorAccent))) {
                        connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        isDialogShow = true;
                    }
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
//                mTextField.setText("done!");
                            if (dialogo != null) {
                                dialogo.dismiss();

                            }

                            finish();

                        }

                    }.start();

                }
            });
        }
        else if (evento.equals("callendedforappointment")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    btnNotificaciones.setImageResource(R.drawable.notificacion_red);
                    dialogo = fabrica_dialogos.dlgBuscandowait(context, "Personal desconectado Rredirecting..");
                    dialogo.show();


                    if (connectActionFab.getBackgroundTintList() == ColorStateList.valueOf(getResources().getColor(R.color.colorAccent))) {
                        connectActionFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        isDialogShow = true;
                    }
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
//                mTextField.setText("done!");
                            if (dialogo != null) {
                                dialogo.dismiss();

                            }

                        }

                    }.start();

                }
            });
        }

       else  if (evento.equals("callmissed")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   Intent intent = new Intent();
                    intent.putExtra("missedcalls", "callmissed");
                    setResult(1001, intent);
                    finish();

                }
            });
        }

    }


        }

