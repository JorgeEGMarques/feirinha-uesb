package model.entities;

import java.math.BigDecimal;

public class SaleItem {
    private long productCode;
    private int saleId;
    private short saleQuantity;
    private BigDecimal salePrice;

    public SaleItem(){}
    public SaleItem(long productCode, int saleId, short saleQuantity, BigDecimal salePrice){
        this.productCode = productCode;
        this.saleId = saleId;
        this.saleQuantity = saleQuantity;
        this.salePrice = salePrice;
    }

    public long getProductCode(){
        return productCode;
    }

    public int getSaleId(){
        return saleId;
    }

    public short getSaleQuantity(){
        return saleQuantity;
    }

    public BigDecimal getSalePrice(){
        return salePrice;
    }
}
