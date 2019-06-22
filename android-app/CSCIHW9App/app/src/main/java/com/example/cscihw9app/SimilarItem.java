package com.example.cscihw9app;

import com.google.gson.Gson;

public class SimilarItem {

    private String image;
    private String title;
    private Double shipping;
    private Integer daysLeft;
    private Double price;
    private String itemURL;

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public SimilarItem() {

    }

    public SimilarItem(String image, String title, Double shipping, Integer daysLeft, Double price, String itemURL) {
        this.image = image;
        this.title = title;
        this.shipping = shipping;
        this.daysLeft = daysLeft;
        this.price = price;
        this.itemURL = itemURL;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public Integer getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


}
