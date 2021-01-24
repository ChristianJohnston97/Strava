package strava.client.auth.client.http4s

import cats.effect._
import cats.effect.concurrent.Deferred
import fs2.Stream
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import strava.client.auth.client.AuthClient
import strava.domain.auth.{AuthCode, Token}

import scala.concurrent.ExecutionContext.Implicits.global

class HttpAuthClient(client: Client[IO])(implicit cs: ContextShift[IO], timer: Timer[IO]) extends AuthClient with Http4sClientDsl[IO] {

  override def getBearerToken(blocker: Blocker, clientId: String, clientSecret: String): IO[Token] = {
    getAuthorizationCode(blocker, clientId).flatMap(fetchBearerToken(clientId, clientSecret, _))
  }

  private[this] def getAuthorizationCode(blocker: Blocker, clientId: String): IO[AuthCode] = {
    Deferred[IO, AuthCode].flatMap { deferredAuthCode =>
      BlazeServerBuilder[IO](global)
        .bindHttp(port = 0)
        .withHttpApp {
          object CodeParam extends QueryParamDecoderMatcher[String]("code")
          HttpRoutes.of[IO] {
            case GET -> Root / "exchange_token" :? CodeParam(code) =>
              deferredAuthCode.complete(AuthCode(code)).as(Response(Status.Ok))
          }.orNotFound
        }
        .stream.flatMap { server =>
        val port = server.address.getPort
        Stream.eval(requestAuthCode(blocker, clientId, port) *> deferredAuthCode.get)
      }.compile.lastOrError
    }
  }

  private[this] def requestAuthCode(blocker: Blocker, clientId: String, localPort: Int): IO[Unit] = {
    blocker.delay[IO, Unit] {
      import scala.sys.process._
      s"open http://www.strava.com/oauth/authorize?client_id=$clientId&response_type=code&redirect_uri=http://localhost:$localPort/exchange_token&approval_prompt=force&scope=read,activity:read".!
    }
  }

  def fetchBearerToken(clientId: String, clientSecret: String, authorizationCode: AuthCode): IO[Token] = {
    val request = Method.POST(
      UrlForm(
        "client_id" -> clientId,
        "client_secret" -> clientSecret,
        "code" -> authorizationCode.value,
        "grant_type" -> "authorization_code"),
      uri"""https://www.strava.com/oauth/token""")
    client.expect(request)(jsonOf[IO, Token])
  }
}
