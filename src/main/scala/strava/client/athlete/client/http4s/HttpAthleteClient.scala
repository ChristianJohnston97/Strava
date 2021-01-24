package strava.client.athlete.client.http4s

import cats.effect.{ContextShift, IO, Sync, Timer}
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Accept, Authorization}
import org.http4s.implicits.http4sLiteralsSyntax
import strava.client.athlete.client.AthleteClient
import strava.domain.athlete.{Athlete, UserStatistic}
import strava.domain.auth.Token

class HttpAthleteClient(client: Client[IO]) extends AthleteClient[IO] with Http4sClientDsl[IO] {

  def getLoggedInAthlete(token: Token): IO[Athlete] = {

    val request = Method.GET(
      uri"""https://www.strava.com/api/v3/athlete""",
      Accept(MediaType.application.json),
      Authorization(Credentials.Token(AuthScheme.Bearer, token.accessToken))
    )
    client.expect(request)(jsonOf[IO, Athlete])
  }

  override def getAthleteStats(token: Token, id: Long): IO[UserStatistic] = {
    val request = Method.GET(
      uri"""https://www.strava.com/api/v3/athlets/$id/stats""",
      Accept(MediaType.application.json),
      Authorization(Credentials.Token(AuthScheme.Bearer, token.accessToken))
    )
    client.expect(request)(jsonOf[IO, UserStatistic])
  }
}

object HttpAthleteClient {
  def apply(client: Client[IO])(implicit cs: ContextShift[IO], timer: Timer[IO], sync: Sync[IO]): AthleteClient[IO] = {
    HttpAthleteClient(client)
  }
}
