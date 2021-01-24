package strava.server.api

import cats._
import io.circe.generic.auto.exportEncoder
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import strava.server.service.auth.AuthService

class StravaAuthController[F[_]: Defer: JsonDecoder: MonadThrow](authService: AuthService[F]) extends Http4sDsl[F] {

  private val prefixPath = "/strava/auth/token"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / userId / secret =>
      Ok(authService.getBearerToken(userId, secret))
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )
}

object StravaAuthController {
  def apply[F[_] : Defer : JsonDecoder : MonadThrow](authService: AuthService[F]): StravaAuthController[F] = new StravaAuthController(authService)
}
