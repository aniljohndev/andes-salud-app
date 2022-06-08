package ar.com.andessalud.andes.fabricas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;

public class fabrica_dialogos {
    public static Dialog dlgBuscando(Context contexto, String mensaje){
        Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_buscando);
        TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        textMsg.setText(mensaje);
        ImageView imageView = dialog.findViewById(R.id.imgCargando);
        /*load from raw folder*/
        Glide.with(contexto).load(R.drawable.loading).into(imageView);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }

    public static Dialog dlgAviso(Context contexto, String mensaje){
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_aviso);
        TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        textMsg.setText(mensaje);
        LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static Dialog dlgError(Context contexto, String mensaje){
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_error);
        TextView textMsgError = (TextView) dialog.findViewById(R.id.lblMsg);
        textMsgError.setText(mensaje);
        LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static Dialog dlgConfirmarDatos(Context contexto, String titulo, String mensaje, int icono,String colorFondo){
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_confirmar_datos);
        LinearLayout  lnMsgTotal=(LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
        if (colorFondo.equals("amarillo")){
            lnMsgTotal.setBackground(ContextCompat.getDrawable(contexto,R.drawable.round_yellow_dialog_bg_yellow));
        }else {
            lnMsgTotal.setBackground(ContextCompat.getDrawable(contexto,R.drawable.round_yellow_dialog_bg_brown));
        }
        TextView lblTitulo = (TextView) dialog.findViewById(R.id.lblTitulo);
        lblTitulo.setText(titulo);
        TextView lblMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        lblMsg.setText(mensaje);
        ImageView imgIcono=(ImageView) dialog.findViewById(R.id.imgIcono);
        imgIcono.setImageResource(icono);
        ImageView btnSi=(ImageView)dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageView btnNo=(ImageView)dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static Dialog dlgConfirmarDatosOcultaBotonNO(Context contexto, String titulo, String mensaje, int icono,String colorFondo){
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_confirmar_datos);
        LinearLayout  lnMsgTotal=(LinearLayout) dialog.findViewById(R.id.lnMsgTotal);
        if (colorFondo.equals("amarillo")){
            lnMsgTotal.setBackground(ContextCompat.getDrawable(contexto,R.drawable.round_yellow_dialog_bg_yellow));
        }else {
            lnMsgTotal.setBackground(ContextCompat.getDrawable(contexto,R.drawable.round_yellow_dialog_bg_brown));
        }
        TextView lblTitulo = (TextView) dialog.findViewById(R.id.lblTitulo);
        lblTitulo.setText(titulo);
        TextView lblMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        lblMsg.setText(mensaje);
        ImageView imgIcono=(ImageView) dialog.findViewById(R.id.imgIcono);
        imgIcono.setImageResource(icono);
        ImageView btnSi=(ImageView)dialog.findViewById(R.id.btnSi);
        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ImageView btnNo=(ImageView)dialog.findViewById(R.id.btnNo);
        btnNo.setVisibility(View.GONE);
        dialog.show();
        return dialog;
    }

    public static Dialog dlgMuestraTelefonosMarcar(Context contexto, String titulo, int icono){
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_margar_telefono);
        TextView lblTitulo=(TextView) dialog.findViewById(R.id.lblTitulo);
        lblTitulo.setText(titulo);
        ImageView imgIcono=(ImageView) dialog.findViewById(R.id.imgIcono);
        imgIcono.setImageResource(icono);
        ImageView lnMsgTotal=(ImageView) dialog.findViewById(R.id.btnNo);
        lnMsgTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static Dialog dlgDescargarCompartir(Context contexto, String titulo, int icono){
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_descargar_compartir);
        TextView lblTitulo = (TextView) dialog.findViewById(R.id.lblTitulo);
        lblTitulo.setText(titulo);
        ImageView imgIcono=(ImageView) dialog.findViewById(R.id.imgIcono);
        imgIcono.setImageResource(icono);
        LinearLayout btnDownload=(LinearLayout) dialog.findViewById(R.id.btnDownload);
        LinearLayout btnShare=(LinearLayout) dialog.findViewById(R.id.btnShare);
        dialog.show();
        return dialog;
    }

    public static Dialog makeGeneralDialog(Activity activity, int layoutDlgResId) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(layoutDlgResId);
        return dialog;
    }

    public static Dialog dlgHorarioDrOnline(Fragment actActual, Context contexto, String nombreConvenio, String[] horarios) {
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_especialidad_horario);
        TextView txtPrestador = (TextView) dialog.findViewById(R.id.txtPrestador);
        txtPrestador.setText(nombreConvenio);
        LinearLayout lnHorario=(LinearLayout)dialog.findViewById(R.id.lnHorario);
        lnHorario.removeAllViews();
        for (int i = 0; i < horarios.length; i++) {
            LayoutInflater inflater = actActual.getLayoutInflater();
            View lnHorarioActual = inflater.inflate(R.layout.lista_un_renglon_horario_dr_online, null);
            TextView linea1 = (TextView) lnHorarioActual.findViewById(R.id.linea1);
            linea1.setText(horarios[i]);
            lnHorario.addView(lnHorarioActual);
        }
        dialog.show();
        return dialog;
    }
    public static Dialog dlgInfo(Context contexto, String message) {
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_especialidad_horario);
        TextView txtPrestador = (TextView) dialog.findViewById(R.id.txtPrestador);
        txtPrestador.setText(message);
        LinearLayout lnHorario=(LinearLayout)dialog.findViewById(R.id.lnHorario);
        lnHorario.removeAllViews();
        dialog.show();
        dialog.setCancelable(true);
        return dialog;
    }

    public static Dialog dlgDinoGame(Context contexto) {
        final Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_dino_game);
        WebView webView=(WebView)dialog.findViewById(R.id.wb) ;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/index.html");
        ImageView btnCerrar=(ImageView)dialog.findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static Dialog dlgBuscandowait(Context contexto, String mensaje){
        Dialog dialog = new Dialog(contexto);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.setContentView(R.layout.dlg_modal_buscando);
        TextView textMsg = (TextView) dialog.findViewById(R.id.lblMsg);
        textMsg.setText(mensaje);
        ImageView imageView = dialog.findViewById(R.id.imgCargando);
        /*load from raw folder*/
        Glide.with(contexto).load(R.drawable.loading).into(imageView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
        return dialog;
    }



}
