# Strava Project using TypeLevel libraries
Project to interact with the Strava API through an HTTP client and then creating my own HTTP4s server.

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
Throughout this code I have used the IO monad from cats effect library however, due to Final Tagless technique, this could
easily be swapped out to ZIO or Monix Task. Future is not used as it is not referentially transparent.

### Libraries Used
- Cats (type class instances)
- Cats effect (IO Monad)
- Doobie (Functional JDBC layer)
- HTTP4S (HTTP client and server)
- Flyway (Database Migrations) with MySQL
- Circe (Serialisation)
- Redis4Cats (Redis Client)
- Log4Cats (Logging)

### Infrastructure and Deployment
- Docker Compose (Dockerised Flyway and MySQL)
- TODO: AWS hosting (ECS vs EC2)

### OAuth
Strava used OAuth for authentication. The docs are very good: https://developers.strava.com/docs/authentication/.
The basic premise is that the application is registered with strava and this provides us with a secret. We can then 
call the Strava authentication endpoint, passing in this secret which provides with an authentication bearer token.
We can then use this token in subsequent calls.

We can test this with curl:
```bash
curl -X POST https://www.strava.com/api/v3/oauth/token \
  -d client_id=ReplaceWithClientID \
  -d client_secret=ReplaceWithClientSecret \
  -d grant_type=authorization_code
```

