package ar.com.andessalud.andes.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationsModel {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("notification_id")
    @Expose
    private Integer notificationId;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("read")
    @Expose
    private Boolean read;

    public NotificationsModel(Integer id, String userId, Integer notificationId, String createdAt, String updatedAt, Boolean read, List<Notifications> notificationslist) {
        this.id = id;
        this.userId = userId;
        this.notificationId = notificationId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.read = read;
        this.notificationslist = notificationslist;
    }

    public List<Notifications> getNotificationslist() {
        return notificationslist;
    }

    public void setNotificationslist(List<Notifications> notificationslist) {
        this.notificationslist = notificationslist;
    }

    @SerializedName("notification")
//    @Expose
//    private Notification notification;

    private List<Notifications> notificationslist;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
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

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }




}
