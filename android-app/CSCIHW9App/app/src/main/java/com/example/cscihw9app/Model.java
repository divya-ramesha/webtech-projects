package com.example.cscihw9app;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by kukresa on 11/27/2017.
 */

public class Model {

    @SerializedName("shippingInfo")
    @Expose
    private Map<String, Object> shippingInfo;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("sellerInfo")
    @Expose
    private Map<String, Object> sellerInfo;
    @SerializedName("index")
    @Expose
    private Integer index;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("fullTitle")
    @Expose
    private String fullTitle;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("seller")
    @Expose
    private String seller;
    @SerializedName("shipping")
    @Expose
    private String shipping;
    @SerializedName("itemURL")
    @Expose
    private String itemURL;
    @SerializedName("condition")
    @Expose
    private String condition;
    @SerializedName("subTitle")
    @Expose
    private String subTitle;

    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals (Object o) {
        Model x = (Model) o;
        if (x.id.equals(this.id))
            return true;
        return false;
    }

    public Map<String, Object> getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(Map<String, Object> shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Map<String, Object> getSellerInfo() {
        return sellerInfo;
    }

    public void setSellerInfo(Map<String, Object> sellerInfo) {
        this.sellerInfo = sellerInfo;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getItemURL() {
        return itemURL;
    }

    public void setItemURL(String itemURL) {
        this.itemURL = itemURL;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}

