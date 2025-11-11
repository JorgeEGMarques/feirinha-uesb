package model.entities;

public class Tent {
    private int code;
    private String cpfHolder;
    private String name;

    public Tent(){}
    public Tent(int code, String cpfHolder, String name){
        this.code = code;
        this.cpfHolder = cpfHolder;
        this.name = name;
    }

    public int getCode(){
        return code;
    }

    public String getCpfHolder(){
        return cpfHolder;
    }

    public String getName(){
        return name;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public void setCpfHolder(String cpfHolder) {
        this.cpfHolder = cpfHolder;
    }
    public void setName(String name) {
        this.name = name;
    }

}
