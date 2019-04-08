package repositories

import database.{Db, DbConfiguration, ProductTable}
import models.Product
import slick.basic.DatabaseConfig

import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


//class Products(tag: Tag) extends Table[Product](tag, "PRODUCTS") {
//  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//  def name = column[String]("name")
//
//  def description = column[String]("description")
//
//  def * = (id, name, description) <> ((Product.apply _).tupled, Product.unapply)
//}

class ProductRepository() //val config: DatabaseConfig[JdbcProfile]
extends Db with ProductTable with DbConfiguration{

  import config.profile.api._
  import scala.concurrent.ExecutionContext.Implicits.global
  // We want the JdbcProfile for this provider
  //private val db = Database.forConfig("h2mem1")

  //val ec = ExecutionContext.global

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  //import dbConfig._
  //import profile.api._

  /**
    * The starting point for all queries on the people table.
    */
  private val product = TableQuery[Products]

  /**
    * Create a product with the given name and description.
    *
    * This is an asynchronous operation, it will return a future of the created product, which can be used to obtain the
    * id for that product.
    */
  def create(name: String, description: String): Future[Product] = db.run {
    // We create a projection of just the name and description columns, since we're not inserting a value for the id column
    (product.map(p => (p.name, p.description))
      // Now define it to return the id, because we want to know what id was generated for the product
      returning product.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((nameDesc, id) => Product(id, nameDesc._1, nameDesc._2))
      // And finally, insert the product into the database
      ) += (name, description)
  }

  /**
    * List all the products in the database.
    */
  def list(): Future[Seq[Product]] = db.run {
    product.result
  }

  def getById(id: Long): Future[Option[Product]] = db.run {
    product.filter{_.id === id}.result.headOption
  }

}

