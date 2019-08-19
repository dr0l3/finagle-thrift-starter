package com.example

import java.net.InetSocketAddress

import com.example.Server.tracer
import com.example.thrift.generated.{BinaryService, Service2, Service3, User}
import com.twitter.finagle.{Service, Thrift}
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.tracing.{Record, Trace, Tracer}
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol

object OMg extends App {

//  val tracer =
//    ZipkinTracer.mk(host = "172.17.0.1", port = 9410, statsReceiver = JavaLoggerStatsReceiver(), sampleRate = 1f)

  val service2Client =
    Thrift.client.withTracer(tracer).withLabel("client").build[Service2.MethodPerEndpoint]("172.17.0.1:8001")

  val service3Client =
    Thrift.client.withTracer(tracer).withLabel("client").build[Service3.MethodPerEndpoint]("172.17.0.1:8002")

  val query = "zomg"
  while (true) {
    Trace.letId(Trace.nextId, false) {
      Trace.record("Starting")
//      Trace.recordServiceName("ROOT")
//      Trace.recordRpc("ROOTRPC")
//      Trace.recordServerAddr(new InetSocketAddress("0.0.0.0", 10))
//      Trace.enable()
//      Trace.time("TIME")(println("HHEHE"))
//      println(Trace.id)
//      println(Trace.nextId)
//      val traceId = Trace.nextId
      val fut = {
        for {
          res1 <- service2Client.hello(query)
          res2 <- service2Client.hello2(query)
          _ <- Trace.timeFuture("doing something that takes a long while")(Future.value("ehehheeh"))
          res3 <- service3Client.world(query)
          res4 <- service3Client.world2(query)
        } yield s"$res1 -- $res2 -- $res3 -- $res4"
      }

      val res = Await.result(fut)
      println(res)
      Thread.sleep(500)
      Trace.record("Ending")
    }
  }

}

object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  System.getenv().forEach((k, v) => println(s"$k -> $v"))

  val clientAddr = s"${System.getenv("SERVICE3_SERVICE_HOST")}:${System.getenv("SERVICE3_SERVICE_PORT")}"
//  val clientAddr = "ser:8000"
//  val clientAddr = "127.0.0.1:1236"
  println(s"ClientAddr: $clientAddr")

  println(System.getenv())

  val tracer =
    ZipkinTracer.mk(host = "172.17.0.1", port = 9410, statsReceiver = JavaLoggerStatsReceiver(), sampleRate = 1f)

  class ServerImpl extends BinaryService.MethodPerEndpoint {

    val service2Client =
      Thrift.client.withTracer(tracer).withLabel("client12").build[Service2.MethodPerEndpoint]("172.17.0.1:8001")

    val service3Client =
      Thrift.client.withTracer(tracer).withLabel("client13").build[Service3.MethodPerEndpoint]("172.17.0.1:8002")

    def fetchBlob(id: Long): Future[String] = {
      for {
        res1 <- service2Client.hello(id.toString)
        res2 <- service2Client.hello2(id.toString)
        res3 <- service3Client.world(id.toString)
        res4 <- service3Client.world2(id.toString)
      } yield s"$res1 -- $res2 -- $res3 -- $res4"
    }
  }

  val server = Thrift.server.withTracer(tracer).withLabel("server").serveIface(":8000", new ServerImpl)

  val methodPerEndpoint: BinaryService.MethodPerEndpoint =
    Thrift.client.withTracer(tracer).withLabel("client0").build[BinaryService.MethodPerEndpoint]("localhost:8000")

  while (true) {
    val result: Future[String] = methodPerEndpoint.fetchBlob(1234L)

    Await.result(result.map(println))
    Thread.sleep(500)
  }

}
