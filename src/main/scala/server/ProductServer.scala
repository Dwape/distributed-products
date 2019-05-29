package server

import java.net.URI

import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import io.grpc.{ManagedChannel, ManagedChannelBuilder, Server, ServerBuilder}
import product.product.{NewProductRequest, ProductRequest, ProductServiceGrpc}
import repositories.ProductRepository
import service.ProductService
import slick.basic.DatabaseConfig
import slick.jdbc.MySQLProfile

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object ProductServer extends App {

  // root is the default value for user and password
  val user: String = args.lift(0).getOrElse("root")
  val password: String = args.lift(1).getOrElse("root")

  implicit val ec = ExecutionContext.global

  /*val serviceManager = new ServiceManager
  serviceManager.startConnection("0.0.0.0", 50000, "product")*/

  val config: Config = ConfigFactory.load("db")
  val url: String = s"jdbc:mysql://localhost:3306/test?user=$user&password=$password"
  val newConfig = config.withValue("db.db.url", ConfigValueFactory.fromAnyRef(url))

  val databaseConfig = DatabaseConfig.forConfig[MySQLProfile]("db", newConfig)

  val repo = new ProductRepository(databaseConfig)

  val server: Server = ServerBuilder.forPort(50001)
    .addService(ProductServiceGrpc.bindService(new ProductService(repo), ExecutionContext.global))
    .build()

  server.start()
  println("Running...")
  server.awaitTermination()
}

/*object ProductServer2 extends App{

  implicit val ec = ExecutionContext.global

  val stubManager = new ServiceManager
  stubManager.startConnection("0.0.0.0", 50002, "product")

  val config = DatabaseConfig.forConfig[MySQLProfile]("db")
  val repo = new ProductRepository(config)

  val result = repo.create("coca", "fria", "Bebida")
  val server: Server = ServerBuilder.forPort(50002)
    .addService(ProductServiceGrpc.bindService(new ProductService(repo), ExecutionContext.global))
    .build()

  server.start()
  println("Running...")
  server.awaitTermination()
}*/

/*object ClientDemo extends App {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val channel = ManagedChannelBuilder.forAddress("localhost", 50000)
    .usePlaintext(true)
    .build()

  val stub = ProductServiceGrpc.stub(channel)

  val result = stub.newProduct(NewProductRequest("alfajor", "triple", "Comestibles"))

  result.onComplete { r =>
    stub.getProduct(ProductRequest(r.get.id)).onComplete(r2 => {
      println(r2.get.name)
      println("completed")
    })
  }

  System.in.read()
}*/
