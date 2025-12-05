package model.entities;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa uma venda realizada.
 */
public class Sale {
    private int id;
    private LocalDate saleDate;
    private int tentCode;
    private String userCode;

    private List<SaleItem> items;

    /**
     * Construtor padrão.
     */
    public Sale(){}
    
    /**
     * Construtor completo.
     * 
     * @param id Identificador único da venda.
     * @param saleDate Data da venda.
     * @param tentCode Código da barraca.
     * @param userCode Código (CPF) do usuário comprador.
     */
    public Sale(int id, LocalDate saleDate, int tentCode, String userCode){
        this.id = id;
        this.saleDate = saleDate;
        this.tentCode = tentCode;
        this.userCode = userCode;
    }

    /**
     * Obtém o ID da venda.
     * @return O ID da venda.
     */
    public int getId(){
        return id;
    }

    /**
     * Obtém a data da venda.
     * @return A data da venda.
     */
    public LocalDate getSaleDate(){
        return saleDate;
    }

    /**
     * Obtém o código da barraca.
     * @return O código da barraca.
     */
    public int getTentCode(){
        return tentCode;
    }

    /**
     * Obtém o código do usuário.
     * @return O código do usuário.
     */
    public String getUserCode(){
        return userCode;
    }

    /**
     * Obtém a lista de itens da venda.
     * @return A lista de itens.
     */
    public List<SaleItem> getItems(){
        return items;
    }
    
    /**
     * Define o ID da venda.
     * @param id O novo ID.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Define a data da venda.
     * @param saleDate A nova data.
     */
    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
    
    /**
     * Define o código da barraca.
     * @param tentCode O novo código da barraca.
     */
    public void setTentCode(int tentCode) {
        this.tentCode = tentCode;
    }
    
    /**
     * Define o código do usuário.
     * @param userCode O novo código do usuário.
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    
    /**
     * Define a lista de itens da venda.
     * @param items A nova lista de itens.
     */
    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
}
