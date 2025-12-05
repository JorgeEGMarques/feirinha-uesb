package model.entities;

import java.math.BigDecimal;

/**
 * Representa um item dentro de uma reserva.
 */
public class ReservationItem {
    private int reservationCode;
    private int productCode;
    private short reservationItemQuantity;
    private BigDecimal reservationPrice;
    
    /**
     * Construtor padrão.
     */
    public ReservationItem(){}
    
    /**
     * Construtor completo.
     * 
     * @param reservationCode Código da reserva associada.
     * @param productCode Código do produto.
     * @param reservationItemQuantity Quantidade do item.
     * @param reservationPrice Preço unitário na reserva.
     */
    public ReservationItem(int reservationCode, int productCode, short reservationItemQuantity, BigDecimal reservationPrice){
        this.reservationCode = reservationCode;
        this.productCode = productCode;
        this.reservationItemQuantity = reservationItemQuantity;
        this.reservationPrice = reservationPrice;
    }

    /**
     * Obtém o código da reserva.
     * @return O código da reserva.
     */
    public int getReservationCode(){
        return reservationCode;
    }

    /**
     * Obtém o código do produto.
     * @return O código do produto.
     */
    public int getProductCode(){
        return productCode;
    }

    /**
     * Obtém a quantidade do item.
     * @return A quantidade.
     */
    public short getReservationitemQuantity(){
        return reservationItemQuantity;
    }

    /**
     * Obtém o preço unitário na reserva.
     * @return O preço.
     */
    public BigDecimal getReservationPrice(){
        return reservationPrice;
    }
    
    /**
     * Define o código da reserva.
     * @param reservationCode O novo código da reserva.
     */
    public void setReservationCode(int reservationCode) {
        this.reservationCode = reservationCode;
    }
    
    /**
     * Define o código do produto.
     * @param productCode O novo código do produto.
     */
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }
    
    /**
     * Define a quantidade do item.
     * @param reservationItemQuantity A nova quantidade.
     */
    public void setReservationItemQuantity(short reservationItemQuantity) {
        this.reservationItemQuantity = reservationItemQuantity;
    }
    
    /**
     * Define o preço unitário na reserva.
     * @param reservationPrice O novo preço.
     */
    public void setReservationPrice(BigDecimal reservationPrice) {
        this.reservationPrice = reservationPrice;
    }
}
