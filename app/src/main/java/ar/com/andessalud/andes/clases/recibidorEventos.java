package ar.com.andessalud.andes.clases;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.fragmentos.fragmento_credencial_virtual;

public class recibidorEventos extends BroadcastReceiver {
    FragmentChangeTrigger trigger;
    private Dialog dialogo;

    @Override
    public void onReceive(Context context, Intent intent) {
        //super.onReceive(context, intent);
        // do something here
        String origen="";
        Bundle extras = intent.getExtras();
        if(extras == null) {
            origen= "";
        } else {
            origen= extras.getString("origen");
        }

        if (origen.equals("credencialVirtual")) {
            dialogo = fabrica_dialogos.dlgAviso(context, "La credencial se envió correctamente");
            LinearLayout lnMsgTotal = (LinearLayout) dialogo.findViewById(R.id.lnMsgTotal);
            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogo.dismiss();
                    trigger.fireChange("credencial_credencial_provisoria_btnTermino");
                    return;
                }
            });
        }
    }
}
