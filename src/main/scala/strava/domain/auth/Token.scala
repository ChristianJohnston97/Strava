package strava.domain.auth

import io.circe._

case class Token(accessToken: String)

object Token {
  implicit val decoder: Decoder[Token] =
    (c: HCursor) => c.downField("access_token").as[String].map(Token(_))
}

