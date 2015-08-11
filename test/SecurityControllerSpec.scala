import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.test._
import models.slick.PasswordInfos

import models.{SignInInfo, SignUpInfo, User}
import net.codingwell.scalaguice.ScalaModule
import org.scalatestplus.play.{PlaySpec, OneAppPerTest}
import play.api.Configuration
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder

import play.api.libs.json._
import play.api.mvc.Cookie
import play.api.test.Helpers._
import org.scalatest._
import play.api.test._
import org.scalatest.concurrent.ScalaFutures
import com.mohiva.play.silhouette.api.Environment
import play.api.libs.concurrent.Execution.Implicits._
import helpers.SecurityTestContext
import play.api.test.WithApplication
import play.filters.csrf.CSRF
import repositories.UserRepository
import org.scalatestplus.play._
import org.scalatest.concurrent.ScalaFutures
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._

/**
 * Created by salho on 07.08.15.
 */


class SecurityControllerSpec extends PlaySpec with ScalaFutures {

  import models.slick.DBPasswordInfo.dbTableElement2PasswordInfo

  "SecurityController" must {
    "return an error on signup if data is incomplete" in new SecurityTestContext {
      new WithApplication(application) {
          val token = CSRF.SignedTokenProvider.generateToken
          val signUpResponse = route(FakeRequest(POST, "/signup")
            .withHeaders("Csrf-Token" -> token)
            .withSession("csrfToken"->token)
            .withJsonBody(Json.obj("message"->"Hi"))).get
          status(signUpResponse) must be(BAD_REQUEST)
      }
    }
    "register a user when signup information is OK" in new SecurityTestContext {
      new WithApplication(application) {
        val token = CSRF.SignedTokenProvider.generateToken
        val testUser = SignUpInfo("John","Doe","jd@test.com","topsecret")
        val signUpResponse = route(FakeRequest(POST, "/signup").withJsonBody(Json.toJson(testUser))
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token))
          .get
        status(signUpResponse) mustBe OK
        contentType(signUpResponse).value mustBe "application/json"
        (contentAsJson(signUpResponse) \ "token") mustNot be(JsUndefined)
        val userRepo = application.injector.instanceOf[UserRepository]
        val Some(user) = userRepo.findByEmail(testUser.email).futureValue
        user.firstname mustBe "John"
        user.loginInfo.providerID mustBe "credentials"
        user.loginInfo.providerKey mustBe testUser.email
        val pwInfos = TableQuery[PasswordInfos]
        val dbConfigProvider = application.injector.instanceOf[DatabaseConfigProvider]
        val db = dbConfigProvider.get[JdbcProfile].db
        val pwInfo = db.run(pwInfos.result).futureValue
        pwInfo.size mustBe 1
        val userPwInfo = pwInfo.head
        userPwInfo.userID mustBe user.id.get
        val hasher = application.injector.instanceOf[PasswordHasher]
        hasher.matches(userPwInfo,"topsecret") mustBe true
      }
    }
    "report an error if a user tries two register more than once"  in new SecurityTestContext {
      new WithApplication(application) {
        val token = CSRF.SignedTokenProvider.generateToken
        val testUser = SignUpInfo("John","Doe","jd@test.com","topsecret")
        val signUpResponse = route(FakeRequest(POST, "/signup").withJsonBody(Json.toJson(testUser)).withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)).get
        status(signUpResponse) mustBe OK
        contentType(signUpResponse).value mustBe "application/json"
        (contentAsJson(signUpResponse) \ "token") mustNot be(JsUndefined)
        val secondUpResponse = route(FakeRequest(POST, "/signup").withJsonBody(Json.toJson(testUser)).withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)).get
        status(secondUpResponse) mustBe BAD_REQUEST
      }
    }
    "send identity if user is authenticated" in new SecurityTestContext {
      new WithApplication(application) {
        val env = environment
        val Some(result) = route(FakeRequest(GET, "/whoami")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo))
        status(result) must be(OK)
        contentType(result) must be(Some("application/json"))
        val user =  (contentAsJson(result)).as[User]
        user must be(identity)
      }
    }
    "refuse to send some Json if user is not authenticated" in new WithApplication() {
      val hello = route(FakeRequest(GET, "/hello")).get
      status(hello) must be(UNAUTHORIZED)
    }
    "support sign-in"  in new SecurityTestContext {
      new WithApplication(application) {
        val token = CSRF.SignedTokenProvider.generateToken
        val testUser = SignUpInfo("John","Doe","jd@test.com","topsecret")
        val signUpResponse = route(FakeRequest(POST, "/signup").withJsonBody(Json.toJson(testUser)).withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)).get
        status(signUpResponse) mustBe OK
        val signInResponse = route(FakeRequest(POST, "/signin").withJsonBody(Json.toJson(SignInInfo(testUser.email,testUser.password,false))).withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)).get
        contentType(signInResponse).value mustBe "application/json"
        (contentAsJson(signInResponse) \ "token") mustNot be(JsUndefined)
      }
    }
    "return an error if signup information does not contain a valid email" in new SecurityTestContext {
      new WithApplication(application) {
        val testUser = SignUpInfo("John","Doe","test","topsecret")
        val token = CSRF.SignedTokenProvider.generateToken
        val signUpResponse = route(FakeRequest(POST, "/signup")
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
          .withJsonBody(Json.toJson(testUser))).get
        status(signUpResponse) must be(BAD_REQUEST)
      }
    }
  }

}
