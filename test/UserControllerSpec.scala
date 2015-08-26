import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import helpers.SecurityTestContext
import models.{UserPreview, User}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import repositories.UserRepository
import com.mohiva.play.silhouette.test._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * Created by salho on 12.08.15.
 */
class UserControllerSpec extends PlaySpec with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(1, Seconds))

  "SecurityController" must {
    "must not allow ordinary users to retrieve a lis of users" in new SecurityTestContext {
      new WithApplication(application) {
        val listRespone = route(FakeRequest(GET, "/user")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(listRespone) must be(FORBIDDEN)
      }
    }
    "must not allow ordinary users to delete a user" in new SecurityTestContext {
      new WithApplication(application) {
        val listRespone = route(FakeRequest(DELETE, "/user/1")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(listRespone) must be(FORBIDDEN)
      }
    }
    "must provide a list of users if the user is an admin" in new SecurityTestContext {
      override val identity = User(Some(1), "The", "Admin", "admin@test.com", "credentials", "admin@test.com",Set("USER","ADMINISTRATOR"))
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        createTestUsers(7,userRepo).futureValue
        val listRespone = route(FakeRequest(GET, "/user")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        status(listRespone) must be(OK)
        contentType(listRespone) mustBe Some("application/json")
        val json = contentAsJson(listRespone)
        val users = Json.fromJson[List[UserPreview]](json).get
        users.length mustBe 7
      }
    }
    "must delete a user if the current user is an admin" in new SecurityTestContext {
      override val identity = User(Some(1), "The", "Admin", "admin@test.com", "credentials", "admin@test.com",Set("USER","ADMINISTRATOR"))
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        createTestUsers(7,userRepo).futureValue
        val user2 = userRepo.findByEmail("user2@test.com").futureValue.get
        val token = CSRF.SignedTokenProvider.generateToken
        val deleteUrl = s"/user/${user2.id.get}"
        val deleteResponse = route(FakeRequest(DELETE, deleteUrl)
          .withHeaders("Csrf-Token" -> token)
          .withSession("csrfToken"->token)
          //.withHeaders("Content-Type" -> "application/json")
          .withAuthenticator[JWTAuthenticator](identity.loginInfo)).get
        println(deleteResponse)
        status(deleteResponse) must be(OK)
        contentType(deleteResponse) mustBe Some("application/json")
      }
    }
  }

  def createTestUsers(number: Int, userRepo: UserRepository) =
    Future.sequence((1 to number)
      .map(i => User(None, s"First$i", s"Last$i", s"user$i@test.com", "myprovider", s"user$i@test.com"))
      .map(userRepo.save(_)))
}
