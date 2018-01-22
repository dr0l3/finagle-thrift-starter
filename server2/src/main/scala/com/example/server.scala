package com.example2

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.{Http, Thrift}
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  val clientAddr = s"${System.getenv("SERVER3_SERVICE_HOST")}:${System.getenv("SERVER3_SERVICE_PORT")}"
  println(s"ClientAddr: $clientAddr")

  val tracer = ZipkinTracer.mk(host = "localhost",
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)

  println(System.getenv())
  class ServerImpl extends BinaryService.MethodPerEndpoint {
    val methodPerEndpoint: BinaryService.MethodPerEndpoint =
      Thrift.client
        .withTracer(tracer)
        .withLabel("client2")
        .build[BinaryService.MethodPerEndpoint](clientAddr)

    def fetchBlob(id: Long): Future[String] = {
      methodPerEndpoint.fetchBlob(1234L)
    }
  }

  val server = Thrift.server
    .withTracer(tracer)
    .withLabel("server2")
    .serveIface("inet!localhost:1235", new ServerImpl)

  Await.ready(server)
}