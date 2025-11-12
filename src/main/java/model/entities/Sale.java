package model.entities;

import java.time.LocalDate;
import java.util.List;

public class Sale {
    private int id;
    private LocalDate saleDate;
    private int tentCode;
    private String userCode;

    private List<SaleItem> items;

    public Sale(){}
    public Sale(int id, LocalDate saleDate, int tentCode, String userCode){
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

    public int getTentCode(){
        return tentCode;
    }

    public String getUserCode(){
        return userCode;
    }

    public List<SaleItem> getItems(){
        return items;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
    public void setTentCode(int tentCode) {
        this.tentCode = tentCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
}
