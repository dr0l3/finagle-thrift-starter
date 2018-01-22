FROM anapsix/alpine-java

COPY target/scala-2.12 /app
EXPOSE 1236
ENTRYPOINT ["java","-jar","/app/server-assembly-0.1-SNAPSHOT.jar"]