package model.entities;

import java.math.BigDecimal;

public class Product {
    private long code;
    private String name;
    private BigDecimal price;
    private String description;

    public Product(){}
    public Product(long code, String name, BigDecimal price, String description){
        this.code = code;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public long getCode(){
        return code;
    }

    public String getName(){
        return name;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public String description(){
        return description;
    }
}