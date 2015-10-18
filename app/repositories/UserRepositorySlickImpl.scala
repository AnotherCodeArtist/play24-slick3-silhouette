package repositories

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import models.{UserPreview, User}
import models.slick._
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
class UserRepositorySlickImpl @Inject()(authInfoRepository: AuthInfoRepository) extends UserRepository with HasDatabaseConfig[JdbcProfile] {

  //import driver.api._
  //import slick.driver.MySQLDriver.api._
  //import scala.concurrent.ExecutionContext.Implicits.global

  private val users = TableQuery[Users]
  private val dbRoles = TableQuery[Roles]
  private val allQuery = users.sortBy(u => (u.lastname.asc, u.firstname.asc)).map(_.preview)
  private val pwInfos = TableQuery[PasswordInfos]

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  private def findBy(criterion: (Users => slick.lifted.Rep[Boolean])) = db.run(
    (for {
      user <- users.filter(criterion).result.head
      dbRoles <- dbRoles.filter(_.userID === user.id).map(_.role).result
    } yield Some(user.copy(roles = dbRoles.toSet)))
  ).recover { case e => None }

  def find(id: Int) = findBy(_.id === id)


  def findByEmail(email: String) = findBy(_.email === email)


  private def updateRolesQuery(userId: Int, roles: Set[String]) = for {
    d <- dbRoles.filter(_.userID === userId).delete
    a <- dbRoles ++= roles.map(DBRole(None, userId, _))
  } yield roles

  def save(user: User): Future[User] = {
    val existingUserFuture = user.id match {
      case None => Future.successful(None)
      case Some(id) => find(id)
    }
    existingUserFuture.flatMap {
      case None => db.run(
        for {
          u <- (users returning users.map(_.id)
            into ((user, id) => user.copy(id = Some(id)))) += user
          r <- updateRolesQuery(u.id.get, user.roles)
        } yield u
      )
      case Some(_) => db.run(
        for {
          updatedUser <- users.filter(_.id === user.id).update(user)
          upatedRoles <- updateRolesQuery(user.id.get, user.roles)
        } yield user
      )
    }
  }

  def count = db.run(users.length.result)

  override def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]] =
    findBy(u => u.providerID === loginInfo.providerID && u.providerKey === loginInfo.providerKey)

  override def all: Future[Seq[UserPreview]] =
    db.run(allQuery.result)

  override def all(page: Int, pageSize: Int): Future[Seq[UserPreview]] =
    db.run(allQuery.drop(page * pageSize).take(pageSize).result)

  override def delete(id: Int): Future[Unit] = {
    val delQuery = for {
      roleDelete <- dbRoles.filter(_.userID === id).delete
      pwInfoDelete <- pwInfos.filter(_.userID === id).delete
      userDelete <- users.filter(_.id === id).delete
    } yield (roleDelete, pwInfoDelete, userDelete)
    db.run(delQuery).map(_ => {})
  }

}
