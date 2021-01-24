package strava.server.service.user.web

import cats.effect.IO
import cats.implicits._
import strava.client.athlete.client.AthleteClient
import strava.domain.athlete.UserStatistic
import strava.domain.auth.Token
import strava.server.service.auth.{AuthCache, AuthService}
import strava.server.service.user.UserStatisticsService

import scala.concurrent.ExecutionContext

class WebUserStatisticsService(authCache: AuthCache[IO], authService: AuthService[IO], athleteClient: AthleteClient[IO])(implicit executionContext: ExecutionContext)
  extends UserStatisticsService[IO] {

  override def getUserStatistic(userId: Long): IO[Option[UserStatistic]] = {
    (for {
      tokenOpt <- authCache.getToken("client_id")
    } yield tokenOpt match {
      case Some(token) =>
        athleteClient.getAthleteStats(Token(token), userId)
      case None => authService.getBearerToken("client_id", "client_secret")
        .flatMap(token => athleteClient.getAthleteStats(token, userId))
    }).flatten.map(Option(_)) // TODO improve this optionality
  }

  override def saveUserStatistics(statistic: UserStatistic): IO[Long] = ???
  override def deleteUserStatistics(userId: Long): IO[Option[UserStatistic]] = ???
  override def updateUserStatistic(userId: Long, statistic: UserStatistic): IO[UserStatistic] = ???
}

object WebUserStatisticsService {
  def apply(authCache: AuthCache[IO], authService: AuthService[IO], athleteClient: AthleteClient[IO])(implicit executionContext: ExecutionContext): WebUserStatisticsService =
    new WebUserStatisticsService(authCache, authService, athleteClient)(executionContext)
}