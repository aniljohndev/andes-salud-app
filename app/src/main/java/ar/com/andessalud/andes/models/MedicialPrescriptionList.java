package ar.com.andessalud.andes.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicialPrescriptionList
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("appointment_id")
    @Expose
    private Integer appointmentId;
    @SerializedName("medical_recipe")
    @Expose
    private String medicalRecipe;
    @SerializedName("medical_diagnosis")
    @Expose
    private String medicalDiagnosis;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("mp_link")
    @Expose
    private String mpLink;

    public MedicialPrescriptionList(Integer id, Integer appointmentId, String medicalRecipe, String medicalDiagnosis, String createdAt, String updatedAt, String mpLink) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.medicalRecipe = medicalRecipe;
        this.medicalDiagnosis = medicalDiagnosis;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.mpLink = mpLink;
    }

    public MedicialPrescriptionList(String Url)
    {
        this.mpLink = Url;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getMedicalRecipe() {
        return medicalRecipe;
    }

    public void setMedicalRecipe(String medicalRecipe) {
        this.medicalRecipe = medicalRecipe;
    }

    public String getMedicalDiagnosis() {
        return medicalDiagnosis;
    }

    public void setMedicalDiagnosis(String medicalDiagnosis) {
        this.medicalDiagnosis = medicalDiagnosis;
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

    public String getMpLink() {
        return mpLink;
    }

    public void setMpLink(String mpLink) {
        this.mpLink = mpLink;
    }

}