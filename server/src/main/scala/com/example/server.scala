package com.example

import com.example.thrift.generated.{BinaryService, User}
import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}


object Server extends App {

  class ServerImpl extends BinaryService.MethodPerEndpoint {
    def fetchBlob(id: Long): Future[String] = {
      Future.value("Hello world")
    }
  }
  val server = Thrift.server.serveIface("localhost:1234", new ServerImpl)


  val methodPerEndpoint: BinaryService.MethodPerEndpoint =
    Thrift.client.build[BinaryService.MethodPerEndpoint]("localhost:1234")

  val result: Future[String] = methodPerEndpoint.fetchBlob(1234L)


  Await.result(result.map(println))

}