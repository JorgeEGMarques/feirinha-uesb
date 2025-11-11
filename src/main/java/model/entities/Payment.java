package model.entities;

import java.time.LocalDate;

public class Payment {
    private int id; // Chave primária, geralmente não é nula
    
    // --- MUDANÇAS AQUI ---
    // Colunas que podem ser NULAS (referências) devem ser Objetos
    private Integer saleId; 
    private Long reservationCode; 
    // --- FIM DAS MUDANÇAS ---

    private String buyerCpf;
    private int tentCode; // Assumindo que cod_barraca é NOT NULL no SQL
    private String paymentForm;
    private LocalDate paymentDate;

    public Payment(){}
    
    // (O construtor também muda)
    public Payment(int id, Integer saleId, Long reservationCode, String buyerCpf, int tentCode, String paymentForm, LocalDate paymentDate){
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

    // --- MUDANÇAS AQUI ---
    public Integer getSaleId(){
        return saleId;
    }
    public Long getReservationCode(){
        return reservationCode;
    }
    // --- FIM DAS MUDANÇAS ---

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

    // --- MUDANÇAS AQUI ---
    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }
    public void setReservationCode(Long reservationCode) {
        this.reservationCode = reservationCode;
    }
    // --- FIM DAS MUDANÇAS ---
    
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