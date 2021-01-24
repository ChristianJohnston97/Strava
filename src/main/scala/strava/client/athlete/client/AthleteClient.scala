package strava.client.athlete.client

import strava.domain.athlete.{Athlete, UserStatistic}
import strava.domain.auth.Token

trait AthleteClient[F[_]] {
  def getLoggedInAthlete(token: Token): F[Athlete]
  def getAthleteStats(token: Token, id: Long): F[UserStatistic]
}
