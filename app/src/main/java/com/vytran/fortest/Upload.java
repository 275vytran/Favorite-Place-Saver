package com.vytran.fortest;

public class Upload {

    private String trackId;
    private String userEmail;
    private String locationName;
    private String locationType;
    private String locationAddress;
    private String userComment;
    private String downloadUrl;
    private String userLatitude;
    private String userLongitude;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String trackId, String userEmail, String locationName, String locationType, String locationAddress, String userComment, String downloadUrl, String userLatitude, String userLongitude) {
        //If no email is passed
        if (userEmail.trim().equals(""))
            userEmail = "Unknown";

        if (locationName.trim().equals(""))
            locationName = "Unknown";


        if (userComment.trim().equals(""))
            userComment = "No comment";

        if (locationType.trim().equals(""))
            locationType = "Unknown";

        if (locationAddress.trim().equals(""))
            locationAddress = "Unknown";


        this.trackId = trackId;
        this.userEmail = userEmail;
        this.locationName = locationName;
        this.locationType = locationType;
        this.locationAddress = locationAddress;
        this.userComment = userComment;
        this.downloadUrl = downloadUrl;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
}
