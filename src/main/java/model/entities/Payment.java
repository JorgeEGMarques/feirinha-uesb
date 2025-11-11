package model.entities;

import java.time.LocalDate;

public class Payment {
    private int id;
    private int saleId;
    private long reservationCode;
    private String buyerCpf;
    private int tentCode;
    private String paymentForm;
    private LocalDate paymentDate;

    public Payment(){}
    public Payment(int id, int saleId, long reservationCode, String buyerCpf, int tentCode, String paymentForm, LocalDate paymentDate){
        this.id = id;
        this.saleId = saleId;
        this.reservationCode = reservationCode;
        this.buyerCpf = buyerCpf;
        this.tentCode = tentCode;
        this.paymentForm = paymentForm;
        this.paymentDate = paymentDate;
    }

    public int getId(){
        return id;
    }

    public int getSaleId(){
        return saleId;
    }

    public long getReservationCode(){
        return reservationCode;
    }

    public String getBuyerCpf(){
        return buyerCpf;
    }

    public int getTentCode(){
        return tentCode;
    }

    public String getPaymentForm(){
        return paymentForm;
    }

    public LocalDate getPaymentDate(){
        return paymentDate;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }
    public void setReservationCode(long reservationCode) {
        this.reservationCode = reservationCode;
    }
    public void setBuyerCpf(String buyerCpf) {
        this.buyerCpf = buyerCpf;
    }
    public void setTentCode(int tentCode) {
        this.tentCode = tentCode;
    }
    public void setPaymentForm(String paymentForm) {
        this.paymentForm = paymentForm;
    }
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    
}
