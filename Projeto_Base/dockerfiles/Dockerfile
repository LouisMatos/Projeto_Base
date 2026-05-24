# ── Stage 1: Build ──────────────────────────────────────────────────────────
# Eclipse Temurin tem suporte ARM64 nativo (M1/M2), ao contrário de openjdk:8-jdk
FROM maven:3.8.8-eclipse-temurin-8 AS builder

WORKDIR /build

# Copia apenas o pom.xml primeiro para cachear as dependências numa layer separada.
# O Docker só re-executa o download quando o pom.xml mudar.
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Copia o código-fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
# Tomcat 9 com Temurin JDK 8 — imagem multi-arch (AMD64 + ARM64)
FROM tomcat:9.0-jdk8-temurin

# Remove os webapps padrão do Tomcat para manter a imagem enxuta
RUN rm -rf "$CATALINA_HOME/webapps/"*

# Implanta o WAR como ROOT para que a app fique acessível em http://localhost:8888/
COPY --from=builder /build/target/*.war "$CATALINA_HOME/webapps/ROOT.war"

# Configuração do Tomcat Manager (usuário/senha: tomcat/tomcat)
COPY dockerfiles/tomcat-users.xml "$CATALINA_HOME/conf/tomcat-users.xml"

EXPOSE 8080
