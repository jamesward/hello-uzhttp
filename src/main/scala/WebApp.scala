import java.net.InetSocketAddress

import uzhttp.Response
import uzhttp.server.Server
import zio.blocking.Blocking
import zio.clock.Clock
import zio.system._
import zio.{App, ZIO}

import scala.util.Try

object WebApp extends App {

  sealed trait PortError
  case class InvalidPortValue(port: String) extends PortError
  case class SecurityError(e: SecurityException) extends PortError

  // Tries to read the `PORT` environment variable and convert it to a valid port value
  // Defaults to 8080, only if there was no `PORT` env var
  // A Char (uint16) is used to store the port value
  // See: https://twitter.com/jroper/status/1217525231868239872
  //
  // todo: possibly read the default from the environment
  //
  val zioPort: ZIO[System, PortError, Char] = {
    def convertToCharOrFail(s: String): Either[InvalidPortValue, Char] = {
      {
        for {
          i <- Try(s.toInt)
          if i >= Char.MinValue
          if i <= Char.MaxValue
        } yield i.toChar
      }.toEither.left.map(_ => InvalidPortValue(s))
    }

    env("PORT").mapError(SecurityError).flatMap { maybePort =>
      ZIO.fromEither {
        maybePort.fold[Either[InvalidPortValue, Char]](Right(8080.toChar))(convertToCharOrFail)
      }
    }
  }

  override def run(args:  List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    def server(port: Char): ZIO[Blocking with Clock, Throwable, Nothing] = {
      Server.builder(new InetSocketAddress(port)).handleAll { _ =>
        ZIO.succeed(Response.plain("hello, world"))
      }.serve.useForever
    }

    {
      for {
        port <- zioPort.mapError {
          case InvalidPortValue(s) => new Error(s"The specified PORT '$s' was invalid")
          case SecurityError(e) => e
        }
        server <- server(port)
      } yield server
    }.fold(t => {
      // not in zio because we are dying
      // but maybe it'd be nice to tapError
      t.printStackTrace()
      1
    }, _ => 0)
  }

}
