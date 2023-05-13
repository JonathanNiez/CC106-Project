package com.example.cc106project;

public class Products {

    String itemName, itemImage, itemPrice;

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }


    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Products() {
    }

    public Products(String itemName, String itemImage, String itemPrice) {
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
    }
}
