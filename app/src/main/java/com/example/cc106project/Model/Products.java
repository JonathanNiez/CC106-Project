package com.example.cc106project.Model;

public class Products {
    private String itemName;
    private String itemImage;
    private String sellerID;
    private String itemCategory;
    private double itemPrice;
    private boolean isSold;
    private int productID;

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }


    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Products() {
    }

    public Products(String itemName, String itemImage, double itemPrice, String sellerID
            , int productID, String itemCategory, boolean isSold) {
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.sellerID = sellerID;
        this.productID = productID;
        this.itemCategory = itemCategory;
        this.isSold = isSold;
    }
}
