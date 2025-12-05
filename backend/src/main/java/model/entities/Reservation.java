package model.entities;

import java.time.LocalDate;
// Importe a List, pois uma Reserva terá uma lista de Itens
import java.util.List; 

public class Reservation {
    
    private int code; // No seu SQL (serial), é melhor usar int ou long
    private String holderCpf;
    private LocalDate reservationDate;
    private String status; // <-- 1. CAMPO ADICIONADO (para bater com "status_reserva")
    
    // 2. CAMPO ADICIONADO (para receber a lista de itens do JSON)
    private List<ReservationItem> items; 

    public Reservation(){}
    
    // (Construtor atualizado)
    public Reservation(int code, String holderCpf, LocalDate reservationDate, String status){
        this.code = code;
        this.holderCpf = holderCpf;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    // Getters e Setters para TODOS os campos
    
    public int getCode(){
        return this.code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getHolderCpf(){
        return holderCpf;
    }
    public void setHolderCpf(String holderCpf) {
        this.holderCpf = holderCpf;
    }

    public LocalDate getReservationDate(){
        return reservationDate;
    }
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public List<ReservationItem> getItems() {
        return items;
    }
    public void setItems(List<ReservationItem> items) {
        this.items = items;
    }
}