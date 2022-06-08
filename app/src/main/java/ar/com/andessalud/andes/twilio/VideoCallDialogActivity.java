package ar.com.andessalud.andes.twilio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;

import static ar.com.andessalud.andes.splash.myCustomKey;

public class VideoCallDialogActivity

        extends AppCompatActivity implements View.OnClickListener {
    ImageView callPickVideo,callDisconnectVideo;
    String TAG = "StartCompaignDialogActivity";
    MediaPlayer mp;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    RelativeLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

               setContentView(R.layout.activity_video_call_dialog);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        builder = new  AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.videocalllayout,null);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        if (dialog.getWindow() != null)
        {
             window = dialog.getWindow();
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.TOP);
//            alert11.getWindow().setBackgroundDrawableResource(android.R.color.background_dark);
        }

        dialog.setCancelable(false);
        dialog.show();
        constraintLayout = view.findViewById(R.id.alermaindialog);
//        constraintLayout.getBackground().setAlpha(200);
//        Blurry.with(this).radius(25).sampling(2).onto(constraintLayout);

        callPickVideo = (ImageView) view.findViewById(R.id.callpickvid);
        callDisconnectVideo = (ImageView) view. findViewById(R.id.callpickend);
        callPickVideo.setOnClickListener(this);
        callDisconnectVideo.setOnClickListener(this);
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_RINGTONE );
        mp = MediaPlayer. create (getApplicationContext(), alarmSound);
        mp.start();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.callpickvid){
            Log.e(TAG,"customer press yes.");
            mp.stop();
            finish();
            Intent intent = new Intent(this, VideoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }else if (v.getId() == R.id.callpickend){
            Log.e(TAG,"customer press no.");
            try {
                getaccesstokenfromRoom();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mp.stop();
            finish();
        }
    }




//    public void showDialogOpener() {
////        dialogo.dismiss();
//        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
////        builder.setTitle("Information:");
//
//        // Set up the input
////        final ImageView input =new ImageView(this);
////        final ImageView input1 = new ImageView(this);
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
////        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
////        builder.setView(input);?
//        View viewInflated = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.videocalllayout, null, false);
//// Set up the input
//        final ImageView callPickVideo = (ImageView) viewInflated.findViewById(R.id.callpickvid);
//        final ImageView  callDisconnectVideo = (ImageView) viewInflated.findViewById(R.id.callpickend);
//        final ConstraintLayout constraintLayout = (ConstraintLayout) viewInflated.findViewById(R.id.callpickhead) ;
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        builder.setView(viewInflated);
//        callPickVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent splashIntent = new Intent(this,this);
////                splashIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
////                startActivity(splashIntent);
//            }
//        });
//        callDisconnectVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                constraintLayout.setVisibility(View.GONE);
//            }
//        });
//// Set up the buttons
////        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
//////                dialogo.show();
//////                name_edt = input.getText().toString();
//////                email_edt = input1.getText().toString();
////                changeActivity(getApplicationContext(),new VideoActivity());
////
////
////            }
////        });
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            builder.setPositiveButtonIcon(getDrawable(R.drawable.ic_video_call_white_24dp));
////            builder.setNegativeButtonIcon(getDrawable(R.drawable.end_call));
////        }
//
////        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                dialog.cancel();
////            }
////        });
//
//        builder.show();
//
//    }
    public static void changeActivity(Context context, Activity activity) {
        Intent in = new Intent();
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setClass(context, activity.getClass());
        context.startActivity(in);
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        new CountDownTimer(
//                30000
////                300000
//
//                , 1000) {
//
//            public void onTick(long millisUntilFinished) {
////                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//                //here you can have your logic to set text to edittext
//            }
//
//            public void onFinish() {
////                mTextField.setText("done!");
//                if(mp.isPlaying())
//                {
//                    mp.stop();
//                    finish();
//
//                }
//
//                }
//
//        }.start();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null)
        {
            dialog.dismiss();
            dialog = null;
        }
        EventBus.getDefault().unregister(this);
    }



    private void getaccesstokenfromRoom() throws JSONException {
//        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
//        String URLs = "http://discoveritech.org/staff-dashboard/api/disconnect-call";
        String URLs =         this.getResources().getString(R.string.srv_localdiscoveritech).concat("disconnect-call");

        Log.d("urldata", "" + URLs);
        JSONObject deviceobj = new JSONObject();
        Log.d("room_name ", myCustomKey);

        deviceobj.put("room_name", myCustomKey);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, deviceobj, response -> {
            Log.d("responseData",""+response);
            }, error -> {
//            dialogo.dismiss();
//            showDialogOpener();
            Log.d(TAG, String.valueOf(error));
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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(String evento){
        if (evento.equals("callmissed")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    btnNotificaciones.setImageResource(R.drawable.notificacion_red);
                    if(mp.isPlaying())
                    {
                        mp.stop();
                        finish();

                    }
                }
            });
        }
    }

}

