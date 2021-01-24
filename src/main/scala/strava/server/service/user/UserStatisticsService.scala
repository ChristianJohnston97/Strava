package strava.server.service.user

import strava.domain.athlete.UserStatistic

trait UserStatisticsService[F[_]] {
  def getUserStatistic(userId: Long): F[Option[UserStatistic]]
  def saveUserStatistics(statistic: UserStatistic): F[Long]
  def deleteUserStatistics(userId: Long): F[Option[UserStatistic]]
  def updateUserStatistic(userId: Long, statistic: UserStatistic): F[UserStatistic]
}
