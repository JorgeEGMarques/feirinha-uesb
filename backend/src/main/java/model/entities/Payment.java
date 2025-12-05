package model.entities;

import java.time.LocalDate;

/**
 * Representa um pagamento realizado em uma venda.
 */
public class Payment {
    private Integer id; 
    private Integer saleId; 
    private String buyerCpf;
    private Integer tentCode; 
    private String paymentForm;
    private LocalDate paymentDate;

    /**
     * Construtor padrão.
     */
    public Payment(){}
    
    /**
     * Construtor completo.
     * 
     * @param id Identificador único do pagamento.
     * @param saleId Identificador da venda associada.
     * @param buyerCpf CPF do comprador.
     * @param tentCode Código da barraca onde ocorreu a venda.
     * @param paymentForm Forma de pagamento (ex: Pix, Dinheiro).
     * @param paymentDate Data do pagamento.
     */
    public Payment(Integer id, Integer saleId, String buyerCpf, Integer tentCode, String paymentForm, LocalDate paymentDate){
        this.id = id;
        this.saleId = saleId;
        this.buyerCpf = buyerCpf;
        this.tentCode = tentCode;
        this.paymentForm = paymentForm;
        this.paymentDate = paymentDate;
    }

    /**
     * Obtém o ID do pagamento.
     * @return O ID do pagamento.
     */
    public Integer getId(){
        return id;
    }

    /**
     * Define o ID do pagamento.
     * @param id O novo ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém o ID da venda.
     * @return O ID da venda.
     */
    public Integer getSaleId(){
        return saleId;
    }

    /**
     * Define o ID da venda.
     * @param saleId O novo ID da venda.
     */
    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    /**
     * Obtém o CPF do comprador.
     * @return O CPF do comprador.
     */
    public String getBuyerCpf(){
        return buyerCpf;
    }

    /**
     * Define o CPF do comprador.
     * @param buyerCpf O novo CPF.
     */
    public void setBuyerCpf(String buyerCpf) {
        this.buyerCpf = buyerCpf;
    }

    /**
     * Obtém o código da barraca.
     * @return O código da barraca.
     */
    public Integer getTentCode(){
        return tentCode;
    }

    /**
     * Define o código da barraca.
     * @param tentCode O novo código da barraca.
     */
    public void setTentCode(Integer tentCode) {
        this.tentCode = tentCode;
    }

    /**
     * Obtém a forma de pagamento.
     * @return A forma de pagamento.
     */
    public String getPaymentForm(){
        return paymentForm;
    }

    /**
     * Define a forma de pagamento.
     * @param paymentForm A nova forma de pagamento.
     */
    public void setPaymentForm(String paymentForm) {
        this.paymentForm = paymentForm;
    }

    /**
     * Obtém a data do pagamento.
     * @return A data do pagamento.
     */
    public LocalDate getPaymentDate(){
        return paymentDate;
    }

    /**
     * Define a data do pagamento.
     * @param paymentDate A nova data do pagamento.
     */
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
