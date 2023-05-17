package com.example.cc106project.Model;

public class MyProductsModel {

    private String itemImage, itemName;
    private double itemPrice;
    private int itemStock;
    private int productID;
    private String sellerID;

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }


    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
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

    public MyProductsModel(String itemImage, double itemPrice, String itemName
            , int itemStock, int productID, String sellerID) {
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.itemName = itemName;
        this.itemStock = itemStock;
        this.productID = productID;
        this.sellerID = sellerID;
    }

}
