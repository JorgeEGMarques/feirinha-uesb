package model.entities;

public class Stock {
    private long productCode;
    private long tentCode;
    private short stockQuantity;

    private Stock(){}
    private Stock(long productCode, long tentCode, short stockQuantity){
        this.productCode = productCode;
        this.tentCode = tentCode;
        this.stockQuantity = stockQuantity;
    }

    public long getProductCode(){
        return productCode;
    }

    public long getTentCode(){
        return tentCode;
    }

    public short getStockQuantity(){
        return stockQuantity;
    }
}

