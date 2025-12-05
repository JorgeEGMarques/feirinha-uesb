-- ============================================================
-- SCRIPT DE ATUALIZAÇÃO DO BANCO DE DADOS
-- ============================================================

-- 1. Criação da tabela PAGAMENTO (Sem a coluna cod_reserva)
CREATE TABLE IF NOT EXISTS pagamento (
    id_pagamento SERIAL PRIMARY KEY,
    id_venda INT NOT NULL,
    cpf_comprador CHAR(11) NOT NULL,
    cod_barraca INT NOT NULL,
    forma_pagamento CHAR(10) NOT NULL,
    data_pagamento DATE NOT NULL,
    
    CONSTRAINT fk_pagamento_venda
        FOREIGN KEY (id_venda)
        REFERENCES venda (id_venda),
        
    CONSTRAINT fk_pagamento_usuario
        FOREIGN KEY (cpf_comprador)
        REFERENCES usuario (cpf_usuario),
        
    CONSTRAINT fk_pagamento_barraca
        FOREIGN KEY (cod_barraca)
        REFERENCES barraca (cod_barraca)
);

-- 2. Sobre a relação Barraca <-> Produto
-- A relação já existe através da tabela 'estoque', que conecta 'cod_prod' e 'cod_barraca'.
-- Isso permite que uma barraca tenha vários produtos e um produto esteja em várias barracas.
-- Se você precisar listar os produtos de uma barraca, use a query:
-- SELECT p.* FROM produto p
-- JOIN estoque e ON p.cod_produto = e.cod_prod
-- WHERE e.cod_barraca = ?;
