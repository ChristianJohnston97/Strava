package strava.server.config

final case class ServerConfig(host: String, port: Int)
final case class StoreConfig(db: DatabaseConfig, server: ServerConfig)
