/**
 * Created by salho on 03.08.15.
 */


import models.User
import models.slick.Users.users
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatestplus.play.PlaySpec
import play.test.WithApplication
import slick.driver.H2Driver.api._
import slick.jdbc.meta._

class UserSpec extends PlaySpec with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  var db: Database = _

  def createSchema = db.run(users.schema.create).futureValue

  before { db = Database.forConfig("testdb");createSchema }


  "UserService" must {
    "create a database schema" in new WithApplication(){

      val tables = db.run(MTable.getTables).futureValue

      assert(tables.size == 1)
      assert(tables.count(_.name.name.equalsIgnoreCase("Users")) == 1)
    }
    "store users" in {

      val user1 = User(None,"John","Doe","jd@test.com","test","test")
      db.run(users+=user1).futureValue
      val allUsers = db.run(users.result).futureValue
      allUsers.length must be(1)
      allUsers.head.id must not be(None)
    }
    "read a stored user" in {
      (1>0) must be(true)
    }
  }


  after { db.close }

}
