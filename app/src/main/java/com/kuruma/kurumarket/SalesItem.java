package com.kuruma.kurumarket;

public class SalesItem {

    private String tv_product;
    private String tv_price;
    private String tv_date;
    private String tv_password;//게시글의 비밀번호

    public  SalesItem(){

    }

    public String getTv_product() {
        return tv_product;
    }

    public void setTv_product(String tv_product) {
        this.tv_product = tv_product;
    }

    public String getTv_price() {
        return tv_price;
    }

    public void setTv_price(String tv_price) {
        this.tv_price = tv_price;
    }

    public String getTv_date() {
        return tv_date;
    }

    public void setTv_date(String tv_date) {
        this.tv_date = tv_date;
    }

    public String getTv_password() {
        return tv_password;
    }

    public void setTv_password(String tv_password) {
        this.tv_password = tv_password;
    }
}
