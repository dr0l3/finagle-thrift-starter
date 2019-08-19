import com.example.thrift.generated.TestService

object Main extends App {
  import com.example.thrift.generated.BinaryService
  import com.twitter.finagle.Thrift
  import com.twitter.util.{Await, Future}
  import org.apache.log4j.BasicConfigurator

  BasicConfigurator.configure()


  class ServerImpl extends TestService.MethodPerEndpoint {
    override def hello(name: String): Future[String] = {
      println("Received helle")
      Future.value(s"Hello: $name")
    }

    override def loadTest(round: Long): Future[String] = Future{
      println("Received fib")
      def fib: Stream[Long] = {
        def tail(h: Long, n: Long): Stream[Long] = h #:: tail(n, h + n)
        tail(0, 1)
      }

      def fibSlow(number: Long): Long = {
        number match {
          case 0 => 0
          case 1 => 1
          case _ => fibSlow(number - 1) + fibSlow(number - 2)
        }
      }
      val nums = fib take round.toInt
      val fibs = fibSlow(round)


      s"The fib sequence is : ${nums.toList}. The last number if $fibs"
    }
  }

  val server = Thrift.server
    .serveIface(":8001", new ServerImpl)

  Await.ready(server)
}
