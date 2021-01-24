package strava.client.auth.client

import cats.effect.{Blocker, IO}
import strava.domain.auth.Token

/**
 * Service to handle authentication with Strava via OAuth2
 */
trait AuthClient {
  def getBearerToken(blocker: Blocker, clientId: String, clientSecret: String): IO[Token]
}
