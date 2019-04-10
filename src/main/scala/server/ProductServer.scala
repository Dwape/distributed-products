package server

import io.grpc.{ManagedChannelBuilder, ServerBuilder}
import product.product.{NewProductRequest, ProductRequest, ProductServiceGrpc}
import repositories.ProductRepository
import service.ProductService
import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object ProductServer extends App{

  implicit val ec = ExecutionContext.global

  val config = DatabaseConfig.forConfig[H2Profile]("db")
  val repo = new ProductRepository(config)

  repo.create("coca", "fria")

  val server = ServerBuilder.forPort(50000)
    .addService(ProductServiceGrpc.bindService(new ProductService(repo), ExecutionContext.global))
    .build()

  server.start()
  println("Running...")

  server.awaitTermination()
}

object ClientDemo extends App {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val channel = ManagedChannelBuilder.forAddress("localhost", 50000)
    .usePlaintext(true)
    .build()

  val stub = ProductServiceGrpc.stub(channel)

  val result = stub.newProduct(NewProductRequest("alfajor", "triple"))

  result.onComplete { r =>
    stub.getProduct(ProductRequest(r.get.id)).onComplete(r2 => {
      println(r2.get.name)
      println("completed")
    })
  }

  System.in.read()
}
