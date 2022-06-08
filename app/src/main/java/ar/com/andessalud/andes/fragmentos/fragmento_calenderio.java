package ar.com.andessalud.andes.fragmentos;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ar.com.andessalud.andes.R;

public class fragmento_calenderio extends Fragment {
//    List<EventDay> events = new ArrayList<>();
//    Calendar calender ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v =  inflater.inflate(R.layout.fragment_fragmento_calenderio, container, false);
//       calender = Calendar.getInstance();



//        events.add(new EventDay(calender, R.drawable.calnder_icon));
//or
//        events.add(new EventDay(calender, new Drawable() {
//            @Override
//            public void draw(@NonNull Canvas canvas) {
//
//            }
//
//            @Override
//            public void setAlpha(int alpha) {
//
//            }
//
//            @Override
//            public void setColorFilter(@Nullable ColorFilter colorFilter) {
//
//            }
//
//            @SuppressLint("WrongConstant")
//            @Override
//            public int getOpacity() {
//                return 0;
//            }
//        }));
//or if you want to specify event label color
//        events.add(new EventDay(calender, R.drawable.calnder_icon, Color.parseColor("#228B22")));

//        CalendarView calendarView = v.findViewById(R.id.calendarView);
//        calendarView.setEvents(events);
//        calendarView.setOnDayClickListener(new OnDayClickListener() {
//            @Override
//            public void onDayClick(EventDay eventDay) {
//                Toast.makeText(getContext(),"THis is " +eventDay.getCalendar().get(Calendar.DATE),Toast.LENGTH_LONG).show();
//            }
//        });
        return v;
    }
}
//
//
//    Calendar calender = Calendar.getInstance();
//            calender.set(date,i);
//                    eventDays.add(new EventDay(calender, R.drawable.calnder_icon,Color.parseColor("#e8b03a")));
//                    calendarView.setEvents(eventDays);