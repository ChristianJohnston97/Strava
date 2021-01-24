package strava.server.config

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import cats.syntax.functor._
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

case class DatabaseConfig(url: String, driver: String, user: String, password: String, poolSize: Int)

object DatabaseConfig {

  def dbTransactor[F[_]: Async: ContextShift](
      dbc: DatabaseConfig,
      connEc: ExecutionContext,
      blocker: Blocker
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor
      .newHikariTransactor[F](dbc.driver, dbc.url, dbc.user, dbc.password, connEc, blocker)

  /**
    * Runs the flyway migrations against the target database
    */
  def initializeDb[F[_]](databaseConfig: DatabaseConfig)(implicit S: Sync[F]): F[Unit] =
    S.delay {
        val flyway: Flyway = {
          Flyway
            .configure()
            .dataSource(databaseConfig.url, databaseConfig.user, databaseConfig.password)
            .load()
        }
        flyway.migrate()
      }
      .as(())
}
