FROM anapsix/alpine-java

COPY target/scala-2.11 /app
ENV JAVA_OPTS="-Dcom.twitter.finagle.zipkin.host=127.0.0.1:9410 -Dcom.twitter.finagle.zipkin.initialSampleRate=1.0"

ENTRYPOINT ["java","-jar","/app/server-assembly-0.1-SNAPSHOT.jar"]