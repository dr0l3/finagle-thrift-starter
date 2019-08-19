package com.example2

import com.example.thrift.generated.{BinaryService, Service2, Service3, User}
import com.twitter.finagle.Thrift
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  val tracer = ZipkinTracer.mk(host = "172.17.0.1",
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)

  println(System.getenv())
  class ServerImpl extends Service2.MethodPerEndpoint {
    val service3Client=
      Thrift.client
        .withTracer(tracer)
        .withLabel("client2")
        .build[Service3.MethodPerEndpoint]("172.17.0.1:8002")

    override def hello(id: String): Future[String] = {
      for {
      res1 <- service3Client.world(id)
      res2 <- service3Client.world2(id)
      } yield {
        s"$res1 - $res2 - Hello $id"
      }
    }

    override def hello2(id: String): Future[String] = {
      for {
        res1 <- service3Client.world2(id)
        res2 <- service3Client.world(id)
      } yield {
        s"$res1 - $res2 - Hello2 $id"
      }
    }
  }

  val server = Thrift.server
    .withTracer(tracer)
    .withLabel("server2")
    .serveIface(":8001", new ServerImpl)

  Await.ready(server)
}