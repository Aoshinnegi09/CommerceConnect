FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/commerce-connect-0.0.1-SNAPSHOT.jar"]
