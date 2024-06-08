FROM gradle:8.8.0-jdk17 as builder
COPY . /home/gradle/eureka
WORKDIR /home/gradle/eureka
RUN ./gradlew build


FROM openjdk:17
EXPOSE 8761
WORKDIR /
COPY --from=builder /home/gradle/eureka/build/libs/eureka.jar .
ENTRYPOINT ["java", "-jar", "/eureka.jar"]
