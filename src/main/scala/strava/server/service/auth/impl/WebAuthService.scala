package strava.server.service.auth.impl

import cats.effect.{Blocker, IO}
import strava.client.auth.client.AuthClient
import strava.domain.auth.Token
import strava.server.service.auth.AuthService

import scala.concurrent.ExecutionContext

class WebAuthService(authClient: AuthClient,
                     blocker: Blocker)(implicit executionContext: ExecutionContext) extends AuthService[IO] {

  override def getBearerToken(clientId: String, clientSecret: String): IO[Token] =
    authClient.getBearerToken(blocker, clientId, clientSecret)

}

object WebAuthService {
  def apply(authClient: AuthClient, blocker: Blocker)(implicit executionContext: ExecutionContext): WebAuthService =
    new WebAuthService(authClient, blocker)
}
