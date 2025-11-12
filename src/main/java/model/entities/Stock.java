package model.entities;

public class Stock {
    private int productCode;
    private int tentCode;
    private short stockQuantity;

    public Stock(){}
    public Stock(int productCode, int tentCode, short stockQuantity){
        this.productCode = productCode;
        this.tentCode = tentCode;
        this.stockQuantity = stockQuantity;
    }

    public int getProductCode(){
        return productCode;
    }

    public int getTentCode(){
        return tentCode;
    }

    public short getStockQuantity(){
        return stockQuantity;
    }
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }
    public void setTentCode(int tentCode) {
        this.tentCode = tentCode;
    }
    public void setStockQuantity(short stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}

