package model.entities;

import java.time.LocalDate;

public class Reservation {
    private long code;
    private String holderCpf;
    private LocalDate reservationDate;

    public Reservation(){}
    public Reservation(long code, String holderCpf, LocalDate reservationDate){
        this.code = code;
        this.holderCpf = holderCpf;
        this.reservationDate = reservationDate;
    }

    public long getCode(){
        return this.code;
    }

    public String getHolderCpf(){
        return holderCpf;
    }

    public LocalDate getReservationDate(){
        return reservationDate;
    }
}