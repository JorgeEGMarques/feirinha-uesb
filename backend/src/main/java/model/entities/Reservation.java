package model.entities;

import java.time.LocalDate;
import java.util.List; 

/**
 * Representa uma reserva de produtos feita por um usuário.
 */
public class Reservation {
    
    private int code; 
    private String holderCpf;
    private LocalDate reservationDate;
    private String status; 

    private List<ReservationItem> items; 

    /**
     * Construtor padrão.
     */
    public Reservation(){}

    /**
     * Construtor completo.
     * 
     * @param code Código da reserva.
     * @param holderCpf CPF do titular da reserva.
     * @param reservationDate Data da reserva.
     * @param status Status da reserva (ex: Pendente, Confirmada).
     */
    public Reservation(int code, String holderCpf, LocalDate reservationDate, String status){
        this.code = code;
        this.holderCpf = holderCpf;
        this.reservationDate = reservationDate;
        this.status = status;
    }
    
    /**
     * Obtém o código da reserva.
     * @return O código da reserva.
     */
    public int getCode(){
        return this.code;
    }
    
    /**
     * Define o código da reserva.
     * @param code O novo código.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Obtém o CPF do titular.
     * @return O CPF do titular.
     */
    public String getHolderCpf(){
        return holderCpf;
    }
    
    /**
     * Define o CPF do titular.
     * @param holderCpf O novo CPF.
     */
    public void setHolderCpf(String holderCpf) {
        this.holderCpf = holderCpf;
    }

    /**
     * Obtém a data da reserva.
     * @return A data da reserva.
     */
    public LocalDate getReservationDate(){
        return reservationDate;
    }
    
    /**
     * Define a data da reserva.
     * @param reservationDate A nova data.
     */
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    /**
     * Obtém o status da reserva.
     * @return O status da reserva.
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Define o status da reserva.
     * @param status O novo status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Obtém a lista de itens da reserva.
     * @return A lista de itens.
     */
    public List<ReservationItem> getItems() {
        return items;
    }
    
    /**
     * Define a lista de itens da reserva.
     * @param items A nova lista de itens.
     */
    public void setItems(List<ReservationItem> items) {
        this.items = items;
    }
}