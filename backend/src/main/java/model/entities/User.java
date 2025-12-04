package model.entities;

public class User {
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private String senha;
    private byte[] fotoPerfil;

    public User() {}
    public User(String cpf, String nome, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
    }

    public String getNome(){
        return nome;
    }

    public String getCpf(){
        return cpf;
    }
    
    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}