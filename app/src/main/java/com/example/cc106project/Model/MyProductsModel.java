package com.example.cc106project.Model;

public class MyProductsModel {

    String itemImage;
    String itemPrice;
    String itemName;
    int itemStock;

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemStock() {
        return itemStock;
    }

    public void setItemStock(int itemStock) {
        this.itemStock = itemStock;
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public MyProductsModel() {

    }

    public MyProductsModel(String itemImage, String itemPrice, String itemName, int itemStock) {
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.itemName = itemName;
        this.itemStock = itemStock;
    }

}
