package service

import product.product.{ProductReply, ProductRequest, ProductServiceGrpc}
import repositories.ProductRepository

import scala.concurrent.{ExecutionContext, Future}

class ProductService (implicit ec: ExecutionContext) extends ProductServiceGrpc.ProductService  {
  override def getProduct(request: ProductRequest): Future[ProductReply] = {

    //val reply = ProductReply(1, "Toast", "Tasty")
    //Future.successful(reply)

    val repo = new ProductRepository() //change, this is wrong!

    repo.getById(request.id).map {
      case Some(p) => ProductReply(p.id, p.name, p.description)
      case None => throw ProductNotFoundException
    }
  }
}

case object ProductNotFoundException extends RuntimeException
