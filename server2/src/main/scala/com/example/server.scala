package com.example2

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.{Service, Thrift}
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.thrift.{ThriftClientFramedCodec, ThriftClientRequest, ThriftServerFramedCodec}
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()


  class OldServerImpl extends BinaryService.FutureIface {
    val client = new BinaryService.FinagledClient(ClientBuilder()
      .name("client-server2")
      .hostConnectionLimit(500)
      .codec(ThriftClientFramedCodec())
      .failFast(false)
      .tcpConnectTimeout(com.twitter.util.Duration(1, TimeUnit.SECONDS))
      .requestTimeout(com.twitter.util.Duration(30, TimeUnit.SECONDS))
      .daemon(true)
      .keepAlive(true)
      .dest("127.0.0.1:1236")
      .build())
    override def fetchBlob(id: Long): Future[String] = {
      client.fetchBlob(1234L)
    }
  }

//  val tracer = ZipkinTracer.mk(host = "127.0.0.1",
//    port = 9410,
//    statsReceiver = JavaLoggerStatsReceiver(),
//    sampleRate = 1f)

//  class ServerImpl extends BinaryService.MethodPerEndpoint {
//    val methodPerEndpoint: BinaryService.MethodPerEndpoint =
//      Thrift.client
//        .withTracer(tracer)
//        .withLabel("client2")
//        .build[BinaryService.MethodPerEndpoint]("127.0.0.1:1236")
//
//    def fetchBlob(id: Long): Future[String] = {
//      methodPerEndpoint.fetchBlob(1234L)
//    }
//  }

  val addr = new InetSocketAddress("127.0.0.1",1235)

  val service:  Service[Array[Byte], Array[Byte]] = new BinaryService.FinagledService(
    iface = new OldServerImpl,
    protocolFactory = new TBinaryProtocol.Factory()
  )

  val oldServer = ServerBuilder()
    .codec(ThriftServerFramedCodec())
    .name("server2")
    .bindTo(addr)
    .build(service)


//  val server = Thrift.server
//    .withTracer(tracer)
//    .withLabel("server2")
//    .serveIface("127.0.0.1:1235", new ServerImpl)

//  Await.ready(server)

  Await.result(oldServer)
}