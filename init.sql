-- ==================================================
-- 1. LIMPEZA (Segurança para recriação)
-- ==================================================
DROP TABLE IF EXISTS comentario CASCADE;
DROP TABLE IF EXISTS pagamento CASCADE;
DROP TABLE IF EXISTS item_venda CASCADE;
DROP TABLE IF EXISTS venda CASCADE;
DROP TABLE IF EXISTS estoque CASCADE;
DROP TABLE IF EXISTS item_reserva CASCADE;
DROP TABLE IF EXISTS reserva CASCADE;
DROP TABLE IF EXISTS barraca CASCADE;
DROP TABLE IF EXISTS produto CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- ==================================================
-- 2. ESTRUTURA (CRIAÇÃO DAS TABELAS)
-- ==================================================

CREATE TABLE IF NOT EXISTS usuario (
    cpf_usuario CHAR(11) PRIMARY KEY,
    nome_usuario VARCHAR(50) NOT NULL,
    tel_usuario VARCHAR(15) NOT NULL,
    email VARCHAR(100), -- Obrigatório para o Login do Java
    senha VARCHAR(50),  -- Obrigatório para o Login do Java
    foto_perfil BYTEA
);

CREATE TABLE IF NOT EXISTS produto (
    cod_produto SERIAL PRIMARY KEY,
    nome_produto VARCHAR(25) NOT NULL,
    preco_produto NUMERIC(10, 2) NOT NULL,
    descricao_produto TEXT NULL,
    imagem_produto BYTEA,
    CONSTRAINT chk_produto_preco_positivo CHECK (preco_produto > 0)
);

CREATE TABLE IF NOT EXISTS barraca (
    cod_barraca INT PRIMARY KEY,
    cpf_dono CHAR(11) NOT NULL,
    nome_barraca VARCHAR(50) NOT NULL,
    licensa_usuario BYTEA NOT NULL,
    CONSTRAINT fk_barraca_usuario FOREIGN KEY (cpf_dono) REFERENCES usuario (cpf_usuario)
);

CREATE TABLE IF NOT EXISTS estoque (
    cod_prod INT NOT NULL,
    cod_barraca INT NOT NULL,
    qntd_estoque SMALLINT DEFAULT 0,
    CONSTRAINT pk_estoque PRIMARY KEY (cod_prod, cod_barraca),
    CONSTRAINT fk_estoque_produto FOREIGN KEY (cod_prod) REFERENCES produto (cod_produto),
    CONSTRAINT fk_estoque_barraca FOREIGN KEY (cod_barraca) REFERENCES barraca (cod_barraca),
    CONSTRAINT chk_estoque_qntd_positivo CHECK (qntd_estoque > 0)
);

CREATE TABLE IF NOT EXISTS venda (
    id_venda SERIAL PRIMARY KEY,
    data_venda DATE NOT NULL,
    cod_barraca INT NOT NULL,
    cod_usuario CHAR(11) NOT NULL,
    CONSTRAINT fk_venda_barraca FOREIGN KEY (cod_barraca) REFERENCES barraca (cod_barraca),
    CONSTRAINT fk_venda_usuario FOREIGN KEY (cod_usuario) REFERENCES usuario (cpf_usuario)
);

CREATE TABLE IF NOT EXISTS item_venda (
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

CREATE TABLE IF NOT EXISTS reserva (
    cod_reserva SERIAL PRIMARY KEY,
    cpf_titular CHAR(11) NOT NULL,
    data_reserva DATE NOT NULL,
    status_reserva TEXT NOT NULL,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (cpf_titular) REFERENCES usuario (cpf_usuario)
);

CREATE TABLE IF NOT EXISTS item_reserva (
    cod_res INT NOT NULL,
    cod_prod INT NOT NULL,
    qntd_item_reserva SMALLINT NOT NULL,
    preco_reserva NUMERIC(10, 2) NOT NULL,
    CONSTRAINT pk_item_reserva PRIMARY KEY (cod_res, cod_prod),
    CONSTRAINT fk_item_reserva_reserva FOREIGN KEY (cod_res) REFERENCES reserva (cod_reserva),
    CONSTRAINT fk_item_reserva_produto FOREIGN KEY (cod_prod) REFERENCES produto (cod_produto),
    CONSTRAINT chk_item_reserva_qntd_positivo CHECK (qntd_item_reserva > 0),
    CONSTRAINT chk_item_reserva_preco_positivo CHECK (preco_reserva > 0)
);

CREATE TABLE IF NOT EXISTS pagamento (
    id_pagamento SERIAL PRIMARY KEY,
    id_venda INT NULL,
    cod_reserva INT NULL,
    cpf_comprador CHAR(11) NOT NULL,
    cod_barraca INT NOT NULL,
    forma_pagamento CHAR(10) NOT NULL,
    data_pagamento DATE NOT NULL,
    CONSTRAINT fk_pagamento_venda FOREIGN KEY (id_venda) REFERENCES venda (id_venda),
    CONSTRAINT fk_pagamento_reserva FOREIGN KEY (cod_reserva) REFERENCES reserva (cod_reserva),
    CONSTRAINT fk_pagamento_usuario FOREIGN KEY (cpf_comprador) REFERENCES usuario (cpf_usuario),
    CONSTRAINT fk_pagamento_barraca FOREIGN KEY (cod_barraca) REFERENCES barraca (cod_barraca)
);

CREATE TABLE IF NOT EXISTS comentario (
    id_comentario SERIAL PRIMARY KEY,
    texto_comentario TEXT,
    cod_produto INT REFERENCES produto(cod_produto),
    cpf_usuario CHAR(11) REFERENCES usuario(cpf_usuario)
);

-- ==================================================
-- 3. DADOS INICIAIS (INSERTS)
-- ==================================================

INSERT INTO usuario (cpf_usuario, nome_usuario, tel_usuario, email, senha, foto_perfil) VALUES
('12345678901', 'Maria Silva', '71999990001', 'maria@example.com', 'senha123', NULL),
('23456789012', 'João Souza', '71999990002', 'joao@example.com', 'senha123', NULL);

INSERT INTO produto (nome_produto, preco_produto, descricao_produto, imagem_produto) VALUES
('Coxinha', 4.50, 'Coxinha de frango 70g', NULL),
('Suco Natural', 6.00, 'Suco de laranja 300ml', NULL),
('Pão de Queijo', 3.00, 'Pão de queijo quentinho', NULL);

INSERT INTO barraca (cod_barraca, cpf_dono, nome_barraca, licensa_usuario) VALUES
(1, '12345678901', 'Barraca da Maria', ''::bytea),
(2, '23456789012', 'Quitanda do João', ''::bytea);

INSERT INTO estoque (cod_prod, cod_barraca, qntd_estoque) VALUES
(1, 1, 50),
(2, 1, 30),
(3, 2, 100);

INSERT INTO venda (data_venda, cod_barraca, cod_usuario) VALUES
(CURRENT_DATE, 1, '23456789012');

INSERT INTO item_venda (cod_prod, id_venda, qntd_venda, preco_venda) VALUES
(1, 1, 2, 4.50);

INSERT INTO comentario (texto_comentario, cod_produto, cpf_usuario) VALUES
('Muito boa, recomendo!', 1, '23456789012');