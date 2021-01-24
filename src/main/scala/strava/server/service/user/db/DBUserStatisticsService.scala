package strava.server.service.user.db

import strava.domain.athlete.UserStatistic
import strava.server.repository.user_statistics.UserStatisticsRepository
import strava.server.service.user.UserStatisticsService

import scala.concurrent.ExecutionContext

class DBUserStatisticsService[F[_]](statisticsRepo: UserStatisticsRepository[F])(implicit executionContext: ExecutionContext)
  extends UserStatisticsService[F] {
  override def getUserStatistic(userId: Long): F[Option[UserStatistic]] =
    statisticsRepo.getUserStatistic(userId)

  override def saveUserStatistics(statistic: UserStatistic): F[Long] =
    statisticsRepo.saveUserStatistics(statistic)

  override def deleteUserStatistics(userId: Long): F[Option[UserStatistic]] =
    statisticsRepo.deleteUserStatistics(userId)

  override def updateUserStatistic(userId: Long, statistic: UserStatistic): F[UserStatistic] =
    statisticsRepo.updateUserStatistic(userId, statistic)
}


object DBUserStatisticsService {
  def apply[F[_]](statisticsRepo: UserStatisticsRepository[F])(implicit executionContext: ExecutionContext): DBUserStatisticsService[F] =
    new DBUserStatisticsService[F](statisticsRepo)(executionContext)
}

