package strava.server

import cats.MonadError
import cats.effect.{IO, _}
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import doobie.util.ExecutionContexts
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.config.parser
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Router, Server => H4Server}
import strava.client.athlete.client.AthleteClient
import strava.client.athlete.client.http4s.HttpAthleteClient
import strava.client.auth.client.AuthClient
import strava.client.auth.client.http4s.HttpAuthClient
import strava.server.api.{StravaAuthController, UserStatisticsController}
import strava.server.config.{DatabaseConfig, StoreConfig, _}
import strava.server.repository.user_statistics.doobie.StatisticsRepositoryImpl
import strava.server.service.auth.AuthService
import strava.server.service.auth.impl.{RedisAuthClient, WebAuthService}
import strava.server.service.user.UserStatisticsService
import strava.server.service.user.db.DBUserStatisticsService
import strava.server.service.user.web.WebUserStatisticsService
import sun.net.www.protocol.http.AuthCache

import scala.concurrent.ExecutionContext.Implicits.global


object Server extends IOApp {

  def createServer(implicit ME: MonadError[IO, Throwable]): Resource[IO, H4Server] = {

    import cats.effect._
    val logger = Slf4jLogger.getLogger[IO]

    for {
      // resources and thread pool
      conf <- Resource.liftF(parser.decodePathF[IO, StoreConfig]("strava")) // config
      connEc <- ExecutionContexts.fixedThreadPool[IO](conf.db.poolSize) // used for awaiting connections
      txnEc <- ExecutionContexts.cachedThreadPool[IO] // used for executing blocking JDBC operations
      blocker = Blocker.liftExecutionContext(txnEc) // execution context that is safe to use for blocking operations
      xa <- DatabaseConfig.dbTransactor[IO](conf.db, connEc, blocker)

      // redis
      redisCommands <- Redis[IO].utf8("redis://localhost")
      redisAuthClient: AuthCache = RedisAuthClient(redisCommands)


      // clients
      client: Client[IO] = JavaNetClientBuilder[IO](blocker).create
      authClient: AuthClient = new HttpAuthClient(client)
      athleteClient: AthleteClient[IO] = new HttpAthleteClient(client)

      // repositories
      statisticsRepo = StatisticsRepositoryImpl[IO](xa)

      // services
      authService: AuthService[IO] = WebAuthService(authClient, blocker)
      dbUserStatisticsService: UserStatisticsService[IO] = DBUserStatisticsService(statisticsRepo)
      webUserStatisticsService: UserStatisticsService[IO] = WebUserStatisticsService(redisAuthClient, authService, athleteClient)

      // routes
      httpApp = Router(
        "/strava/auth/token"      -> StravaAuthController[IO](authService).routes,
        "/strava/user/statistics" -> UserStatisticsController[IO](webUserStatisticsService).routes
      ).orNotFound

      // create Blaze server
      server <- BlazeServerBuilder[IO](connEc)
        .bindHttp(conf.server.port, conf.server.host)
        .withHttpApp(httpApp)
        .resource

      - <- Resource.liftF(logger.info("App Running..."))

    } yield server
  }

  def run(args: List[String]): IO[ExitCode] = createServer.use(_ => IO.never).as(ExitCode.Success)
}
