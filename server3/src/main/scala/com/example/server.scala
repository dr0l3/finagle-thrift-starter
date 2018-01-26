package com.example3

import com.example.thrift.generated.BinaryService
import com.twitter.finagle.Thrift
import com.twitter.finagle.stats.{JavaLoggerStatsReceiver, NullStatsReceiver, StatsReceiver}
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.zipkin.core
import com.twitter.finagle.zipkin.thrift.{ScribeZipkinTracer, ZipkinTracer}
import com.twitter.util.{Await, Future}


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  println(System.getenv())

  class ServerImpl extends BinaryService.MethodPerEndpoint {
    def fetchBlob(id: Long): Future[String] = {
      Trace.record("starting some extremely expensive computation")

      Thread.sleep(5000)
      Trace.record("Finished")
      Future.value("Hello world")
    }
  }



  val tracer = ZipkinTracer.mk(host = "zipkin-service",
    port = 9410,
    statsReceiver = JavaLoggerStatsReceiver(),
    sampleRate = 1f)

  val server = Thrift.server
    .withLabel("server3")
    .withTracer(tracer)
    .serveIface(":8000", new ServerImpl)
  Await.ready(server)
}