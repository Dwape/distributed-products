package database

import models.Product

trait ProductTable {
  this: Db =>

  import config.profile.api._

  class Products(tag: Tag) extends Table[Product](tag, "product") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    def description = column[String]("description")

    def category = column[String]("category")


    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Product object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Person case classes
      * apply and unapply methods.
      */
    def * = (id, name, description, category) <> ((Product.apply _).tupled, Product.unapply)
  }

}
