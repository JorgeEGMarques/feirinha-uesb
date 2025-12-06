package model.entities;

import java.math.BigDecimal;

public class Product {
    private int code;
    private String name;
    private BigDecimal price;
    private String description;

    public Product(){}
    public Product(int code, String name, BigDecimal price, String description){
        this.code = code;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public int getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public String getDescription(){
        return description;
    }

    public void setCode(int code){
        this.code = code;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(BigDecimal price){
        this.price = price;
    }

    public void setDescription(String description){
        this.description = description;
    }
}