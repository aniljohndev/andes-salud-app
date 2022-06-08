package ar.com.andessalud.andes.models;

import androidx.annotation.DrawableRes;

import java.util.Date;

public class EventDate {
    public EventDate(String date, int drawable, int labelColor) {
        this.date = date;
        this.drawable = drawable;
        this.labelColor = labelColor;
    }

    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public int getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
    }

    @DrawableRes int drawable ;
    int labelColor;
}
