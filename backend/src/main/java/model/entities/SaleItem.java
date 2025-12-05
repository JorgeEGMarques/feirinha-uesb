package model.entities;

import java.math.BigDecimal;

/**
 * Representa um item de uma venda.
 */
public class SaleItem {
    private int productCode;
    private int saleId;
    private short saleQuantity;
    private BigDecimal salePrice;

    /**
     * Construtor padrão.
     */
    public SaleItem(){}

    /**
     * Construtor completo.
     * 
     * @param productCode Código do produto.
     * @param saleId ID da venda.
     * @param saleQuantity Quantidade vendida.
     * @param salePrice Preço de venda unitário.
     */
    public SaleItem(int productCode, int saleId, short saleQuantity, BigDecimal salePrice){
        this.productCode = productCode;
        this.saleId = saleId;
        this.saleQuantity = saleQuantity;
        this.salePrice = salePrice;
    }

    /**
     * Obtém o código do produto.
     * @return O código do produto.
     */
    public int getProductCode(){
        return productCode;
    }

    /**
     * Obtém o ID da venda.
     * @return O ID da venda.
     */
    public int getSaleId(){
        return saleId;
    }

    /**
     * Obtém a quantidade vendida.
     * @return A quantidade vendida.
     */
    public short getSaleQuantity(){
        return saleQuantity;
    }

    /**
     * Obtém o preço de venda.
     * @return O preço de venda.
     */
    public BigDecimal getSalePrice(){
        return salePrice;
    }

    /**
     * Define o código do produto.
     * @param productCode O novo código do produto.
     */
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    /**
     * Define o ID da venda.
     * @param saleId O novo ID da venda.
     */
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    /**
     * Define a quantidade vendida.
     * @param saleQuantity A nova quantidade vendida.
     */
    public void setSaleQuantity(short saleQuantity) {
        this.saleQuantity = saleQuantity;
    }

    /**
     * Define o preço de venda.
     * @param salePrice O novo preço de venda.
     */
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
}
