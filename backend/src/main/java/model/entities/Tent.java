package model.entities;

/**
 * Representa uma barraca na feira.
 */
public class Tent {
    private int code;
    private String cpfHolder;
    private String name;
    private String userLicense;
    private java.util.List<Stock> items; 

    /**
     * Construtor padrão.
     */
    public Tent(){}

    /**
     * Construtor completo.
     * 
     * @param code Código da barraca.
     * @param cpfHolder CPF do proprietário.
     * @param name Nome da barraca.
     * @param userLicense Alvará de funcionamento (imagem/bytes).
     */
    public Tent(int code, String cpfHolder, String name, String userLicense){
        this.code = code;
        this.cpfHolder = cpfHolder;
        this.name = name;
        this.userLicense = userLicense;
    }

    /**
     * Obtém o código da barraca.
     * @return O código da barraca.
     */
    public int getCode(){
        return code;
    }

    /**
     * Obtém o CPF do proprietário.
     * @return O CPF do proprietário.
     */
    public String getCpfHolder(){
        return cpfHolder;
    }

    /**
     * Obtém o nome da barraca.
     * @return O nome da barraca.
     */
    public String getName(){
        return name;
    }

    /**
     * Obtém o alvará de funcionamento.
     * @return O alvará em bytes.
     */
    public String getUserLicense(){
        return userLicense;
    }
    
    /**
     * Obtém a lista de itens (estoque) da barraca.
     * @return A lista de itens.
     */
    public java.util.List<Stock> getItems() {
        return items;
    }

    /**
     * Define o alvará de funcionamento.
     * @param userLicense O novo alvará em bytes.
     */
    public void setUserLicense(String userLicense){
        this.userLicense = userLicense;
    }

    /**
     * Define o código da barraca.
     * @param code O novo código.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Define o CPF do proprietário.
     * @param cpfHolder O novo CPF.
     */
    public void setCpfHolder(String cpfHolder) {
        this.cpfHolder = cpfHolder;
    }

    /**
     * Define o nome da barraca.
     * @param name O novo nome.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Define a lista de itens (estoque) da barraca.
     * @param items A nova lista de itens.
     */
    public void setItems(java.util.List<Stock> items) {
        this.items = items;
    }

}
