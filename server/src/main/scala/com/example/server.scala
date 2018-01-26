package com.example

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.{Service, Thrift}
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  System.getenv().forEach((k,v) => println(s"$k -> $v"))

  val clientAddr = s"${System.getenv("SERVICE3_SERVICE_HOST")}:${System.getenv("SERVICE3_SERVICE_PORT")}"
//  val clientAddr = "ser:8000"
//  val clientAddr = "127.0.0.1:1236"
  println(s"ClientAddr: $clientAddr")

  println(System.getenv())

  val tracer = ZipkinTracer.mk(host = "zipkin-service",
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)


  class ServerImpl extends BinaryService.MethodPerEndpoint {
    val methodPerEndpoint: BinaryService.MethodPerEndpoint =
      Thrift.client
        .withTracer(tracer)
        .withLabel("client1")
        .build[BinaryService.MethodPerEndpoint](clientAddr)

    def fetchBlob(id: Long): Future[String] = {
      println("fetching")
      methodPerEndpoint.fetchBlob(1234L)
    }
  }

  val server = Thrift.server
    .withTracer(tracer)
    .withLabel("server")
    .serveIface(":8000", new ServerImpl)

  val methodPerEndpoint: BinaryService.MethodPerEndpoint =
    Thrift.client
      .withTracer(tracer)
      .withLabel("client0")
      .build[BinaryService.MethodPerEndpoint]("localhost:8000")


  while (true) {
    val result: Future[String] = methodPerEndpoint.fetchBlob(1234L)


    Await.result(result.map(println))
    Thread.sleep(500)
  }


}