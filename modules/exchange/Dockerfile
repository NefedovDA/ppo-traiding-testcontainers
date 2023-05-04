FROM gradle:8.0-jdk17 as build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle modules:exchange:buildFatJar --no-daemon

FROM amazoncorretto:17
EXPOSE 8080:8080
COPY --from=build /home/gradle/src/modules/exchange/build/libs/spark-trading-exchange.jar /app/app.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "app.jar"]
