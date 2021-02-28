# Strava Project using TypeLevel libraries
Project to interact with the Strava API through an HTTP client and then creating my own HTTP4s server to programtically save this data to an DB.

### Overview
Strava exposes user data through a REST API: https://developers.strava.com and https://developers.strava.com/docs/reference/
I have created a HTTP client `/src/main/scala/strava/client` using http4s to query the Strava API.
For example: `http GET "https://www.strava.com/api/v3/athlete" "Authorization: Bearer [[token]]"` which returns info about the currently authenticated athlete.
To make this authenticated request, we need an authentication token (see 'OAuth' session).
Also created an HTTP server `/src/main/scala/strava/server` using http4s to manage the persisting of this data to a database. Flyway is used to manage DB schema migrations.

### Libraries Used
- Cats (type class instances)
- Cats effect (IO Monad)
- Doobie (Functional JDBC layer)
- HTTP4S (HTTP client and server)
- Flyway (Database Migrations) with MySQL
- Circe (Serialisation)
- Redis4Cats (Redis Client)
- Log4Cats (Logging)

### OAuth
Strava used OAuth2 for authentication. The docs are very good: https://developers.strava.com/docs/authentication/.
The basic premise is that the application is registered with Strava and this provides us with a secret. We can then 
call the Strava authentication endpoint, passing in this secret which provides with an authentication bearer token.
We can then use this token in subsequent calls:

We can test this with curl:
```bash
curl -X POST https://www.strava.com/api/v3/oauth/token \
  -d client_id=ReplaceWithClientID \
  -d client_secret=ReplaceWithClientSecret \
  -d grant_type=authorization_code
```
I have implemented a bearer token retrieval service which:
- logs into Strava and grants our application access to read their activities
- An authorization code is provided to our application
- The application fetches an access token using the authorization code from previous step
- The access token is then used in all Strava API requests

```scala
trait AuthClient {
  def getBearerToken(blocker: Blocker, clientId: String, clientSecret: String): IO[Token]
}
```


### Programming Style
I have used the Final Tagless pattern throughout the server-side of this project. I have written about this pattern 
here: https://medium.com/panaseer-labs-engineering-data-science/architecting-a-flexible-and-purely-functional-scala-back-end-using-slick-and-tagless-final-97b9754f5817
however in short it is a technique used in Scala to make code more flexible and testable and is used to track effects.
It requires an understanding of a number of functional programming techniques such as:
- higher kinded types (F[_])
- programming with effects
- type classes

```scala
trait UserStatisticsRepository[F[_]] {
  def getUserStatistic(userId: Long): F[Option[UserStatistic]]
  def saveUserStatistics(statistic: UserStatistic): F[Long]
  def deleteUserStatistics(userId: Long): F[Option[UserStatistic]]
  def updateUserStatistic(userId: Long, statistic: UserStatistic): F[UserStatistic]
}
```

### IO Monad
Throughout this code I have used the IO monad from Cats Effect library however, due to Final Tagless technique, this could
easily be swapped out to ZIO or Monix Task. Future is not used as it is not referentially transparent.

### Infrastructure and Deployment
- Docker Compose (Dockerised Flyway and MySQL)
- TODO: AWS hosting (ECS vs EC2)


