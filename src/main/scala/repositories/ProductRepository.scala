package repositories

import database.{Db, ProductTable}
import models.Product
import slick.basic.DatabaseConfig

import slick.jdbc.H2Profile

import scala.concurrent.Future


class ProductRepository(val config: DatabaseConfig[H2Profile])
extends Db with ProductTable {

  import config.profile.api._
  /**
    * The starting point for all queries on the people table.
    */
  private val product = TableQuery[Products]

  db.run(DBIO.seq(product.schema.create))

  /**
    * Create a product with the given name and description.
    *
    * This is an asynchronous operation, it will return a future of the created product, which can be used to obtain the
    * id for that product.
    */
  def create(name: String, description: String, category: String): Future[Product] = db.run(
    // We create a projection of just the name and description columns, since we're not inserting a value for the id column
    (product.map(p => (p.name, p.description, p.category))
      // Now define it to return the id, because we want to know what id was generated for the product
      returning product.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((data, id) => Product(id, data._1, data._2, data._3))
      // And finally, insert the product into the database
      ) += (name, description, category))

  /**
    * List all the products in the database.
    */
  def list(): Future[Seq[Product]] = db.run (
    product.result
  )

  def getById(id: Long): Future[Option[Product]] = db.run (
    product.filter{_.id === id}.result.headOption
  )

  //TODO test option
  def getByCategory(category: String): Future[Seq[Product]] = db.run (
    product.filter{_.category === category}.result
  )

}

