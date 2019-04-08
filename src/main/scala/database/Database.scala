package database

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DbConfiguration {
  lazy val config = DatabaseConfig.forConfig[JdbcProfile]("h2mem1")
}

trait Db {
  val config: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = config.db
}
