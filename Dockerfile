FROM openjdk:8

COPY gradle gradle
COPY build.gradle .
COPY gradlew .
COPY src src

RUN ./gradlew build

EXPOSE 8080

CMD ["java", "-jar", "build/libs/annoying34-backend-0.1.0.jar"]