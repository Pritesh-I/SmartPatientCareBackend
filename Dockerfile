FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/smart-care-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

CMD ["sh", "-c", "java -Dserver.port=${PORT:-10000} -Dserver.address=0.0.0.0 -jar app.jar"]
