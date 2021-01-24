package strava.domain.athlete

import cats.implicits._
import io.circe.{Decoder, HCursor}

case class UserStatistic(recent_run_totals: String,
                         all_run_totals: String,
                         recent_swim_totals: String,
                         biggest_ride_distance: Long,
                         ytd_swim_totals: String,
                         all_swim_totals: String)

object UserStatistic {
  implicit val decodeFoo: Decoder[UserStatistic] = (c: HCursor) =>
    (c.downField("recent_run_totals").as[String],
      c.downField("all_run_totals").as[String],
      c.downField("recent_swim_totals").as[String],
      c.downField("biggest_ride_distance").as[Long],
      c.downField("ytd_swim_totals").as[String],
      c.downField("all_swim_totals").as[String]
      )
      .mapN(
      (recent_run_totals, all_run_totals, recent_swim_totals, biggest_ride_distance, ytd_swim_totals, all_swim_totals) =>
        UserStatistic(recent_run_totals, all_run_totals, recent_swim_totals, biggest_ride_distance, ytd_swim_totals, all_swim_totals)
    )
}