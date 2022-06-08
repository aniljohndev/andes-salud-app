package ar.com.andessalud.andes.twilio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendingMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getExtras().getString("room_name").contains("meeting")
//        )
//        {
//            changeActivity(context,new VideoCallDialogActivity());
//
//        }
        Log.d("valueffffff",intent.getAction());

    }



    public  void changeActivity(Context context, Activity activity) {
        Intent in = new Intent();
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setClass(context, activity.getClass());
        context.startActivity(in);
    }
}
