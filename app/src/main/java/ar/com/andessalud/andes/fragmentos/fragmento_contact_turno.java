package ar.com.andessalud.andes.fragmentos;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.funciones;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.MySingleton;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;


public class fragmento_contact_turno extends Fragment implements View.OnClickListener {
    TextView nombreValueTxt,
            whatsAppNumberTxt,
            callNumberTxt,
            websiteAddressTxt;
    ImageView whatsAppNumberImg,
            callNumberImg,
            websiteAddressImg;
    JSONObject jsonObject;
    Bundle args;
    public static int counterForAp;
    public static int idforsend;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.contact_credentials, container, false);
        nombreValueTxt = v.findViewById(R.id.nombreTxtvalue);
        whatsAppNumberTxt = v.findViewById(R.id.whatsappNumber_txt);
        callNumberTxt = v.findViewById(R.id.telfonoNumbertxt);
        websiteAddressTxt = v.findViewById(R.id.websiteAddressTxt);
        whatsAppNumberImg = v.findViewById(R.id.whatsappImagecont);
        callNumberImg = v.findViewById(R.id.nombretelephoneImg);
        jsonObject = new JSONObject();
        websiteAddressImg = v.findViewById(R.id.siteowebImg);
        whatsAppNumberTxt.setOnClickListener(this);
        callNumberTxt.setOnClickListener(this);
        websiteAddressTxt.setOnClickListener(this);
        whatsAppNumberImg.setOnClickListener(this);
        callNumberImg.setOnClickListener(this);
        websiteAddressImg.setOnClickListener(this);
        args = getArguments();
        Log.d("asdad", args.toString());
        if (args != null) {
            nombreValueTxt.setText(args.getString("nombreConvenio"));
            whatsAppNumberTxt.setText(args.getString("whatsappNumber"));
            callNumberTxt.setText(args.getString("callNumber"));
            websiteAddressTxt.setText(args.getString("website"));
            ;



        }
        return v;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.whatsappNumber_txt:
                try {
                    sendCentroToserveroCount("whatsappNumber");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              /*  String url = "https://api.whatsapp.com/send?phone=(+"+whatsAppNumberTxt.getText().toString();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.putExtra(Intent.EXTRA_TEXT,"Hello");
                i.setType("text/plain");
                i.setPackage("com.whatsapp");
*/


                openWhatsApp(whatsAppNumberTxt.getText().toString(),
                        "=¡Hola! Mi nombre es " +
                                args.getString("apellidoYNombre") +


                                ".\nY necesito un turno en la " + args.getString("prestadorcval") + "\n OBRA SOCIAL: ANDES SALUD \n PLAN: "+ principal.arrasend[1] + "\n DNI: "+principal.arrasend[0] + "\n Espacialidad: "+args.getString("Especialidad")
                );


//                startActivity(i);
                break;
            case R.id.telfonoNumbertxt:
                try {
                    sendCentroToserveroCount("callNumber");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNumberTxt.getText().toString()));
                startActivity(intent);
                break;
            case R.id.websiteAddressTxt:
                try {
                    sendCentroToserveroCount("website");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(websiteAddressTxt.getText().toString()));
                try {
                    startActivity(webIntent);
                } catch (ActivityNotFoundException ex) {
                }
                break;
            case R.id.whatsappImagecont:


                try {
                    sendCentroToserveroCount("whatsappNumber");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                openWhatsApp(whatsAppNumberTxt.getText().toString(),
                        "=¡Hola! Mi nombre es " +
                                args.getString("apellidoYNombre") +


                                ".\nY necesito un turno en la " + args.getString("prestadorcval") + "\n OBRA SOCIAL: ANDES SALUD \n PLAN: "+ principal.arrasend[1] + "\n DNI: "+principal.arrasend[0] + "\n Espacialidad: "+args.getString("Especialidad")
                );
                break;
            case R.id.nombretelephoneImg:
                try {
                    sendCentroToserveroCount("callNumber");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intenst = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callNumberTxt.getText().toString()));
                startActivity(intenst);
                break;
            case R.id.siteowebImg:
                try {
                    sendCentroToserveroCount("website");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent webIntents = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(websiteAddressTxt.getText().toString()));
                try {
                    startActivity(webIntents);
                } catch (ActivityNotFoundException ex) {
                }
                break;

        }
    }

    private void sendCentroToserveroCount(String idPresto) throws JSONException {
        counterForAp += 1;
        if (counterForAp > 1) {
            sendCentroUpdate(idPresto, idforsend);
        } else {
            sendCentosToservero(idPresto);
        }
    }

    private void sendCentroUpdate(String idPrestedo, int id) throws JSONException {
//        Log.d("createadata", "" + idPrestedo);
        Log.d("datesvalue", "update");
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("meeting-details/" + id);
//                "https://digitalech.com/staff-dashboard/api/update-health-provider-meeting-details/"+id;
        Log.d("urldata", "" + URLs);
        jsonObject.put("OpcionSeleccionada", idPrestedo);
        jsonObject.put("idAffiliadoTitular", args.getString("idAffiliadoTitular"));
        jsonObject.put("idAffiliado", args.getString("idAffiliado"));
        jsonObject.put("apellidoYNombre", args.getString("apellidoYNombre"));
        jsonObject.put("Especialidad", args.getString("Especialidad"));
        jsonObject.put("prestador", args.getString("prestadorcval"));
        Log.d("asdasd", String.valueOf(jsonObject));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, jsonObject, response -> {

            try {
                boolean stats = response.getBoolean("status");
                if (stats) {
                    Log.d("respnsea", "" + response);


                }
            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
//            dialogo.dismiss();
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }


        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }


    void sendCentosToservero(String idPrestedo) throws JSONException {
//        Log.d("createadata", "" + idPrestedo);
        Log.d("datesvalue", "first");
        String URLs = getContext().getResources().getString(R.string.srv_localdiscoveritech).concat("store-health-provider-meeting-details");

//         "https://digitalech.com/staff-dashboard/api/store-health-provider-meeting-details";
        Log.d("urldata", "" + URLs);
        jsonObject.put("OpcionSeleccionada", idPrestedo);
        jsonObject.put("idAffiliadoTitular", args.getString("idAffiliadoTitular"));
        jsonObject.put("idAffiliado", args.getString("idAffiliado"));
        jsonObject.put("apellidoYNombre", args.getString("apellidoYNombre"));
        jsonObject.put("Especialidad", args.getString("Especialidad"));
        jsonObject.put("prestador", args.getString("prestadorcval"));
        Log.d("asdasd", String.valueOf(jsonObject));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, jsonObject, response -> {

            try {
                boolean stats = response.getBoolean("status");
                if (stats) {
                    Log.d("respnsea", String.valueOf(stats));
                    idforsend = (int) response.getJSONObject("response").getJSONObject("detail").get("id")
                    ;

                }
            } catch (JSONException e) {

                e.printStackTrace();
            }


        }, error -> {
//            dialogo.dismiss();
            Log.d("eDASDADS", "" + error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("Content-Type", "application/form-data");
                return pars;
            }


        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    public void openWhatsApp(String number, String text) {
        try {
//            String text = "This is a\n test";// Replace with your message.

            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + number + "&text" + text));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        counterForAp = 0;
        idforsend = 0;
    }
}