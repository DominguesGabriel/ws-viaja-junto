# Multi-stage build para otimização de segurança e tamanho de imagem
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Criar usuário sem privilégios de root para segurança
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
