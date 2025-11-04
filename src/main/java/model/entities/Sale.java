package model.entities;

import java.time.LocalDate;

public class Sale {
    private int id;
    private LocalDate saleDate;
    private long tentCode;
    private long userCode;

    public Sale(){}
    public Sale(int id, LocalDate saleDate, long tentCode, long userCode){
        this.id = id;
        this.saleDate = saleDate;
        this.tentCode = tentCode;
        this.userCode = userCode;
    }

    public int getId(){
        return id;
    }

    public LocalDate getSaleDate(){
        return saleDate;
    }

    public long getTentCode(){
        return tentCode;
    }

    public long getUserCode(){
        return userCode;
    }
}
