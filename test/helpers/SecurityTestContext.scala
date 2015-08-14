package helpers

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test.FakeEnvironment
import models.User
import net.codingwell.scalaguice.ScalaModule
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global

trait SecurityTestContext {

  val identity = User(Some(1), "John", "Doe", "jd@test.com", "facebook", "jd@test.com")
  implicit lazy val environment = FakeEnvironment[User, JWTAuthenticator](Seq(identity.loginInfo -> identity))

  val memDB = Map(
    "slick.dbs.default.driver"->"slick.driver.H2Driver$",
    "slick.dbs.default.db.driver"->"org.h2.Driver",
    "slick.dbs.default.db.url"->"jdbc:h2:mem:play;MODE=PostgreSQL"
  )

  class FakeModule extends AbstractModule with ScalaModule {
    def configure() = {
      bind[Environment[User, JWTAuthenticator]].toInstance(environment)
    }
  }

  lazy val application = new GuiceApplicationBuilder()
    .overrides(new FakeModule)
    .configure(memDB)
    .build()

}