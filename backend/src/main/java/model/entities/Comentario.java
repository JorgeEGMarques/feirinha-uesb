package model.entities;

import java.time.LocalDateTime;

public class Comentario {
    private int id;
    private String texto;
    private int codProd;
    private String cpfUsuario;
    private LocalDateTime dataPostagem;

    public Comentario() {}

    public Comentario(int id, String texto, int codProd, String cpfUsuario, LocalDateTime dataPostagem) {
        this.id = id;
        this.texto = texto;
        this.codProd = codProd;
        this.cpfUsuario = cpfUsuario;
        this.dataPostagem = dataPostagem;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public int getCodProd() { return codProd; }
    public void setCodProd(int codProd) { this.codProd = codProd; }

    public String getCpfUsuario() { return cpfUsuario; }
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    public LocalDateTime getDataPostagem() { return dataPostagem; }
    public void setDataPostagem(LocalDateTime dataPostagem) { this.dataPostagem = dataPostagem; }
}
