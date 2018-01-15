package com.example3

import java.net.{InetAddress, InetSocketAddress, SocketAddress}

import com.example.thrift.generated.BinaryService
import com.twitter.finagle.{Service, Thrift}
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.stats.{JavaLoggerStatsReceiver, NullStatsReceiver, StatsReceiver}
import com.twitter.finagle.thrift.ThriftServerFramedCodec
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.zipkin.core
import com.twitter.finagle.zipkin.thrift.{ScribeZipkinTracer, ZipkinTracer}
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()

  class OldServerImpl extends BinaryService.FutureIface {
    override def fetchBlob(id: Long): Future[String] = {
      Trace.record("starting some extremely expensive computation")

      Thread.sleep(5000)
      Trace.record("Finished")
      Future.value("Hello world")
    }
  }

//  class ServerImpl extends BinaryService.MethodPerEndpoint {
//    def fetchBlob(id: Long): Future[String] = {
//      Trace.record("starting some extremely expensive computation")
//
//      Thread.sleep(5000)
//      Trace.record("Finished")
//      Future.value("Hello world")
//    }
//  }



//  val tracer = ZipkinTracer.mk(host = "localhost",
//    port = 9410,
//    statsReceiver = JavaLoggerStatsReceiver(),
//    sampleRate = 1f)

  val addr = new InetSocketAddress("localhost",1236)

  val service:  Service[Array[Byte], Array[Byte]] = new BinaryService.FinagledService(
    iface = new OldServerImpl,
    protocolFactory = new TBinaryProtocol.Factory()
  )

  val oldServer = ServerBuilder()
    .codec(ThriftServerFramedCodec())
    .name("server3")
    .bindTo(addr)
    .build(service)


//  val server = Thrift.server
//    .withLabel("server3")
//    .withTracer(tracer)
//    .serveIface("localhost:1236", new ServerImpl)
//  Await.ready(server)

  Await.result(oldServer)
}