package repositories

import com.mohiva.play.silhouette.api.LoginInfo
import models.{UserPreview, User}
import models.slick.{DBRole, Roles, Users}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile

import scala.concurrent.Future

//import slick.lifted._

/**
 * Created by salho on 05.08.15.
 */
class UserRepositorySlickImpl extends UserRepository with HasDatabaseConfig[JdbcProfile] {

  //import driver.api._
  //import slick.driver.MySQLDriver.api._
  //import scala.concurrent.ExecutionContext.Implicits.global

  private val users = TableQuery[Users]
  private val dbRoles = TableQuery[Roles]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private def findBy(criterion: (Users => slick.lifted.Rep[Boolean])) = (for {
    user <- db.run(users.filter(criterion).result.head)
    dbRoles <- db.run(dbRoles.filter(_.userID === user.id).map(_.role).result)
  } yield Some(user.copy(roles = dbRoles.toSet))).recover { case e => None }

  def find(id: Int) = findBy(_.id === id)

  def addRoles(user: User) = db.run(dbRoles.filter(_.userID === user.id).map(_.role).result).flatMap {
    roleSet => Future.successful(Some(user.copy(roles = roleSet.toSet)))
  }

  def findByEmail(email: String) = findBy(_.email === email)

  private def updateRoles(user: User, roles: Set[String]): Future[User] =
    db.run(dbRoles.filter(_.userID === user.id).delete)
      .flatMap { _ => db.run(dbRoles ++= roles.map(DBRole(None, user.id.get, _)))
      .map { _ => user
    }
    }


  def save(user: User): Future[User] = {
    val existingUserFuture = user.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }
    existingUserFuture.flatMap {
      case None => db.run((users returning users.map(_.id)
        into ((user, id) => user.copy(id = Some(id)))
        ) += user).flatMap(updateRoles(_, user.roles))
      case Some(_) => for {
        updatedUser <- db.run(users.filter(_.id === user.id).update(user))
        upatedRoles <- updateRoles(user, user.roles)
      } yield user
    }
  }

  def count = db.run(users.length.result)

  override def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]] =
    findBy(u => u.providerID === loginInfo.providerID && u.providerKey === loginInfo.providerKey)

  override def all: Future[Seq[UserPreview]] =
    db.run(users.map(_.preview).result)
}
