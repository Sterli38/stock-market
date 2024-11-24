# импорт базового образа, имя образа: его версия
FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine
COPY stock-market-0.0.1-SNAPSHOT.jar stock-market-0.0.1.jar
#запускаем наше приложение в контейнере
ENTRYPOINT ["java","-jar","/stock-market-0.0.1.jar"]