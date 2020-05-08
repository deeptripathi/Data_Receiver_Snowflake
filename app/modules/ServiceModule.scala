package modules

import akka.routing.RoundRobinPool
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

import actors._

class ServiceModule extends AbstractModule with AkkaGuiceSupport {
  final val MAIN_ACTOR_ROUTER_POOL_SIZE = 1

  final val SQL_ACTOR_ROUTER_POOL_SIZE = 30

  final val DATA_REPOSITORY_ACTOR_ROUTER_POOL_SIZE = 15

  final val SQS_ACTOR_ROUTER_POOL_SIZE = 10

  override def configure(): Unit = {

    bindActor[MainActor]("main-actor",
      RoundRobinPool(MAIN_ACTOR_ROUTER_POOL_SIZE).props)
  }
}
