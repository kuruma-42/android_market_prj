package com.kuruma.kurumarket;

public class MemberInfo {

    private String name;
    private String phone;
    private String birthdate;
    private String address;
    private String photoUrl;

    public MemberInfo(String name, String phone, String birthdate, String address, String photoUrl){
        this.name = name;
        this.phone = phone;
        this.birthdate = birthdate;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public MemberInfo(String name, String phone, String birthdate, String address){
        this.name = name;
        this.phone = phone;
        this.birthdate = birthdate;
        this.address = address;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}


