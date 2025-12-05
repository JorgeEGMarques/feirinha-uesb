package model.entities;

/**
 * Representa um usuário do sistema.
 */
public class User {
    private String cpf;
    private String nome;
    private String telefone;
    private String email;
    private String senha;
    private byte[] fotoPerfil;

    /**
     * Construtor padrão.
     */
    public User() {}

    /**
     * Construtor parcial.
     * 
     * @param cpf CPF do usuário.
     * @param nome Nome do usuário.
     * @param telefone Telefone do usuário.
     */
    public User(String cpf, String nome, String telefone) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
    }

    /**
     * Obtém o nome do usuário.
     * @return O nome do usuário.
     */
    public String getNome(){
        return nome;
    }

    /**
     * Obtém o CPF do usuário.
     * @return O CPF do usuário.
     */
    public String getCpf(){
        return cpf;
    }
    
    /**
     * Obtém o telefone do usuário.
     * @return O telefone do usuário.
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Obtém o email do usuário.
     * @return O email do usuário.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obtém a senha do usuário.
     * @return A senha do usuário.
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Obtém a foto de perfil do usuário.
     * @return A foto de perfil em bytes.
     */
    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    /**
     * Define o nome do usuário.
     * @param nome O novo nome.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Define o CPF do usuário.
     * @param cpf O novo CPF.
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    /**
     * Define o telefone do usuário.
     * @param telefone O novo telefone.
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Define o email do usuário.
     * @param email O novo email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Define a senha do usuário.
     * @param senha A nova senha.
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Define a foto de perfil do usuário.
     * @param fotoPerfil A nova foto de perfil em bytes.
     */
    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}