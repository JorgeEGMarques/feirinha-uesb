package model.entities;

import java.math.BigDecimal;

public class SaleItem {
    private int productCode;
    private int saleId;
    private short saleQuantity;
    private BigDecimal salePrice;

    public SaleItem(){}
    public SaleItem(int productCode, int saleId, short saleQuantity, BigDecimal salePrice){
        this.productCode = productCode;
        this.saleId = saleId;
        this.saleQuantity = saleQuantity;
        this.salePrice = salePrice;
    }

    public int getProductCode(){
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
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }
    public void setSaleQuantity(short saleQuantity) {
        this.saleQuantity = saleQuantity;
    }
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    
}
