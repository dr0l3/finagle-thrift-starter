package com.example

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.{Service, Thrift}
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.thrift.{RichClientParam, ThriftClientRequest}
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()


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
        .build[BinaryService.MethodPerEndpoint]("0.0.0.0:8001")

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