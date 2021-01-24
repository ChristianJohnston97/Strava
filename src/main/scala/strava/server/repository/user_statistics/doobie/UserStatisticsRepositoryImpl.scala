package strava.server.repository.user_statistics.doobie

import cats.MonadError
import cats.effect.BracketThrow
import doobie.implicits.{toSqlInterpolator, _}
import doobie.{Query0, Transactor, Update0}
import strava.domain.athlete.UserStatistic
import strava.server.repository.user_statistics.UserStatisticsRepository

/**
 * Doobie interpretation of [[UserStatisticsRepository]]
 */
class StatisticsRepositoryImpl[F[_]: BracketThrow](val xa: Transactor[F])(implicit ME: MonadError[F, Throwable])
  extends UserStatisticsRepository[F] {

  import StatisticQueries._
  import cats.implicits._

  override def getUserStatistic(userId: Long): F[Option[UserStatistic]] =
    getUserStatistics(userId).option.transact(xa)

  override def saveUserStatistics(statistic: UserStatistic): F[Long] =
    insertUserStatistic(statistic).withUniqueGeneratedKeys[Long]("userId").transact(xa)

  override def deleteUserStatistics(userId: Long): F[Option[UserStatistic]] =
    getUserStatistic(userId).flatMap(userStatistic => delete(userId).run.transact(xa).as(userStatistic))

  override def updateUserStatistic(userId: Long, userStatistic: UserStatistic): F[UserStatistic] =
    StatisticQueries.update(userId, userStatistic).run.transact(xa).as(userStatistic)
}

object StatisticsRepositoryImpl {
  def apply[F[_]: BracketThrow](xa: Transactor[F])(implicit ME: MonadError[F, Throwable]): StatisticsRepositoryImpl[F] = new StatisticsRepositoryImpl(xa)
}

object StatisticQueries {
  def getAllUserStatistics: Query0[UserStatistic] = sql"""
    SELECT userId, recent_run_totals, all_run_totals, recent_swim_totals, biggest_ride_distance, ytd_swim_totals, all_swim_totals
    FROM STATISTIC
  """.query

  def getUserStatistics(userId: Long): Query0[UserStatistic] = sql"""
    SELECT userId, recent_run_totals, all_run_totals, recent_swim_totals, biggest_ride_distance, ytd_swim_totals, all_swim_totals
    FROM STATISTIC
    WHERE userId = $userId
  """.query

  def insertUserStatistic(userStatistic: UserStatistic): Update0 = sql"""
    INSERT INTO STATISTIC (recent_run_totals, all_run_totals, recent_swim_totals, biggest_ride_distance, ytd_swim_totals, all_swim_totals)
    VALUES (${userStatistic.recent_run_totals}, ${userStatistic.all_run_totals},
    ${userStatistic.recent_swim_totals}, ${userStatistic.biggest_ride_distance}, ${userStatistic.ytd_swim_totals},
    ${userStatistic.all_swim_totals})
  """.update

  def delete(userId: Long): Update0 = sql"""
    DELETE FROM STATISTIC WHERE userId = $userId
  """.update

  def update(id: Long, userStatistic: UserStatistic): Update0 = sql"""
    UPDATE STATISTIC
    SET recent_run_totals = ${userStatistic.recent_run_totals},
        all_run_totals = ${userStatistic.all_run_totals},
        recent_swim_totals = ${userStatistic.recent_swim_totals},
        biggest_ride_distance = ${userStatistic.biggest_ride_distance},
        ytd_swim_totals = ${userStatistic.ytd_swim_totals},
        all_swim_totals = ${userStatistic.all_swim_totals}
    WHERE userId = $id
  """.update
}
