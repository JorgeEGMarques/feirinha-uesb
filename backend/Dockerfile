# Etapa 1: Construção (Build) usando Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o arquivo de configuração e baixa as dependências
COPY pom.xml .
# (Dica: isso faz o cache das dependências para ser mais rápido nas próximas vezes)
RUN mvn dependency:go-offline

# Copia o código fonte e faz a compilação (gera o arquivo .war)
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Configuração do Servidor Tomcat 10 (Necessário para Jakarta EE 10)
FROM tomcat:10.1-jdk21
WORKDIR /usr/local/tomcat/webapps/

# Remove os apps padrões do Tomcat para limpar
RUN rm -rf ROOT && rm -rf examples

# Copia o arquivo .war gerado na etapa anterior e renomeia para ROOT.war
# (Isso faz com que sua API rode na raiz: localhost:8080/ em vez de localhost:8080/crud)
COPY --from=build /app/target/crud.war ./ROOT.war

# Expõe a porta padrão do Tomcat
EXPOSE 8080

# Inicia o Tomcat
CMD ["catalina.sh", "run"]