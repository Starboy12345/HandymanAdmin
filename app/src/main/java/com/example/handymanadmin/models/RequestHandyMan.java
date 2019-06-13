package com.example.handymanadmin.models;

public class RequestHandyMan {
    private String senderUserId,handyManId;
    private String ownerName;
    private String handyManName;
    String ownerImage, handyManImage;
    private String reason;
    double latitude, longitude;
    private String occupation;
    private String response;
    private Long date;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getHandyManName() {
        return handyManName;
    }

    public void setHandyManName(String handyManName) {
        this.handyManName = handyManName;
    }



    public RequestHandyMan() {
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getHandyManId() {
        return handyManId;
    }

    public void setHandyManId(String handyManId) {
        this.handyManId = handyManId;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }

    public String getHandyManImage() {
        return handyManImage;
    }

    public void setHandyManImage(String handyManImage) {
        this.handyManImage = handyManImage;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
