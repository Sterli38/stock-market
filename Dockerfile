# импорт базового образа, имя образа: его версия
FROM eclipse-temurin:17-jdk-focal
COPY target/stock-market-0.0.1-SNAPSHOT.jar stock-market-0.0.1.jar
#запускаем наше приложение в контейнере
ENTRYPOINT ["java","-jar","/stock-market-0.0.1.jar"]