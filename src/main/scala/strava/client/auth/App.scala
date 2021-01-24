package strava.client.auth

import cats.effect._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import strava.client.auth.client.http4s.HttpAuthClient

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * CLI to retrieve OAuth bearer token to be used in subsequent calls to Strava API
 */
object App extends IOApp with Http4sClientDsl[IO] {
  def run(args: List[String]): IO[ExitCode] = {
    if (args.size != 3) {
      IO(Console.err.println("Syntax: Auth get token: <strava.client id> <strava.client secret>")).as(ExitCode.Error)
    } else {
      Blocker[IO].use { blocker =>
        BlazeClientBuilder[IO](global).resource.use { client =>
          val authService = new HttpAuthClient(client)
          authService.getBearerToken(blocker, args.head, args.tail.head)
        }
      }.as(ExitCode.Success)
    }
  }
}
