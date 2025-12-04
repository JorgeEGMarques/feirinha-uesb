-- Script de inserção de dados de exemplo para o novo esquema
-- Execute com psql: psql -h HOST -p PORT -U USER -d DB -f init_data.sql

BEGIN;

-- Usuários
INSERT INTO public.usuario (cpf_usuario, nome_usuario, tel_usuario, email, senha, foto_perfil)
VALUES
('12345678901', 'Maria Silva', '71999990001', 'maria@example.com', 'senha123', NULL),
('23456789012', 'João Souza',  '71999990002', 'joao@example.com',  'senha123', NULL);

-- Produtos (imagem_produto = NULL por enquanto)
INSERT INTO public.produto (nome_produto, preco_produto, descricao_produto, imagem_produto)
VALUES
('Coxinha', 4.50, 'Coxinha de frango 70g', NULL),
('Suco Natural', 6.00, 'Suco de laranja 300ml', NULL),
('Pão de Queijo', 3.00, 'Pão de queijo quentinho', NULL);

-- Barracas
INSERT INTO public.barraca (cod_barraca, cpf_dono, nome_barraca, licensa_usuario)
VALUES
(1, '12345678901', 'Barraca da Maria', ''::bytea),
(2, '23456789012', 'Quitanda do João', ''::bytea);

-- Estoque (usar cod_prod como os cod_produto gerados - assumimos 1,2,3)
INSERT INTO public.estoque (cod_prod, cod_barraca, qntd_estoque)
VALUES
( (SELECT cod_produto FROM public.produto WHERE nome_produto = 'Coxinha' LIMIT 1), 1, 50),
( (SELECT cod_produto FROM public.produto WHERE nome_produto = 'Suco Natural' LIMIT 1), 1, 30),
( (SELECT cod_produto FROM public.produto WHERE nome_produto = 'Pão de Queijo' LIMIT 1), 2, 100);

-- Vendas
INSERT INTO public.venda (data_venda, cod_barraca, cod_usuario)
VALUES
(CURRENT_DATE, 1, '23456789012');

-- Item de venda (assumindo id_venda = currval)
-- Nota: se a serial não for conhecida, use subselect para id da última venda
INSERT INTO public.item_venda (cod_prod, id_venda, qntd_venda, preco_venda)
VALUES
( (SELECT cod_produto FROM public.produto WHERE nome_produto = 'Coxinha' LIMIT 1), (SELECT id_venda FROM public.venda ORDER BY id_venda DESC LIMIT 1), 2, 4.50);

-- Comentários (usa cod_produto e cpf_usuario existentes)
INSERT INTO public.comentario (texto_comentario, cod_produto, cpf_usuario)
VALUES
('Muito boa, recomendo!', (SELECT cod_produto FROM public.produto WHERE nome_produto = 'Coxinha' LIMIT 1), '23456789012'),
('Ótima qualidade, vou voltar.', (SELECT cod_produto FROM public.produto WHERE nome_produto = 'Suco Natural' LIMIT 1), '12345678901');

COMMIT;

-- Observação:
-- Se sua sequência SERIAL (cod_produto, id_venda) já tem valores usados, ajuste os cod_prod e id_venda
-- ou consulte o valor com `SELECT currval('produto_cod_produto_seq')` / `nextval()` antes de inserir.
