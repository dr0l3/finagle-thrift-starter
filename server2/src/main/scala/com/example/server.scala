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

<<<<<<< HEAD
  val clientAddr = s"${System.getenv("SERVER3_SERVICE_HOST")}:${System.getenv("SERVER3_SERVICE_PORT")}"
  println(s"ClientAddr: $clientAddr")

  val tracer = ZipkinTracer.mk(host = "localhost",
=======
  val tracer = ZipkinTracer.mk(host = "zipkin-service",
>>>>>>> 3d94249ba5974e038cb33c0561f8751bd3ea3cdb
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)

  println(System.getenv())
  class ServerImpl extends BinaryService.MethodPerEndpoint {
    val methodPerEndpoint: BinaryService.MethodPerEndpoint =
      Thrift.client
        .withTracer(tracer)
        .withLabel("client2")
<<<<<<< HEAD
        .build[BinaryService.MethodPerEndpoint](clientAddr)
=======
        .build[BinaryService.MethodPerEndpoint]("server3-service:8000")
>>>>>>> 3d94249ba5974e038cb33c0561f8751bd3ea3cdb

    def fetchBlob(id: Long): Future[String] = {
      methodPerEndpoint.fetchBlob(1234L)
    }
  }

  val server = Thrift.server
    .withTracer(tracer)
    .withLabel("server2")
<<<<<<< HEAD
    .serveIface("inet!localhost:1235", new ServerImpl)
=======
    .serveIface(":8001", new ServerImpl)
>>>>>>> 3d94249ba5974e038cb33c0561f8751bd3ea3cdb

  Await.ready(server)
}