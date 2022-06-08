package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.twilio.twiml.voice.Dial;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.soy_prestador;
import ar.com.andessalud.andes.twilio.MySingleton;



public class fragmento_eventos extends Fragment {
    private Dialog dialogo;
    TextView eventId, eventDesc;
    private String TAG = "fragmento_eventos";
    Button registerButton;
    Boolean isRegister;
    TextView eventIdhead, eventDeschead;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragmento_eventos, container, false);
        init(v);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("idsfromnotification")) {
                getEventsFromId(bundle.getInt("idsfromnotification"));
                dialogo.dismiss();
            } else if (bundle.containsKey("event_id")) {
                getEventsFromId(Integer.parseInt(bundle.getString("event_id")));
            } else if (bundle.containsKey("ids")) {
                Log.d("click_listenerEventos",""+bundle.getInt("ids"));
                getEventsFromId(bundle.getInt("ids"));
            }


        }

        registerButton.setOnClickListener(v1 -> {
            if (bundle != null) {
                if (bundle.containsKey("idsfromnotification")) {
                    registerForEvents(bundle.getInt("idsfromnotification"));
                    dialogo.dismiss();
                } else if (bundle.containsKey("event_id")) {
                    registerForEvents(Integer.parseInt(bundle.getString("event_id")));
                } else if (bundle.containsKey("ids")) {
                    registerForEvents(bundle.getInt("ids"));
                }




        }

        });
//        dialogo.show();
        return v;
    }

    private void init(View v) {
        eventId = v.findViewById(R.id.eventId);
        eventDesc = v.findViewById(R.id.eventDesc);
        eventIdhead = v.findViewById(R.id.event_desc_head);
        eventDeschead = v.findViewById(R.id.event_date_head);

        registerButton = v.findViewById(R.id.event_register_btn);
    }

    private void getEventsFromId(int Id) {
        if (principal.access_token == null) {
            getCustomers(Id);
        } else {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            Log.d("idRegistration", idAfiliado);
            JSONObject eventJsonObject = new JSONObject();
            try {
//          eventJsonObject.put("idAfiliado",idAfiliado);
                eventJsonObject.put("event_id", Id);
                Log.d("event_reg", String.valueOf(eventJsonObject));

            } catch (JSONException e) {
                e.printStackTrace();
            }

                dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");

            String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("get-event");

//      "https://digitalech.com/staff-dashboard/api/get-event";
            Log.d("TAG", "URLS" + URLs);
            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URLs, eventJsonObject, response -> {
                Log.d(TAG, String.valueOf(response));
                try {
                    Log.d("sdadsss", "" + response);
                    Boolean status = response.getBoolean("status");
                    if (status.equals(true)) {
                        String date = response.getJSONObject("response").getJSONObject("detail").getJSONObject("event").getString("event_date");
                        isRegister = response.getJSONObject("response").getJSONObject("detail").getJSONObject("user_event").getBoolean("registers");
                        eventIdhead.setVisibility(View.VISIBLE);
                        eventDeschead.setVisibility(View.VISIBLE);
                        registerButton.setVisibility(View.VISIBLE);

                        if (isRegister.equals(true)) {
                            registerButton.setText(R.string.registered);
                        } else if (isRegister.equals(false)) {
                            registerButton.setText(R.string.unregistered);
                        }
                        date = date.substring(8, 10) + "-" + date.substring(5, 8) + "" + date.substring(0, 4);

                        eventId.setText(response.getJSONObject("response").getJSONObject("detail").getJSONObject("event").getString("text"));
                        eventDesc.setText(date);
                        dialogo.dismiss();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d(TAG, String.valueOf(error));

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    Log.d("access_token", principal.access_token);
                    headers.put("Authorization", "Bearer " + principal.access_token);
                    return headers;
                }
            };
            MySingleton.getInstance(getContext()).addToRequestQueue(jsonOblect);

        }

    }


    private void registerForEvents(int Id) {
        SQLController database = new SQLController(getContext());
        final String idAfiliado = database.obtenerIDAfiliado();
        Log.d("idRegistration", idAfiliado);
        JSONObject eventJsonObject = new JSONObject();
        try {
            eventJsonObject.put("idAfiliado", idAfiliado);
            eventJsonObject.put("event_id", Id);
            Log.d("event_reg", String.valueOf(eventJsonObject))
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
//        String URLs =
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("user-register-event");

//                "https://digitalech.com/staff-dashboard/api/user-register-event";
        Log.d("TAG", "URLS" + URLs);
        JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URLs, eventJsonObject, response -> {
            Log.d(TAG, String.valueOf(response));
            try {
                Log.d("sdad", "" + response);
                Boolean status = response.getBoolean("status");
                if (status.equals(true)) {
                    showSuccessDialog("¡Te registraste con éxito!", response.getJSONObject("response").getString("message"));
                    dialogo.dismiss();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
//                    Toast.makeText(getContext(), "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, String.valueOf(error));

//                    onBackPressed();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
//                    headers.put("Cache-Control", "application/x-www-form-urlencoded");//put your token here
                Log.d("access_tokenget", principal.access_token);
                headers.put("Authorization", "Bearer " + principal.access_token);

//            Content-Type;//put your token here

                return headers;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonOblect);

    }


    public void showSuccessDialog(String title, String body) {
        Dialog dialog = new Dialog(getContext());
//        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString, bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setText("SI");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCalender(id);
                dialog.dismiss();
                Intent inent = new Intent(getContext(), principal.class);
                startActivity(inent);


            }


        });
        noBtn.setVisibility(View.GONE);
        dialog.show();


    }

    public void getCustomers(int event_id) {
//        String URLs = "https://digitalech.com/staff-dashboard/api/get-customer";
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("get-customer");

        JSONObject deviceobjs = new JSONObject();
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            deviceobjs.put("idAfiliado", idAfiliado);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequests = new JsonObjectRequest(Request.Method.POST, URLs, deviceobjs, response -> {

            try {
                Boolean stats = response.getBoolean("status");
                if (stats.equals(true)) {

                    principal.access_token = response.getJSONObject("response").getJSONObject("detail").getJSONObject("token_detail").getString("access_token");

                    getEventsFromId(event_id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        }) {

        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequests);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        event_id = null;
        isRegister = null;
    }
}