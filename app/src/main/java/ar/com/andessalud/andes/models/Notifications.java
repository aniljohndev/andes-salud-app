package ar.com.andessalud.andes.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Notifications {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("appointment_id")
    @Expose
    private Integer appointmentId;
    @SerializedName("medical_prescription_id")
    @Expose
    private Integer medicalPrescriptionId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("has_event")
    @Expose
    private Boolean hasEvent;
    @SerializedName("has_appointment")
    @Expose
    private Boolean hasAppointment;
    @SerializedName("has_medical_prescription")
    @Expose
    private Boolean hasMedicalPrescription;
    @SerializedName("is_clickable")
    @Expose
    private Boolean isClickable;

    public String getMp_link() {
        return mp_link;
    }

    public void setMp_link(String mp_link) {
        this.mp_link = mp_link;
    }

    @SerializedName("mp_link")
    @Expose
    private String mp_link;



    public Notifications(Integer id, String text, Integer eventId, Integer appointmentId, Integer medicalPrescriptionId, String createdAt, String updatedAt, Boolean hasEvent, Boolean hasAppointment, Boolean hasMedicalPrescription, Boolean isClickable,String mylink, List<EventNotificationList> eventNotificationList, List<AppointmentNotificationList> appointmentNotificationList, List<MedicialPrescriptionList> medicialPrescriptionList) {
        this.id = id;
        this.text = text;
        this.eventId = eventId;
        this.appointmentId = appointmentId;
        this.medicalPrescriptionId = medicalPrescriptionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.hasEvent = hasEvent;
        this.hasAppointment = hasAppointment;
        this.hasMedicalPrescription = hasMedicalPrescription;
        this.isClickable = isClickable;
        this.mp_link= mylink;
        this.eventNotificationList = eventNotificationList;
        this.appointmentNotificationList = appointmentNotificationList;
        this.medicialPrescriptionList = medicialPrescriptionList;
    }




  /*  @SerializedName("event")
    @Expose
    private Object event;*/

    public List<EventNotificationList> getEventNotificationList() {
        return eventNotificationList;
    }
    public void setEventNotificationList(List<EventNotificationList> notificationslist) {
        this.eventNotificationList = notificationslist;
    }
    @Expose
    @SerializedName("event")
    private List<EventNotificationList> eventNotificationList;


    public List<AppointmentNotificationList> getAppointmentNotificationList() {
        return appointmentNotificationList;
    }
    public void setAppointmentNotificationList(List<AppointmentNotificationList> notificationslist) {
        this.appointmentNotificationList = notificationslist;
    }
    @Expose
    @SerializedName("appointment")
    private List<AppointmentNotificationList> appointmentNotificationList;



    public List<MedicialPrescriptionList> getMedicalPrescriptionList() {
        return medicialPrescriptionList;
    }
    public void setMedicalPrescriptionList(List<MedicialPrescriptionList> notificationslist) {
        this.medicialPrescriptionList = notificationslist;
    }
    @Expose
    @SerializedName("medical_prescription")
  private List<MedicialPrescriptionList> medicialPrescriptionList;







    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getMedicalPrescriptionId() {
        return medicalPrescriptionId;
    }

    public void setMedicalPrescriptionId(Integer medicalPrescriptionId) {
        this.medicalPrescriptionId = medicalPrescriptionId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getHasEvent() {
        return hasEvent;
    }

    public void setHasEvent(Boolean hasEvent) {
        this.hasEvent = hasEvent;
    }

    public Boolean getHasAppointment() {
        return hasAppointment;
    }

    public void setHasAppointment(Boolean hasAppointment) {
        this.hasAppointment = hasAppointment;
    }

    public Boolean getHasMedicalPrescription() {
        return hasMedicalPrescription;
    }

    public void setHasMedicalPrescription(Boolean hasMedicalPrescription) {
        this.hasMedicalPrescription = hasMedicalPrescription;
    }

    public Boolean getIsClickable() {
        return isClickable;
    }

    public void setIsClickable(Boolean isClickable) {
        this.isClickable = isClickable;
    }
}









/*
public  class Notifications {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("event_id")
    @Expose
    private Integer eventId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("has_event")
    @Expose
    private Boolean hasEvent;

    public Notifications(Integer id, String text, Integer eventId, String createdAt, String updatedAt, Boolean hasEvent) {
        this.id = id;
        this.text = text;
        this.eventId = eventId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.hasEvent = hasEvent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getHasEvent() {
        return hasEvent;
    }

    public void setHasEvent(Boolean hasEvent) {
        this.hasEvent = hasEvent;
    }


}*/
