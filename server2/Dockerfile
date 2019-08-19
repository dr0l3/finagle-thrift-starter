FROM anapsix/alpine-java

COPY target/scala-2.12 /app
EXPOSE 8001
ENV JAVA_OPTS="Dcom.twitter.finagle.zipkin.initialSampleRate=1.0 -Dcom.twitter.finagle.zipkin.host=172.17.0.1:9410 -server -Xmx256M -Xms256M -XX:MaxMetaspaceSize=128M -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=70"
ENTRYPOINT ["java","-jar","/app/server2-assembly-0.1.0-SNAPSHOT.jar"]