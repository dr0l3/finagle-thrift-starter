package com.example

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.{Service, Thrift}
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.stats.JavaLoggerStatsReceiver
import com.twitter.finagle.thrift.{ThriftClientFramedCodec, ThriftClientRequest, ThriftServerFramedCodec}
import com.twitter.finagle.zipkin.thrift.ZipkinTracer
import com.twitter.util.{Await, Future}
import org.apache.thrift.protocol.TBinaryProtocol


object Server extends App {

  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()


//  val tracer = ZipkinTracer.mk(host = "localhost",
//    port = 9410,
//    statsReceiver = JavaLoggerStatsReceiver(),
//    sampleRate = 1f)

//  class ServerImpl extends BinaryService.MethodPerEndpoint {
//    val methodPerEndpoint: BinaryService.MethodPerEndpoint =
//      Thrift.client
//        .withTracer(tracer)
//        .withLabel("client1")
//        .build[BinaryService.MethodPerEndpoint]("localhost:1235")
//
//    def fetchBlob(id: Long): Future[String] = {
//      methodPerEndpoint.fetchBlob(1234L)
//    }
//  }

  class OldServerImpl extends BinaryService.FutureIface {
    val client = new BinaryService.FinagledClient(ClientBuilder()
      .name("client-server1")
      .hostConnectionLimit(500)
      .codec(ThriftClientFramedCodec())
      .failFast(false)
      .tcpConnectTimeout(com.twitter.util.Duration(1, TimeUnit.SECONDS))
      .requestTimeout(com.twitter.util.Duration(30, TimeUnit.SECONDS))
      .daemon(true)
      .keepAlive(true)
      .dest("localhost:1235")
      .build())
    override def fetchBlob(id: Long): Future[String] = {
      client.fetchBlob(1234L)
    }
  }

  val addr = new InetSocketAddress("localhost",1234)

  val service:  Service[Array[Byte], Array[Byte]] = new BinaryService.FinagledService(
    iface = new OldServerImpl,
    protocolFactory = new TBinaryProtocol.Factory()
  )

  val oldServer = ServerBuilder()
    .codec(ThriftServerFramedCodec())
    .name("server1")
    .bindTo(addr)
    .build(service)


//  val server = Thrift.server
//    .withTracer(tracer)
//    .withLabel("server")
//    .serveIface("localhost:1234", new ServerImpl)

//  val methodPerEndpoint: BinaryService.MethodPerEndpoint =
//    Thrift.client
//      .withTracer(tracer)
//      .withLabel("client0")
//      .build[BinaryService.MethodPerEndpoint]("localhost:1234")

  val client = new BinaryService.FinagledClient(ClientBuilder()
    .name("client-server0")
    .hostConnectionLimit(500)
    .codec(ThriftClientFramedCodec())
    .failFast(false)
    .tcpConnectTimeout(com.twitter.util.Duration(1, TimeUnit.SECONDS))
    .requestTimeout(com.twitter.util.Duration(30, TimeUnit.SECONDS))
    .daemon(true)
    .keepAlive(true)
    .dest("localhost:1234")
    .build())


  while (true) {
//    val result: Future[String] = methodPerEndpoint.fetchBlob(1234L)

    val res = client.fetchBlob(1234L)
    Await.result(res.map(println))

//    Await.result(result.map(println))
    Thread.sleep(500)
  }


}