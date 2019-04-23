package service

import product.product._
import repositories.ProductRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ProductService (repo: ProductRepository) (implicit ec: ExecutionContext) extends ProductServiceGrpc.ProductService  {
  override def getProduct(request: ProductRequest): Future[ProductReply] = {
    println("Product requested")
    repo.getById(request.id).map {
      case Some(p) => ProductReply(p.id, p.name, p.description)
      case None => println("product not found"); throw ProductNotFoundException
    }
  }

  override def newProduct(request: NewProductRequest): Future[ProductReply] = {
    repo.create(request.name, request.description, request.category).map(p => ProductReply(p.id, p.name, p.description, p.category))
  }

  override def getProductsByCategory(request: GetProductsByCategoryRequest): Future[GetProductsByCategoryReply] = {
    repo.getByCategory(request.category).map(Option(_)) map {
      case Some(p) => GetProductsByCategoryReply(p.map(r => ProductReply(r.id,r.name, r.description, r.category)))
      case None => println("product not found"); throw ProductNotFoundException
    }
  }

  //implementar isActive (solo recibe el request y devuelve un reply con un string)
  override def isActive(request: PingRequest): Future[PingReply] = {
    Future.successful(PingReply("active"))
  }
}

case object ProductNotFoundException extends RuntimeException
