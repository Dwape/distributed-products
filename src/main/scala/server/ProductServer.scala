package server

import io.grpc.{ManagedChannelBuilder, ServerBuilder}
import product.product.{ProductRequest, ProductServiceGrpc}
import service.ProductService

import scala.concurrent.ExecutionContext

object ProductServer extends App{

  implicit val ec = ExecutionContext.global

  val server = ServerBuilder.forPort(50000)
    .addService(ProductServiceGrpc.bindService(new ProductService(), ExecutionContext.global))
    .build()

  server.start()
  println("Running...")

  server.awaitTermination()
}

object ClientDemo extends App {

  implicit val ec = ExecutionContext.global

  val channel = ManagedChannelBuilder.forAddress("localhost", 50000)
    .usePlaintext(true)
    .build()

  val stub = ProductServiceGrpc.stub(channel)

  val result = stub.getProduct(ProductRequest(1))

  result.onComplete { r =>
    println(r.get.name)
    println("Completed")
  }

  System.in.read()
}
