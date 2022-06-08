package ar.com.andessalud.andes.clases;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class AllDaysDisabledDecorator implements DayViewDecorator {

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true; //decorate all days in calendar
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true); //disable all days
    }
}