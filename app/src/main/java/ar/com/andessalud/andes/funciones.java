package ar.com.andessalud.andes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;

public class funciones {

    static String estaRegistrado="";

    public static Document getDomElement(String xml){
        xml=xml.replace("data=","");
        xml=xml.replace("{<Resultado>","<Resultado>");
        xml=xml.replace("</Resultado>}","</Resultado>");
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    public static String obtenerIMEI(Context context)
    {
        String myAndroidDeviceId = "";
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (mTelephony.getDeviceId() != null) {
                myAndroidDeviceId = mTelephony.getDeviceId();
            } else {
                myAndroidDeviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return myAndroidDeviceId;
        }catch (SecurityException error){
            return "error";
        }
    }

    public static String decodificaTextoAPP(String cadena) {
        String cadenaFin="";
        cadenaFin=cadena.replace("0xa1","á");
        cadenaFin=cadenaFin.replace("0x81","Á");
        cadenaFin=cadenaFin.replace("0xa9","é");
        cadenaFin=cadenaFin.replace("0x89","É");
        cadenaFin=cadenaFin.replace("0xad","í");
        cadenaFin=cadenaFin.replace("0x8d","Í");
        cadenaFin=cadenaFin.replace("0xb3","ó");
        cadenaFin=cadenaFin.replace("0x93","Ó");
        cadenaFin=cadenaFin.replace("0xba","ú");
        cadenaFin=cadenaFin.replace("0x9a","Ú");
        cadenaFin=cadenaFin.replace("0xbc","ü");
        cadenaFin=cadenaFin.replace("0x9c","Ü");
        cadenaFin=cadenaFin.replace("0xb1","ñ");
        cadenaFin=cadenaFin.replace("0x91","Ñ");
        cadenaFin=cadenaFin.replace("0xbf","¿");
        cadenaFin=cadenaFin.replace("0x3f","?");
        cadenaFin=cadenaFin.replace("0xa1","¡");
        cadenaFin=cadenaFin.replace("0x21","!");
        cadenaFin=cadenaFin.replace("0x26","&amp;");
        return cadenaFin;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, int valorAgregarBottom) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+ valorAgregarBottom; //este 100 es arbitrario
        listView.setLayoutParams(params);
    }

    public static String verificaSenderID(Activity actActual, int cantSegundos){
        String registrado=funciones.registraDispositivo(actActual);
        if (registrado.equals("SD")){
            //No hay datos de ID para enviar
            return "SD";
        }else{
            int cuentaVuelta=0;
            if (registrado.equals("NO")) {
                while (registrado.equals("NO") && cuentaVuelta<=cantSegundos) {
                    SystemClock.sleep(1000);
                    registrado=funciones.registraDispositivo(actActual);
                    cuentaVuelta++;
                }
            }
        }
        return registrado;
    }

    public static String registraDispositivo(Activity _actActual){
        final Activity actActual=_actActual;
        SharedPreferences datosLocales =  actActual.getSharedPreferences("datosAplicacion", actActual.MODE_PRIVATE);
        String senderidEnviado = datosLocales.getString("senderidEnviado", "");
        String regID = datosLocales.getString("senderid", "");
        estaRegistrado="NO";
        if (regID.equals("")){
            estaRegistrado="SD";
        }else if (senderidEnviado.equals("")) {
            estaRegistrado= "NO";
        }else if (senderidEnviado.equals("0")){
            try {

                SQLController database = new SQLController(actActual);

                Map<String, String> params = new HashMap<String, String>();
                params.put("idAfiliado", database.obtenerIDAfiliado());
                params.put("IMEI", obtenerIMEI(actActual));
                params.put("regID", datosLocales.getString("senderid", ""));

                fabrica_WS.APPRegistrarRegID(actActual, params, new SuccessResponseHandler<Document>() {
                    @Override
                    public void onSuccess(Document valores) {
                        String valorDevuelto = valores.getElementsByTagName("valorDevuelto").item(0).getTextContent();
                        if(valorDevuelto.equals("00")) {
                            SharedPreferences datosLocales = actActual.getSharedPreferences("datosAplicacion", actActual.MODE_PRIVATE);
                            SharedPreferences.Editor editor = datosLocales.edit();
                            editor.putString("senderidEnviado", "1");
                            editor.commit();
                            estaRegistrado = "SI";
                        }else if (valorDevuelto.equals("03")) {
                            estaRegistrado = "SD";
                        } else {
                            estaRegistrado = "NO";
                        }
                    }
                }, new ErrorResponseHandler() {
                    @Override
                    public void onError(String msg) {
                        estaRegistrado = "NO";
                    }
                });
            }catch (Exception ex) {
                estaRegistrado="NO";
            }
        }else if (senderidEnviado.equals("1")){
            estaRegistrado="SI";
        } else{
            estaRegistrado="NO";
        }
        return estaRegistrado;
    }

    public static boolean soportaLlamados(Context context, Intent intent) {

        boolean result = true;
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() <= 0) {
            result = false;
        }
        return result;
    }

    public static Bitmap convertirImagen(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convertirImagen(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
