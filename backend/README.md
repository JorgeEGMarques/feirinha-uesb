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

**Usuários (Users)**
- Mapeamento: `@WebServlet(urlPatterns = {"/api/usuarios","/api/usuarios/*"})`
- Campos (modelo): `cpf` (string 11 chars), `nome`, `telefone`

- Create (POST /api/usuarios)
  - curl:
    - `curl -X POST "$BASE/api/usuarios" -H "Content-Type: application/json" -d '{"cpf":"11122233344","nome":"João Silva","telefone":"27999998888"}'`
  - PowerShell:
    - ```powershell
      $body = @'{"cpf":"11122233344","nome":"João Silva","telefone":"27999998888"}'@
      Invoke-RestMethod -Method Post -Uri "$BASE/api/usuarios" -ContentType 'application/json' -Body $body
      ```

- Read (GET)
  - Listar todos: `GET /api/usuarios`
    - `curl "$BASE/api/usuarios"`
  - Buscar por CPF (exato, 11 chars): `GET /api/usuarios/{cpf}`
    - `curl "$BASE/api/usuarios/11122233344"`
  - Atenção: **não usa JSON no corpo do GET** — o servlet usa a URL para identificar o CPF.

- Update (PUT /api/usuarios/{cpf})
  - Envie somente `nome` e/ou `telefone` no JSON; o CPF é tomado da URL.
  - Exemplo:
    - `curl -X PUT "$BASE/api/usuarios/11122233344" -H "Content-Type: application/json" -d '{"nome":"João Silva Atualizado","telefone":"11988877766"}'`

- Delete (DELETE /api/usuarios/{cpf})
  - `curl -X DELETE "$BASE/api/usuarios/11122233344"`

**Produtos (Products)**
- Mapeamento: `@WebServlet(urlPatterns = {"/api/products","/api/products/*"})`
- Modelo: `code` (int), `name`, `price` (BigDecimal), `description`

- Create: `POST /api/products`
  - Exemplo JSON:
    - `{"name":"Maçã","price":3.50,"description":"Maçã gala"}`

- Read: `GET /api/products` (listar) e `GET /api/products/{id}` (buscar)
- Update: `PUT /api/products/{id}` → envie o objeto (nome, price, description)
- Delete: `DELETE /api/products/{id}`

**Barracas (Tents)**
- Mapeamento: `@WebServlet("/api/tents/*")`
- Modelo: `code` (int), `cpfHolder` (owner CPF), `name`, `userLicense` (byte[] como Base64 no JSON)

- Create: `POST /api/tents`
  - Exemplo:
    - `{"code":1,"cpfHolder":"11122233344","name":"Barraca A","userLicense":"dGVzdA=="}`
  - Obs: `cpfHolder` deve existir na tabela `usuario` (FK); caso contrário o INSERT falhará.

- Read/Update/Delete: `GET/PUT/DELETE /api/tents/{id}`

**Vendas (Sales)**
- Mapeamento: `@WebServlet("/api/sales/*")`
- Modelo: `id` (int), `saleDate` (date), `tentCode` (int), `userCode` (cpf string), `items` (array de SaleItem)

- Create (POST /api/sales)
  - Exemplo:
    - `{
         "id":1,
         "saleDate":"2006-03-08",
         "tentCode":1,
         "userCode":"11122233344",
         "items":[{"productCode":1,"saleQuantity":2,"salePrice":3.50}]
       }`
  - FK: `tentCode` e `userCode` devem existir antes de criar a venda (caso contrário, violação de FK `venda_cod_barraca_fkey`).

- Read: `GET /api/sales` e `GET /api/sales/{id}`
- Update: `PUT /api/sales/{id}` → implementado para atualizar a linha da venda e substituir seus itens em transação.
  - Envie novo objeto `Sale` sem o campo `id` (ou mantenha), o servlet define `id` a partir da URL.

- Delete: `DELETE /api/sales/{id}`

**Reservas (Reservations)**
- Mapeamento: `@WebServlet("/api/reservations/*")`
- Modelo: `code`, `holderCpf`, `reservationDate`, `status`, `items` (array)

- Create: `POST /api/reservations` (salva a reserva e itens em transação)
- Read: `GET /api/reservations` e `GET /api/reservations/{code}` (retorna também os itens)
- Update: `PUT /api/reservations/{code}` — Atualmente o servlet aceita atualização do campo `status` via `reservationDAO.updateStatus(code, status)`.
  - Para alterar apenas o status envie: `{"status":"CONFIRMADA"}`
- Delete: `DELETE /api/reservations/{code}` (deleta reserva e seus itens em transação)

**Pagamentos (Payments)**
- Mapeamento: `@WebServlet(urlPatterns = {"/api/payments","/api/payments/*"})`
- Modelo: `id`, `saleId` (Integer nullable), `reservationCode` (Integer nullable), `buyerCpf`, `tentCode`, `paymentForm`, `paymentDate`

- Create: `POST /api/payments` — `saleId` ou `reservationCode` podem ser `null`. `id` também deve ser fornecido pelo cliente (DDL não é SERIAL).
- Read/Update/Delete: suportados em `/api/payments` e `/api/payments/{id}`

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


**Coleção de exemplos rápida**
- Criar uma coleção chamada `feirinha-backend` com requests:
  - `POST /api/usuarios` (body JSON do usuário)
  - `GET /api/usuarios` (listar)
  - `GET /api/usuarios/{cpf}` (buscar)
  - `PUT /api/usuarios/{cpf}` (editar)
  - `DELETE /api/usuarios/{cpf}` (deletar)
  - `POST /api/products`, `GET /api/products`, `GET /api/products/{id}`, `PUT`, `DELETE`
  - `POST /api/tents`, `GET /api/tents/{id}`, `PUT`, `DELETE`
  - `POST /api/sales`, `GET /api/sales`, `GET /api/sales/{id}`, `PUT`, `DELETE`
  - `POST /api/reservations`, `GET /api/reservations`, `PUT /api/reservations/{id}` (status), `DELETE`
  - `POST /api/payments`, `GET /api/payments`, `GET /api/payments/{id}`, `PUT`, `DELETE`

----------