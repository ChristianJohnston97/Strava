package strava.domain.athlete

import cats.implicits._
import io.circe.{Decoder, HCursor}

case class Athlete(id: Long, username: String)

object Athlete {
  implicit val decodeFoo: Decoder[Athlete] = (c: HCursor) =>
    (c.downField("id").as[Long], c.downField("username").as[String]).mapN(
      (id, username) => Athlete(id, username)
    )
}