package model.entities;

import java.math.BigDecimal;

/**
 * Representa um produto disponível para venda.
 */
public class Product {
    private int code;
    private String name;
    private BigDecimal price;
    private String description;
    private byte[] imagem;

    /**
     * Construtor padrão.
     */
    public Product(){}

    /**
     * Construtor sem imagem.
     * 
     * @param code Código do produto.
     * @param name Nome do produto.
     * @param price Preço do produto.
     * @param description Descrição do produto.
     */
    public Product(int code, String name, BigDecimal price, String description){
        this.code = code;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    /**
     * Construtor completo.
     * 
     * @param code Código do produto.
     * @param name Nome do produto.
     * @param price Preço do produto.
     * @param description Descrição do produto.
     * @param imagem Imagem do produto em bytes.
     */
    public Product(int code, String name, BigDecimal price, String description, byte[] imagem){
        this.code = code;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imagem = imagem;
    }

    /**
     * Obtém o código do produto.
     * @return O código do produto.
     */
    public int getCode(){
        return code;
    }

    /**
     * Obtém o nome do produto.
     * @return O nome do produto.
     */
    public String getName(){
        return name;
    }

    /**
     * Obtém o preço do produto.
     * @return O preço do produto.
     */
    public BigDecimal getPrice(){
        return price;
    }

    /**
     * Obtém a descrição do produto.
     * @return A descrição do produto.
     */
    public String getDescription(){
        return description;
    }

    /**
     * Obtém a imagem do produto.
     * @return A imagem em bytes.
     */
    public byte[] getImagem() {
        return imagem;
    }

    /**
     * Define o código do produto.
     * @param code O novo código.
     */
    public void setCode(int code){
        this.code = code;
    }

    /**
     * Define o nome do produto.
     * @param name O novo nome.
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Define o preço do produto.
     * @param price O novo preço.
     */
    public void setPrice(BigDecimal price){
        this.price = price;
    }

    /**
     * Define a descrição do produto.
     * @param description A nova descrição.
     */
    public void setDescription(String description){
        this.description = description;
    }

    /**
     * Define a imagem do produto.
     * @param imagem A nova imagem em bytes.
     */
    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
}