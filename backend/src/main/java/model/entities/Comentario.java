package model.entities;

import java.time.LocalDateTime;

/**
 * Representa um comentário feito por um usuário em um produto.
 */
public class Comentario {
    private int id;
    private String texto;
    private int codProd;
    private String cpfUsuario;
    private LocalDateTime dataPostagem;

    /**
     * Construtor padrão.
     */
    public Comentario() {}

    /**
     * Construtor completo.
     * 
     * @param id Identificador único do comentário.
     * @param texto Conteúdo do comentário.
     * @param codProd Código do produto comentado.
     * @param cpfUsuario CPF do usuário que fez o comentário.
     * @param dataPostagem Data e hora da postagem.
     */
    public Comentario(int id, String texto, int codProd, String cpfUsuario, LocalDateTime dataPostagem) {
        this.id = id;
        this.texto = texto;
        this.codProd = codProd;
        this.cpfUsuario = cpfUsuario;
        this.dataPostagem = dataPostagem;
    }

    /**
     * Obtém o ID do comentário.
     * @return O ID do comentário.
     */
    public int getId() { return id; }
    
    /**
     * Define o ID do comentário.
     * @param id O novo ID.
     */
    public void setId(int id) { this.id = id; }

    /**
     * Obtém o texto do comentário.
     * @return O texto do comentário.
     */
    public String getTexto() { return texto; }
    
    /**
     * Define o texto do comentário.
     * @param texto O novo texto.
     */
    public void setTexto(String texto) { this.texto = texto; }

    /**
     * Obtém o código do produto.
     * @return O código do produto.
     */
    public int getCodProd() { return codProd; }
    
    /**
     * Define o código do produto.
     * @param codProd O novo código do produto.
     */
    public void setCodProd(int codProd) { this.codProd = codProd; }

    /**
     * Obtém o CPF do usuário.
     * @return O CPF do usuário.
     */
    public String getCpfUsuario() { return cpfUsuario; }
    
    /**
     * Define o CPF do usuário.
     * @param cpfUsuario O novo CPF.
     */
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    /**
     * Obtém a data de postagem.
     * @return A data de postagem.
     */
    public LocalDateTime getDataPostagem() { return dataPostagem; }
    
    /**
     * Define a data de postagem.
     * @param dataPostagem A nova data de postagem.
     */
    public void setDataPostagem(LocalDateTime dataPostagem) { this.dataPostagem = dataPostagem; }
}
