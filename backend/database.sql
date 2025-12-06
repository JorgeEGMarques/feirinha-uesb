
CREATE TABLE usuario (
    cpf_usuario CHAR(11) PRIMARY KEY,
    nome_usuario VARCHAR(50) NOT NULL,
    tel_usuario VARCHAR(25) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    foto_perfil BYTEA NULL
);

CREATE TABLE barraca (
    cod_barraca SERIAL PRIMARY KEY,
    cpf_dono CHAR(11) NOT NULL,
    nome_barraca VARCHAR(50) NOT NULL,
    licensa_usuario TEXT NULL,

    CONSTRAINT fk_barraca_usuario
        FOREIGN KEY (cpf_dono) REFERENCES usuario (cpf_usuario)
);

CREATE TABLE produto (
    cod_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    preco_produto NUMERIC(10, 2) NOT NULL,
    cod_barraca INT NOT NULL,
    descricao_produto TEXT NULL,
    imagem_produto BYTEA NULL,

    CONSTRAINT chk_produto_preco_positivo CHECK (preco_produto > 0),
    CONSTRAINT fk_produto_barraca FOREIGN KEY (cod_barraca) REFERENCES barraca (cod_barraca) ON DELETE RESTRICT
);

CREATE TABLE comentario (
    id_comentario SERIAL PRIMARY KEY,
    texto_comentario TEXT NOT NULL,
    cod_produto INT NOT NULL,
    cpf_usuario CHAR(11) NOT NULL,
    data_postagem TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comentario_produto FOREIGN KEY (cod_produto) REFERENCES produto (cod_produto) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (cpf_usuario) REFERENCES usuario (cpf_usuario) ON DELETE CASCADE
);

CREATE TABLE estoque (
    cod_prod INT NOT NULL,
    cod_barraca INT NOT NULL,
    qntd_estoque SMALLINT DEFAULT 0,

    CONSTRAINT pk_estoque PRIMARY KEY (cod_prod, cod_barraca),
    CONSTRAINT fk_estoque_produto FOREIGN KEY (cod_prod) REFERENCES produto (cod_produto),
    CONSTRAINT fk_estoque_barraca FOREIGN KEY (cod_barraca) REFERENCES barraca (cod_barraca),
    CONSTRAINT chk_estoque_qntd_nao_negativa CHECK (qntd_estoque >= 0)
);

CREATE TABLE venda (
    id_venda SERIAL PRIMARY KEY,
    data_venda DATE NOT NULL,
    cod_barraca INT NOT NULL,
    cod_usuario CHAR(11) NOT NULL,

    CONSTRAINT fk_venda_barraca FOREIGN KEY (cod_barraca) REFERENCES barraca (cod_barraca),
    CONSTRAINT fk_venda_usuario FOREIGN KEY (cod_usuario) REFERENCES usuario (cpf_usuario)
);

CREATE TABLE item_venda (
    cod_prod INT NOT NULL,
    id_venda INT NOT NULL,
    qntd_venda SMALLINT NOT NULL,
    preco_venda NUMERIC(10, 2) NOT NULL,

    CONSTRAINT pk_item_venda PRIMARY KEY (cod_prod, id_venda),
    CONSTRAINT fk_item_venda_produto FOREIGN KEY (cod_prod) REFERENCES produto (cod_produto),
    CONSTRAINT fk_item_venda_venda FOREIGN KEY (id_venda) REFERENCES venda (id_venda),
    CONSTRAINT chk_item_venda_qntd_positivo CHECK (qntd_venda > 0),
    CONSTRAINT chk_item_venda_preco_positivo CHECK (preco_venda > 0)
);

CREATE TABLE pagamento (
    id_pagamento SERIAL PRIMARY KEY,
    id_venda INT NOT NULL,
    cpf_usuario CHAR(11) NOT NULL,
    metodo_pagamento VARCHAR(20) NOT NULL,
    status_pagamento VARCHAR(20) DEFAULT 'Pendente',
    valor_pagamento NUMERIC(10, 2) NOT NULL,
    data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pagamento_venda FOREIGN KEY (id_venda) REFERENCES venda (id_venda) ON DELETE CASCADE,
    CONSTRAINT fk_pagamento_usuario FOREIGN KEY (cpf_usuario) REFERENCES usuario (cpf_usuario),
    CONSTRAINT chk_pagamento_valor_positivo CHECK (valor_pagamento > 0),
    CONSTRAINT chk_metodo_valido CHECK (metodo_pagamento IN ('Pix', 'Cartao_Credito', 'Cartao_Debito', 'Dinheiro')),
    CONSTRAINT chk_status_valido CHECK (status_pagamento IN ('Pendente', 'Aprovado', 'Recusado', 'Estornado'))
);