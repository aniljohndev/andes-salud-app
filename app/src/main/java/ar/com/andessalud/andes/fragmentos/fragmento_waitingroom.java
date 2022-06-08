package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.splash;
import ar.com.andessalud.andes.twilio.VideoActivity;


public class fragmento_waitingroom extends Fragment {
    private Dialog dialogo;
    principal pantPrincipal;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragmento_waitingroom, container, false);

        return v;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultReceived(String result) {
        if (result.contains("meeting")){
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");

            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                }

                public void onFinish() {
//                mTextField.setText("done!");
                    try {
                        splash.myCustomKeyfordoctor = "";
                        dialogo.dismiss();
                        Intent intentforsend = new Intent(getContext(), VideoActivity.class);
                        splash.myCustomKeyfordoctor = result;

                         startActivity(intentforsend);
                        pantPrincipal.overridePendingTransition(R.anim.entrar_arriba, R.anim.entrar_de_abajo);

                    }
                    catch (Exception ignored)
                    {

                    }
                }

            }.start();



        }
       /* Log.d("results1",result);*/

    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}