package strava.server.service.auth.impl

import dev.profunktor.redis4cats.RedisCommands
import strava.domain.auth.Token
import strava.server.service.auth.AuthCache

import scala.concurrent.duration.{FiniteDuration, MINUTES}

class RedisAuthClient[F[_]](redis: RedisCommands[F, String, String]) extends AuthCache[F] {

  override def addToken(token: Token, clientId: String): F[Unit] = {
    redis.hSet(clientId, "token", token.accessToken)
    redis.expire(clientId, new FiniteDuration(10, MINUTES))
  }

  override def getToken(clientId: String): F[Option[String]] =
    redis.get(clientId)

  override def clearCache(clientId: String): F[Unit] =
    redis.hDel(clientId, "token")
}

object RedisAuthClient {
  def apply[F[_]](redis: RedisCommands[F, String, String]): RedisAuthClient[F] = new RedisAuthClient(redis)
}