package database

import models.Product

trait ProductTable {
  this: Db =>

  import config.profile.api._
  // We want the JdbcProfile for this provider
  //private val db = Database.forConfig("h2mem1")

  //val ec = ExecutionContext.global

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  //import dbConfig._
  //import profile.api._

  class Products(tag: Tag) extends Table[Product](tag, "product") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    def description = column[String]("description")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Product object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Person case classes
      * apply and unapply methods.
      */
    def * = (id, name, description) <> ((Product.apply _).tupled, Product.unapply)
  }
}
