## Rotas, instalação e configuração do backend

**Base**:
- Host local: `http://localhost:8080` 
- Contexto da aplicação no projeto: `/crud` 
- Prefixo da API: `/api` — então a base completa é `http://localhost:8080/crud/api`.

**CORS** (pré-requisito para chamadas do browser):
- Existe um filtro `CorsFilter` aplicado em `@WebFilter("/api/*")` que permite:
  - `Access-Control-Allow-Origin: *`
  - `Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS`
  - `Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With`

----------
**Convencionais do projeto**
- Para endpoints mapeados com `/*` o servlet usa `req.getPathInfo()`:
  - `null` ou `/` → rota de coleção (ex: listar, criar)
  - `/{id}` → rota de item (ex: buscar/atualizar/deletar por id)
- Content-Type esperado: `application/json`
- Datas: formato ISO `YYYY-MM-DD` (ex.: `"2025-11-25"`) — `ObjectMapper` tem `JavaTimeModule`.
- Valores monetários: `BigDecimal`. JSON numérico funciona (ex.: `5.50`); para maior segurança use string (ex.: `"5.50"`).
- `byte[]` (ex.: campo `userLicense` em `Tent`) → enviar base64 no JSON.
- Respostas típicas:
  - `201 Created` → criação bem-sucedida (POST)
  - `200 OK` → leitura/atualização bem-sucedida
  - `204 No Content` → exclusão bem-sucedida (DELETE)
  - `400 Bad Request` → JSON inválido ou parâmetro inválido
  - `404 Not Found` → recurso não encontrado
  - `500 Internal Server Error` → erro no DAO/SQL

----------
**Rotas e exemplos**

- Users (`UserServlet`)
  - Mapeamento: `@WebServlet(urlPatterns = {"/api/usuarios", "/api/usuarios/*"})`
  - Métodos: `POST` (criar). 
  - Endpoint exemplo: `POST /crud/api/usuarios`
  - Body (JSON):
    {
      "cpf": "11122233344",
      "nome": "João Silva",
      "telefone": "27999998888"
    }

- Products (`ProductServlet`)
  - Mapeamento: `/api/products` e `/api/products/{id}`
  - Métodos: `POST`, `GET`, `PUT`, `DELETE`
  - Exemplos:
    - `POST /crud/api/products` — criar
    - `GET /crud/api/products` — listar todos
    - `GET /crud/api/products/100` — buscar por id
    - `PUT /crud/api/products/100` — atualizar
    - `DELETE /crud/api/products/100` — deletar
  - Body Produto:
    {
      "code": 100,
      "name": "Coxinha",
      "price": 5.50,
      "description": "Coxinha de frango"
    }
  - Observação: `price` é obrigatório e deve ser > 0.

- Tents / Barracas (`TentServlet`)
  - Mapeamento: `/api/tents` e `/api/tents/{id}`
  - Métodos: `POST` (criar)
  - Body Tent:
    {
      "code": 1,
      "cpfHolder": "22233344455",
      "name": "Barraca A",
      "userLicense": "dGVzdF9saWNlbnNl"
    }

- Reservations (`ReservationServlet`)
  - Mapeamento: `/api/reservations` e `/api/reservations/{code}`
  - Métodos: `POST`, `GET`, `PUT`, `DELETE`
  - Body Reservation (exemplo com itens):
    {
      "code": 10,
      "holderCpf": "33344455566",
      "reservationDate": "2025-11-25",
      "status": "PENDING",
      "items": [ { "reservationCode": 10, "productCode": 100, "reservationItemQuantity": 2, "reservationPrice": 5.50 } ]
    }

- Sales / Vendas (`SaleServlet`)
  - Mapeamento: `/api/sales` e `/api/sales/{id}`
  - Métodos: `POST` (criar).
  - Atenção importante: na DDL atual a tabela `venda.id_venda` NÃO é auto-gerada (não é SERIAL/IDENTITY). Portanto o backend ATUAL espera que o JSON contenha `id`.
  - Body Sale (exemplo):
    {
      "id": 1,
      "saleDate": "2025-11-25",
      "tentCode": 1,
      "userCode": "11122233344",
      "items": [ { "productCode": 100, "saleId": 1, "saleQuantity": 3, "salePrice": 5.50 } ]
    }
  - Observação: `saleDate`, `cod_barraca` (tentCode) e `cod_usuario` (userCode) são NOT NULL no banco; se algum for nulo o INSERT falhará com erro de constraint.

- Payments / Pagamentos (`PaymentServlet`)
  - Mapeamento: `/api/payments` e `/api/payments/{id}`
  - Métodos: `POST`, `GET`, `PUT`, `DELETE`
  - Body Payment (exemplo):
    {
      "id": 1,
      "saleId": 1,
      "reservationCode": null,
      "buyerCpf": "33344455566",
      "tentCode": 1,
      "paymentForm": "CASH",
      "paymentDate": "2025-11-25"
    }
  - Observação: `saleId` ou `reservationCode` podem ser nulos dependendo do fluxo (venda vs reserva). `id_pagamento` na DDL também não é SERIAL.

----------
**Regras de integração e sequência (FKs)**
- A base DDL contém várias chaves estrangeiras. Ordem básica de criação para evitar violação de FK:
 1. Criar `usuario` (usuário/cliente e donos de barraca)
 2. Criar `barraca` (referencia `usuario` dono)
 3. Criar `produto` (se necessário)
 4. Criar `venda` ou `reserva` referenciando `barraca` e `usuario`
 5. Criar `item_venda` ou `item_reserva` referenciando `venda/reserva` e `produto`
 6. Criar `pagamento` referenciando `venda` ou `reserva`

Se você tentar criar uma `venda` com `tentCode` que não existe ou com `userCode` que não existe o banco vai lançar erro de FK.

----------
**Exemplos de chamadas (curl / Thunder Client)**
- Criar usuário (curl):
  curl -X POST -H "Content-Type: application/json" -d '{"cpf":"11122233344","nome":"João Silva","telefone":"27999998888"}' http://localhost:8080/crud/api/usuarios

- Criar venda (curl) — supondo que `usuario` e `barraca` já existam:
  curl -X POST -H "Content-Type: application/json" -d '{"id":1,"saleDate":"2025-11-25","tentCode":1,"userCode":"11122233344","items":[{"productCode":100,"saleId":1,"saleQuantity":3,"salePrice":5.50}]}' http://localhost:8080/crud/api/sales

No Thunder Client: criar uma coleção com os endpoints acima, setar `Content-Type: application/json` no header e colar os JSONs como body.

----------
**Instalação e configuração do backend**

Pré-requisitos (sistema de desenvolvimento):
- Java JDK 17 ou superior.
- Apache Maven 3.6+.
- PostgreSQL (versão 10+).
- Apache Tomcat 11+ para deploy.

Passos rápidos para rodar localmente:
1. Instale o Java e o Maven e verifique as versões:
   - `java -version`
   - `mvn -v`
2. Instale o PostgreSQL e crie um banco (exemplo):
   - `CREATE DATABASE feirinha_db;`
   - Crie usuário e senha conforme preferir. Garanta para esse usuário todas as permissões sobre o banco
3. Crie as tabelas usando o DDL fornecido (execute o SQL no pgAdmin):
   - Exemplo (`psql`): `psql -U postgres -d feirinha_db -f path/to/schema.sql`

Configuração da conexão JDBC
- Abra `src/main/java/dao/DatabaseConnection.java` e ajuste as constantes:
  - `JDBC_URL` → `jdbc:postgresql://<host>:<port>/<database>` (ex.: `jdbc:postgresql://localhost:5432/feirinha_db`)
  - `JDBC_USER` → usuário do Postgres
  - `JDBC_PASSWORD` → senha do usuário

Build e deploy
- Para compilar o WAR:
  ```powershell
  mvn clean package
  ```
- O WAR será gerado em `target/` (ex.: `target/crud.war` ou pasta `target/crud/`). Deploy no Tomcat:
  - Copie o WAR para `tomcat/webapps/` e reinicie o Tomcat, ou
  - Configure seu Tomcat para apontar para o `target/crud/` durante desenvolvimento.

Rodando em desenvolvimento
- Se preferir testar rapidamente sem deploy manual, rode o Tomcat localmente e faça deploy do WAR gerado pelo Maven.

Verificação e troubleshooting
- Logs do Tomcat: verifique `logs/catalina.out` ou `logs/localhost.<date>.log` para stacktraces de inicialização e erros ao executar SQL.
- Erros de driver JDBC: assegure que a dependência do PostgreSQL esteja presente no `pom.xml`. Se o erro for `Driver JDBC do PostgreSQL não encontrado.`, verifique o `pom.xml` e rode `mvn dependency:tree`.
- Erros de FK/NOT NULL: crie os registros dependentes (usuarios, barracas, produtos) antes de criar vendas/itens/pagamentos.