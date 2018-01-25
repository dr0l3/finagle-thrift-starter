package com.example2

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.Thrift
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  val tracer = ZipkinTracer.mk(host = "zipkin-service",
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)

  class ServerImpl extends BinaryService.MethodPerEndpoint {
    val methodPerEndpoint: BinaryService.MethodPerEndpoint =
      Thrift.client
        .withTracer(tracer)
        .withLabel("client2")
        .build[BinaryService.MethodPerEndpoint]("server3-service:8000")

    def fetchBlob(id: Long): Future[String] = {
      methodPerEndpoint.fetchBlob(1234L)
    }
  }


  val server = Thrift.server
    .withTracer(tracer)
    .withLabel("server2")
    .serveIface(":8001", new ServerImpl)

  Await.ready(server)
}