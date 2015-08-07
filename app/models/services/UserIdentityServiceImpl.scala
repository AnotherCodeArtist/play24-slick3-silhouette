package models.services

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import repositories.UserRepository

import scala.concurrent.Future

/**
 * Created by salho on 06.08.15.
 */
class UserIdentityServiceImpl @Inject() (userRepository: UserRepository) extends UserIdentityService {

  override def save(user: User): Future[User] = userRepository.save(user)

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userRepository.findByLoginInfo(loginInfo)
}