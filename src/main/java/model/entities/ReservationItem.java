package model.entities;

import java.math.BigDecimal;

public class ReservationItem {
    private int reservationCode;
    private int productCode;
    private short reservationItemQuantity;
    private BigDecimal reservationPrice;
    
    public ReservationItem(){}
    public ReservationItem(int reservationCode, int productCode, short reservationItemQuantity, BigDecimal reservationPrice){
        this.reservationCode = reservationCode;
        this.productCode = productCode;
        this.reservationItemQuantity = reservationItemQuantity;
        this.reservationPrice = reservationPrice;
    }

    public int getReservationCode(){
        return reservationCode;
    }

    public int getProductCode(){
        return productCode;
    }

    public short getReservationitemQuantity(){
        return reservationItemQuantity;
    }

    public BigDecimal getReservationPrice(){
        return reservationPrice;
    }
    public void setReservationCode(int reservationCode) {
        this.reservationCode = reservationCode;
    }
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }
    public void setReservationItemQuantity(short reservationItemQuantity) {
        this.reservationItemQuantity = reservationItemQuantity;
    }
    public void setReservationPrice(BigDecimal reservationPrice) {
        this.reservationPrice = reservationPrice;
    }

    
}
