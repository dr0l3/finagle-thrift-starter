import cats.effect.IO
import com.example.thrift.generated.{BinaryService, TestService}
import com.twitter.finagle.Thrift
import com.twitter.util.Await
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import scala.collection.JavaConverters._

object Main extends StreamApp[IO] {
  import cats.implicits._
  import org.http4s.server.blaze._
  import org.http4s.implicits._
  import cats.effect._, org.http4s._, org.http4s.dsl.io._, scala.concurrent.ExecutionContext.Implicits.global
  import org.http4s.server.blaze._
  import fs2.StreamApp.ExitCode
  import fs2.{Stream, StreamApp}
  val dest = System.getenv().asScala.toMap.get("dest").getOrElse {
    println("USING DEFAULT")
    "service4.test:8001"
  }


  val client = Thrift.client.build[TestService.MethodPerEndpoint](dest)

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    val helloWorldService = HttpService[IO] {
      case GET -> Root / "hello" / name =>
        Ok(Await.result(client.hello(name)))

      case GET -> Root / "load" / amount =>
        Ok(Await.result(client.loadTest(amount.toLong)))
    }
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(helloWorldService, "/")
      .serve

  }
}
