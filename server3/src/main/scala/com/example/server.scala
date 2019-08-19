package com.example3

import com.example.thrift.generated.{BinaryService, Service3}
import com.twitter.finagle.Thrift
import com.twitter.finagle.stats.{JavaLoggerStatsReceiver, NullStatsReceiver, StatsReceiver}
import com.twitter.finagle.tracing.{Trace, Tracer}
import com.twitter.finagle.zipkin.core
import com.twitter.finagle.zipkin.thrift.{ScribeZipkinTracer, ZipkinTracer}
import com.twitter.util.{Await, Future}


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  println(System.getenv())

  class ServerImpl extends Service3.MethodPerEndpoint {
    override def world(id: String): Future[String] = {
      Trace.record("world")
      Future.value(s"world $id")
    }

    override def world2(id: String): Future[String] = {
      Trace.record("World2")
      Future.value(s"world2 $id")
    }
  }



  val tracer = ZipkinTracer.mk(host = "zipkin-service",
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)

  val server = Thrift.server
    .withLabel("server3")
    .withTracer(tracer)
    .serveIface(":8002", new ServerImpl)
  Await.ready(server)
}