package strava.server.api

import cats._
import cats.implicits._
import io.circe.generic.auto.exportEncoder
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.{JsonDecoder, toMessageSynax}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import strava.domain.athlete.UserStatistic
import strava.server.service.user.UserStatisticsService

class UserStatisticsController[F[_]: Defer: JsonDecoder: MonadThrow](userStatisticsService: UserStatisticsService[F])
  extends Http4sDsl[F] {

  private val prefixPath = "/user/statistics"

  implicit val convertStringToLong: String => Long = (str: String) => str.toLong

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / userId =>
      Ok(userStatisticsService.getUserStatistic(userId))
    case request @ POST -> Root => request.asJsonDecode[UserStatistic].flatMap { userStatistic =>
      Ok(userStatisticsService.saveUserStatistics(userStatistic))
    }
    case request @ PUT -> Root / userId =>
      request.asJsonDecode[UserStatistic].flatMap { userStatistic =>
        Ok(userStatisticsService.updateUserStatistic(userId, userStatistic))
      }
    case DELETE -> Root / userId =>
      Ok(userStatisticsService.deleteUserStatistics(userId))
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}

object UserStatisticsController {
  def apply[F[_] : Defer : JsonDecoder : MonadThrow](userStatisticsService: UserStatisticsService[F]):
  UserStatisticsController[F] = new UserStatisticsController(userStatisticsService)
}

