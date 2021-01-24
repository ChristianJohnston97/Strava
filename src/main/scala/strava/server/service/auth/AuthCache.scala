package strava.server.service.auth

import strava.domain.auth.Token

trait AuthCache[F[_]] {
  def addToken(tokenIO: Token, clientId: String): F[Unit]

  def getToken(clientId: String): F[Option[String]]

  def clearCache(clientId: String): F[Unit]
}
