package service

import product.product.{NewProductRequest, ProductReply, ProductRequest, ProductServiceGrpc}
import repositories.ProductRepository

import scala.concurrent.{ExecutionContext, Future}

class ProductService (repo: ProductRepository) (implicit ec: ExecutionContext) extends ProductServiceGrpc.ProductService  {
  override def getProduct(request: ProductRequest): Future[ProductReply] = {
    repo.getById(request.id).map {
      case Some(p) => ProductReply(p.id, p.name, p.description)
      case None => throw ProductNotFoundException
    }
  }

  override def newProduct(request: NewProductRequest): Future[ProductReply] = {
    repo.create(request.name, request.description).map(p => ProductReply(p.id, p.name, p.description))
  }
}

case object ProductNotFoundException extends RuntimeException
