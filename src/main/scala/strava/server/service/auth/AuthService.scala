package strava.server.service.auth

import strava.domain.auth.Token

trait AuthService[F[_]] {
  def getBearerToken(clientId: String, clientSecret: String): F[Token]
}
