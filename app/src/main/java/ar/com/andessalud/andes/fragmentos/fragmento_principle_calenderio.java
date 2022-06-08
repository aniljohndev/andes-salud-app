package ar.com.andessalud.andes.fragmentos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.Interfaces.Eventos_Click_Interface;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.adaptadores.Eventos_Adapter;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.models.Eventos;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.MySingleton;

import static android.content.Context.CAMERA_SERVICE;
import static ar.com.andessalud.andes.principal.pantActual;


public class fragmento_principle_calenderio extends Fragment implements Eventos_Click_Interface {
    JSONObject jsonObjectForMonths;
    private Dialog dialog;
    Dialog displayEventDialog;
    List<EventDay> events = new ArrayList<>();
    //    List<EventDay> appointment =  new ArrayList<>();
    //    Calendar calender ;
    CalendarView calendarView;
    JsonObjectRequest jsonObjectRequest;
    Eventos_Adapter eventos_adapter;
    Calendar calendar;

    RecyclerView eventos_list_recyclerview;
    TextView titleDialog;
    ConstraintLayout constraintLayoutforevent;
    List<Eventos> eventos_list = new ArrayList<>();
    TextView NoDataTextView;
    int month=1;
    int turn = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragmento_principle_calenderio, container, false);
        jsonObjectForMonths = new JSONObject();
        calendarView = v.findViewById(R.id.calendarView1);

        calendar = Calendar.getInstance();
        /* calendar1 = Calendar.getInstance(); */
        displayEventDialog = new Dialog(getContext());
        displayEventDialog.setContentView(R.layout.eventos_list);
        titleDialog = displayEventDialog.findViewById(R.id.appointment_titles);
        displayEventDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        displayEventDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        displayEventDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        displayEventDialog.setCancelable(true);

        eventos_list_recyclerview = displayEventDialog.findViewById(R.id.event_today_list);
        constraintLayoutforevent = displayEventDialog.findViewById(R.id.eventos_main);

        NoDataTextView = displayEventDialog.findViewById(R.id.noEventText);
        eventos_list_recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        eventos_adapter = new Eventos_Adapter(getContext(), eventos_list, this);
        eventos_list_recyclerview.setAdapter(eventos_adapter);
//        init(v);
        try {
//            month = Integer.parseInt(String.valueOf(calendar.get(Calendar.MONTH))) ;
//            Log.d("checkingmonths", String.valueOf(calendar.get(Calendar.MONTH) + month));

//            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+month);
            Log.d("responseActual11", "" + calendar.get(Calendar.MONTH));
            if (calendar.get(Calendar.MONTH) == Calendar.JANUARY)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR)+1 + "-" + 1);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.FEBRUARY)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 2);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.MARCH)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 3);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.APRIL)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 4);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.MAY)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 5);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.JUNE)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 6);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.JULY)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 7);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.AUGUST)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 8);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 9);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 10);

            }

            if (calendar.get(Calendar.MONTH) == Calendar.NOVEMBER)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 11);

            }
            if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER)
            {
                jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 12);

            }



//            jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH));
//            jsonObjectForMonths.put("change_key","f");
            loadCurrentMonthData(jsonObjectForMonths);

            calendarView.setOnForwardPageChangeListener(() -> {
                try {
                    this.events.clear();
                    month++;
//                    month = Integer.parseInt(String.valueOf(calendar.get(Calendar.MONTH))) + 1;
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
                    Log.d("checkingmonths", String.valueOf(month));
                 /*   if (month == 12) {
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    } else if (month == 1) {
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                    }
*/
                    jsonObjectForMonths.remove("date");
                    if (calendar.get(Calendar.MONTH) == Calendar.JANUARY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 1);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.FEBRUARY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 2);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.MARCH)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 3);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.APRIL)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 4);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.MAY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 5);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.JUNE)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 6);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.JULY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 7);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.AUGUST)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 8);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 9);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 10);

                    }

                    if (calendar.get(Calendar.MONTH) == Calendar.NOVEMBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 11);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 12);

                    }


//                    jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH));
                    loadCurrentMonthData(jsonObjectForMonths);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

            //Setting the calender date from Previous..
            calendarView.setOnPreviousPageChangeListener(() -> {
//                monthChanger[0] = monthChanger[0] + 1;

                try {
                    this.events.clear();
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);

//                    month = Integer.parseInt(String.valueOf(calendar.get(Calendar.MONTH))) - 1;
               /*     month--;
                    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) -1);
                    Log.d("checkingmonths", String.valueOf(month));

                    Log.d("responseActualmonth", "" + month);
                    Log.d("responseActualcm", "" + month);

            *//*        if (month == 0) {
                        month = 12;
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);

                        calendar.set(Calendar.MONTH, 12);
                        Log.d("responseActualmonth", "" + month);
                        Log.d("responseActualcm", "" + month);

                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1 );
                    } else if (month == -1) {
                        month = 11;

                        calendar.set(Calendar.MONTH, 11);
                        Log.d("responseActualmonth", "" + month);
                        Log.d("responseActualcm", "" + month);


                    }
                   *//**//* else if(month == 1)
                    {
                        month = 12;
                    }*//**//*


            *//*
                    jsonObjectForMonths.remove("date");
                    jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH));
                    //                        jsonObjectForMonths.put("change_key","b");
               */

                    if (calendar.get(Calendar.MONTH) == Calendar.JANUARY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 1);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.FEBRUARY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 2);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.MARCH)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 3);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.APRIL)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 4);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.MAY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 5);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.JUNE)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 6);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.JULY)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 7);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.AUGUST)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 8);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 9);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 10);

                    }

                    if (calendar.get(Calendar.MONTH) == Calendar.NOVEMBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR) + "-" + 11);

                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER)
                    {
                        jsonObjectForMonths.put("month_from", calendar.get(Calendar.YEAR)-1 + "-" + 12);

                    }


                    loadCurrentMonthData(jsonObjectForMonths);
                    /*}
                    else{
                        jsonObjectForMonths.put("month_from",calendar.get(Calendar.YEAR) + "-"+calendar.get(Calendar.MONTH));
                        jsonObjectForMonths.put("change_key","b");
                        loadCurrentMonthData(jsonObjectForMonths,calendar);

                    }
*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private int change_Numbers(int calendertype) {
        return calendertype;
    }


  /*  public void showDialogBox(String title, String body)
    {
        Dialog dialog = new Dialog(getContext());
        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString,bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn,noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setText("OK");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCalender(id);
                dialog.dismiss();

            }


        });
        noBtn.setVisibility(View.GONE);
        dialog.show();


    }
*/


    private void loadCurrentMonthData(JSONObject params)
            throws JSONException {
        turn ++ ;
//        appointment.clear();
        dialog = fabrica_dialogos.dlgBuscando(getContext(), "Espere por favor...");
        String URLs = this.getResources().getString(R.string.srv_localdiscoveritech).concat("events");
//                "http://discoveritech.org/staff-dashboard/api/";
        Log.d("responseActual11", "" + params);

        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs, params, response -> {

            try {
                Log.d("responseActual1", "" + response);
                boolean stats = response.getBoolean("status");
                Log.d("response_Status", "" + stats);
                if (stats) {
                    if (jsonObjectForMonths.has("month_from")) {

                        int size = response.getJSONObject("response").getJSONObject("detail").getJSONObject("events_data").getJSONArray("data").length();
                        Log.d("checkEventsize", "" + size);
                        if (size>0)
                        {

                            for (int i = 0; i < size; i++) {
                                Log.d("checkEventsresponsec", "" + i);

                                String date = response.getJSONObject("response").getJSONObject("detail").getJSONObject("events_data").getJSONArray("data").getJSONObject(i).getString("event_date");
                                Log.d("checkEventsDate", "" + date);
                                Calendar calendar = this.calendar;
                               /* if(turn == 1)
                                {
                                    calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));

                                }
                                else {
                                    calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)+1);

                                }*/
                                date = date.substring(8, 10);
                                calendar.set(Calendar.DATE, Integer.parseInt(date));
                                events.add(new EventDay(calendar, R.drawable.calnder_icon, Color.parseColor("#e8b03a")));
                                Log.d("checkings", "" + events.get(i).getCalendar().get(Calendar.DATE));


//                        addDateInToCalender(events,calendarView,Integer.parseInt(date));


//                        events.add(Integer.parseInt(date),null);

                            }

                        }
//                        calendarView.setEvents(events);

                        int size1 = response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").length();
                        Log.d("checkEventsize", "" + size);
                        if (size1>0)
                        {
                            for (int i = 0; i < size1; i++) {
                                Log.d("checkEventsresponsec", "" + i);

                                String date = response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").getJSONObject(i).getString("appointment_date");
                                date = date.substring(8, 10);
                                Log.d("checkEventsDate", "" + date);
                                Calendar calendar = this.calendar;
                                calendar.set(Calendar.DATE, Integer.parseInt(date));

                                events.add(new EventDay(calendar, R.drawable.calnder_icon, Color.parseColor("#e8b03a")));
                                Log.d("checkings", "" + events.get(i).getCalendar().get(Calendar.DATE));


//                        addDateInToCalender(events,calendarView,Integer.parseInt(date));


//                        events.add(Integer.parseInt(date),null);

                            }

                        }
                        calendarView.setOnDayClickListener(eventDay -> {
                            Log.d("checkEventExistence", "" + eventDay);
                            if (events.contains(eventDay)) {
                                eventos_list.clear();
                                try {

                                    String year = String.valueOf(eventDay.getCalendar().get(Calendar.YEAR));
                                    year = "20" + year.substring(2, 4);
                                    titleDialog.setText(MessageFormat.format("\tCita / Eventos para {2}/{1}/{0}", year, eventDay.getCalendar().get(Calendar.MONTH) + 1, eventDay.getCalendar().get(Calendar.DATE)));
                                    int currentmonth = eventDay.getCalendar().get(Calendar.MONTH) + 1;
                                    jsonObjectForMonths.remove("month_from");
                                    jsonObjectForMonths.put("date", eventDay.getCalendar().get(Calendar.YEAR)
                                            + "-" + currentmonth +
                                            "-" + eventDay.getCalendar().get(Calendar.DATE));
                                    loadCurrentMonthData(jsonObjectForMonths);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            displayEventDialog.show();
                        });
                     /*   calendarView.setOnDayClickListener(eventDay -> {
                            int  currentmonth =  eventDay.getCalendar().get(Calendar.MONTH) + 1;
                            jsonObjectForMonths.remove("month_from");
//                            titleDialog.setText(MessageFormat.format("Cita / Eventos para {2}/{1}/{0}", eventDay.getCalendar().get(Calendar.YEAR), eventDay.getCalendar().get(Calendar.MONTH), eventDay.getCalendar().get(Calendar.DATE)));

                            try {
                                jsonObjectForMonths.put("date",eventDay.getCalendar().get(Calendar.YEAR)
                                        + "-" + currentmonth+
                                        "-" + eventDay.getCalendar().get(Calendar.DATE));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (events.contains(eventDay))
                            {
                                Log.d("checkEventExistence",""+eventDay.getCalendar().get(Calendar.YEAR)
                                        + "-" + eventDay.getCalendar().get(Calendar.MONTH) +
                                        "-" + eventDay.getCalendar().get(Calendar.DATE));

                                try {
                                    loadCurrentMonthData(jsonObjectForMonths,calendar);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        });
*/

                    } else if (jsonObjectForMonths.has("date")) {
                        Log.d(
                                "dateIssue",
                                jsonObjectForMonths.getString("date")
                        );


                        Log.d("response_checking", "" + response);
                        int size1 = response.getJSONObject("response").getJSONObject("detail").getJSONObject("events_data").getJSONArray("data").length();
                        Log.d("checkingsize", "" + size1);


//                        dialog.show();
                        if (size1 >0)
                        {
                            for (int i = 0; i < size1; i++) {
                                Log.d("checkEventslister", "" + i);
                                String ids = response.getJSONObject("response").getJSONObject("detail").getJSONObject("events_data").getJSONArray("data").getJSONObject(i).getString("id");
                                String date = response.getJSONObject("response").getJSONObject("detail").getJSONObject("events_data").getJSONArray("data").getJSONObject(i).getString("event_date");
                                String event_desc = response.getJSONObject("response").getJSONObject("detail").getJSONObject("events_data").getJSONArray("data").getJSONObject(i).getString("text");
                                Log.d("checkEventslister", date);
                                Log.d("checkEventslister", event_desc);

                                eventos_list.add(new Eventos(

                                        ids, event_desc, date,"event"
                                ));

                                Log.d("checkEventslisterdesc", "" + eventos_list.get(i).getDesc());

                            }

                        }

                        int size2 = response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").length();
                        Log.d("checkingsize", "" + size1);

                        if (size2>0)
                        {
                            for (int i = 0; i < size2; i++) {
                                Log.d("checkEventslister", "" + i);
                                String ids = response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").getJSONObject(i).getString("id");
                                String date = response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").getJSONObject(i).getString("appointment_date");
                                String event_desc =
                                        "Hola " +
                                                response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").getJSONObject(i).getString("apellidoYNombre") +
                                                "tienes una cita con " +
                                                response.getJSONObject("response").getJSONObject("detail").getJSONObject("appointments_data").getJSONArray("data").getJSONObject(i).getString("prestador");

                                /*            Hola Hassan tienes una cita con anil
                                 */


                                Log.d("checkEventslister", date);
                                Log.d("checkEventslister", event_desc);

                                eventos_list.add(new Eventos(

                                        ids, event_desc, date,"appointment"
                                ));

                                Log.d("checkEventslisterdesc", "" + eventos_list.get(i).getDesc());

                            }

                        }


                        Log.d("checkEventslisterdesc", "" + eventos_list.size());
                        eventos_adapter.notifyDataSetChanged();


                    }
                    calendarView.setEvents(events);
                    if (dialog.isShowing())
                    {
                        dialog.dismiss();
                    }
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
                pars.put("Authorization", "Bearer " + principal.access_token);
                pars.put("Content-Type", "application/json; charset=utf-8");

                return pars;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (jsonObjectRequest != null) {
            jsonObjectRequest.cancel();
            dialog.dismiss();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(int id,int position) {
        if (pantActual.equals("eventos_det")) {
            return;
        }
                Log.d("click_listener", "" + id);
                fragmento_eventos eventos_frgmnt = new fragmento_eventos();
                FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("ids", id);
                eventos_frgmnt.setArguments(bundle);
                transaction.replace(R.id.frg_pantallaActual, eventos_frgmnt);
                transaction.addToBackStack("calenderio");
                transaction.commit();
                pantActual = "eventos_det";
                displayEventDialog.dismiss();


    }

    @Override
    public void onItemClickUrl(String url) {

    }


}