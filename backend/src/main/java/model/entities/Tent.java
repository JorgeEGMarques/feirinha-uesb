package model.entities;

public class Tent {
    private long code;
    private String cpfHolder;
    private String name;

    public Tent(){}
    public Tent(long code, String cpfHolder, String name){
        this.code = code;
        this.cpfHolder = cpfHolder;
        this.name = name;
    }

    public long getCode(){
        return code;
    }

    public String getCpfHolder(){
        return cpfHolder;
    }

    public String getName(){
        return name;
    }
}
