import cats.effect.IO
import com.example.protos.hello._
import fs2._
import io.grpc._
import io.grpc.protobuf.services.ProtoReflectionService
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import fs2.grpc.syntax.all._
// import org.lyranthe.fs2_grpc.java_runtime.implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import cats.effect.kernel.Resource
import cats.effect._

class ExampleImplementation extends GreeterFs2Grpc[IO, io.grpc.Metadata] {
  override def sayHello(request: HelloRequest,
                        clientHeaders: Metadata): IO[HelloReply] = {
    IO(HelloReply("Request name is: " + request.name))
  }

  override def sayHelloStream(
      request: Stream[IO, HelloRequest],
      clientHeaders: Metadata): Stream[IO, HelloReply] = {
    request.evalMap(req => sayHello(req, clientHeaders))
  }
}



object Main extends IOApp {

override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- helloService.use(runService)
    } yield ExitCode.Success
  val helloService: Resource[IO, ServerServiceDefinition] = 
    GreeterFs2Grpc.bindServiceResource[IO](new ExampleImplementation())

  def runService(service: ServerServiceDefinition) = 
    NettyServerBuilder
    .forPort(9999)
    .addService(service)
    .addService(ProtoReflectionService.newInstance())
    .resource[IO]
    .evalMap(server => IO(server.start()))
    .useForever

}
  

//   val helloService: ServerServiceDefinition =
//     GreeterFs2Grpc.bindService(new ExampleImplementation)
//   def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
//     ServerBuilder
//       .forPort(9999)
//       .addService(helloService)
//       .addService(ProtoReflectionService.newInstance())
//       .stream[IO]
//       .evalMap(server => IO(server.start()))
//       .evalMap(_ => IO.never)
//   }

