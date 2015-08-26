import helpers.SecurityTestContext
import models.User


import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeApplication
import play.api.test.WithApplication

//import slick.driver.H2Driver.api._

import repositories.{UserRepository}
import org.scalatest._
import play.api.test._

import play.api.test.Helpers._
import org.scalatestplus.play._

import scala.concurrent.Future

/**
 * Created by salho on 05.08.15.
 */
class UserRepositorySpec extends PlaySpec with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  import scala.concurrent.ExecutionContext.Implicits.global


  "UserRepository" must {
    "store users" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        insertedUser.id must not be (None)
        val user = userRepo.findByEmail("jd@test.com").futureValue.get
        user.email must be("jd@test.com")
        user.roles must contain only ("USER")
      }
    }
    "retrieve users" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val count = userRepo.count.futureValue
        count must be(0)
        userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val count2 = userRepo.count.futureValue
        count2 must be(1)
      }
    }
    "return None if user isn't found" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        userRepo.findByEmail("dummy@test.com").futureValue mustBe None
      }
    }
    "update roles" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        val count = userRepo.count.futureValue
        count must be(0)
        userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
        val user = userRepo.findByEmail("jd@test.com").futureValue.get
        user.roles must contain only ("USER")
        userRepo.save(user.addRole("ADMINISTRATOR")).futureValue
        val user2 = userRepo.findByEmail("jd@test.com").futureValue.get
        user2.roles must contain only("USER", "ADMINISTRATOR")
      }
    }
    "list all users" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        createTestUsers(10, userRepo).futureValue
        val count = userRepo.count.futureValue
        count must be(10)
        val allUsers = userRepo.all.futureValue
        allUsers.length must be(10)
      }
    }
    "delete a user" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        createTestUsers(5, userRepo).futureValue
        val user2 = userRepo.findByEmail("user2@test.com").futureValue.get
        userRepo.delete(user2.id.get).futureValue
        val count = userRepo.count.futureValue
        count must be(4)
        userRepo.findByEmail("user2@test.com").futureValue mustBe None
      }
    }
    "provide pagination" in new SecurityTestContext {
      new WithApplication(application) {
        val userRepo = app.injector.instanceOf[UserRepository]
        createTestUsers(7, userRepo).futureValue
        val page1 = userRepo.all(0,5).futureValue
        page1.length mustBe 5
        page1(4).lastname mustBe "Last5"
        val page2 = userRepo.all(1,5).futureValue
        page2.length mustBe 2
        page2(0).lastname mustBe "Last6"
      }
    }
  }

  def createTestUsers(number: Int, userRepo: UserRepository) =
    Future.sequence((1 to number)
      .map(i => User(None, s"First$i", s"Last$i", s"user$i@test.com", "credentials", s"user$i@test.com"))
      .map(userRepo.save(_)))

}
