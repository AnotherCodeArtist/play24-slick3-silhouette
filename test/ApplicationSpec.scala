import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test._

import models.User
import net.codingwell.scalaguice.ScalaModule
import org.scalatestplus.play.{PlaySpec, OneAppPerTest}
import play.api.inject.guice.GuiceApplicationBuilder

import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import org.scalatest.concurrent.ScalaFutures
import com.mohiva.play.silhouette.api.Environment
import play.api.libs.concurrent.Execution.Implicits._
import helpers.SecurityTestContext

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class ApplicationSpec extends PlaySpec {
  /*
  override def newAppForTest(testData : org.scalatest.TestData) =
    FakeApplication(additionalConfiguration = inMemoryDatabase("default", Map("MODE" -> "PostgreSQL")))
  */
  "Application" must {

    "send 404 on a bad request" in new WithApplication() {
      val result = route(FakeRequest(GET, "/boum")).get
      status(result) must be(NOT_FOUND)
    }

    "render the index page" in new WithApplication() {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must be(OK)
      contentType(home).value must be("text/html")
      //contentAsString(home) must include("Your new application is ready.")
    }


    "send some Json if user is authenticated" in new SecurityTestContext {
      new WithApplication(application) {
        val env = environment
        val hello = route(FakeRequest(GET, "/hello")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(hello) must be(OK)
        contentType(hello) must be(Some("application/json"))
        (contentAsJson(hello) \ "msg").get must be(JsString("Hello"))
      }
    }

  }



}
