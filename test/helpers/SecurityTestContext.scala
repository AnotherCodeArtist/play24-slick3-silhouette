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
  implicit val environment = FakeEnvironment[User, JWTAuthenticator](Seq(identity.loginInfo -> identity))

  class FakeModule extends AbstractModule with ScalaModule {
    def configure() = {
      bind[Environment[User, JWTAuthenticator]].toInstance(environment)
    }
  }

  lazy val application = new GuiceApplicationBuilder()
    .overrides(new FakeModule)
    .configure(inMemoryDatabase("default", Map("MODE" -> "PostgreSQL")))
    .build()

}