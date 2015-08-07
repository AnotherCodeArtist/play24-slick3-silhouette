import models.User

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeApplication
import play.api.test.WithApplication
import repositories.{UserRepository}
import org.scalatest._
import play.api.test._
import play.api.test.Helpers._
import org.scalatestplus.play._

import scala.concurrent.Future

/**
 * Created by salho on 05.08.15.
 */
class UserRepositorySpec extends PlaySpec with ScalaFutures  {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  import scala.concurrent.ExecutionContext.Implicits.global

  def app = new GuiceApplicationBuilder()
    .configure(inMemoryDatabase("default", Map("MODE" -> "PostgreSQL"))).build()

  "UserRepository" must {
    "store users" in new WithApplication(app) {
      val userRepo = app.injector.instanceOf[UserRepository]
      val insertedUser = userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
      insertedUser.id must not be (None)
      val user = userRepo.findByEmail("jd@test.com").futureValue.get
      user.email must be("jd@test.com")
      user.roles must contain only("USER")
    }
    "retrieve users" in new WithApplication(app) {
      val userRepo = app.injector.instanceOf[UserRepository]
      val count = userRepo.count.futureValue
      count must be(0)
      userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
      val count2 = userRepo.count.futureValue
      count2 must be(1)
    }
    "update roles" in new WithApplication(app) {
      val userRepo = app.injector.instanceOf[UserRepository]
      val count = userRepo.count.futureValue
      count must be(0)
      userRepo.save(User(None, "John", "Doe", "jd@test.com", "test", "test")).futureValue
      val user = userRepo.findByEmail("jd@test.com").futureValue.get
      user.roles must contain only("USER")
      userRepo.save(user.addRole("ADMINISTRATOR")).futureValue
      val user2 = userRepo.findByEmail("jd@test.com").futureValue.get
      user2.roles must contain only("USER","ADMINISTRATOR")
    }
    "list users" in new WithApplication(app) {
      val userRepo = app.injector.instanceOf[UserRepository]
      createTestUsers(10,userRepo).futureValue
      val count = userRepo.count.futureValue
      count must be(10)
      val allUsers = userRepo.all.futureValue
      allUsers.length must be(10)
    }
  }

  def createTestUsers(number: Int,userRepo: UserRepository) =
    Future.sequence((1 to number)
      .map(i => User(None,s"First$i",s"Last$i",s"user$i@test.com","myprovider",s"user$i@test.com"))
      .map(userRepo.save(_)))

}
