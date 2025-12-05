package model.entities;

/**
 * Representa o estoque de um produto em uma barraca.
 */
public class Stock {
    private int productCode;
    private int tentCode;
    private short stockQuantity;
    private Product product; 

    /**
     * Construtor padrão.
     */
    public Stock(){}

    /**
     * Construtor completo.
     * 
     * @param productCode Código do produto.
     * @param tentCode Código da barraca.
     * @param stockQuantity Quantidade em estoque.
     */
    public Stock(int productCode, int tentCode, short stockQuantity){
        this.productCode = productCode;
        this.tentCode = tentCode;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Obtém o código do produto.
     * @return O código do produto.
     */
    public int getProductCode(){
        return productCode;
    }

    /**
     * Obtém o código da barraca.
     * @return O código da barraca.
     */
    public int getTentCode(){
        return tentCode;
    }

    /**
     * Obtém a quantidade em estoque.
     * @return A quantidade em estoque.
     */
    public short getStockQuantity(){
        return stockQuantity;
    }
    
    /**
     * Obtém os detalhes do produto.
     * @return O objeto Product associado.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Define o código do produto.
     * @param productCode O novo código do produto.
     */
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    /**
     * Define o código da barraca.
     * @param tentCode O novo código da barraca.
     */
    public void setTentCode(int tentCode) {
        this.tentCode = tentCode;
    }

    /**
     * Define a quantidade em estoque.
     * @param stockQuantity A nova quantidade em estoque.
     */
    public void setStockQuantity(short stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    /**
     * Define os detalhes do produto.
     * @param product O objeto Product.
     */
    public void setProduct(Product product) {
        this.product = product;
    }
}

