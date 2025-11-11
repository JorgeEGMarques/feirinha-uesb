package model.entities;

import java.math.BigDecimal;

public class ReservationItem {
    private Long reservationCode;
    private Long productCode;
    private short reservationItemQuantity;
    private BigDecimal reservationPrice;
    
    public ReservationItem(){}
    public ReservationItem(Long reservationCode, Long productCode, short reservationItemQuantity, BigDecimal reservationPrice){
        this.reservationCode = reservationCode;
        this.productCode = productCode;
        this.reservationItemQuantity = reservationItemQuantity;
        this.reservationPrice = reservationPrice;
    }

    public Long getReservationCode(){
        return reservationCode;
    }

    public Long getProductCode(){
        return productCode;
    }

    public short getReservationitemQuantity(){
        return reservationItemQuantity;
    }

    public BigDecimal getReservationPrice(){
        return reservationPrice;
    }
}
