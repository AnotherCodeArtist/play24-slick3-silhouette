package utils

import javax.inject.Inject

import com.google.inject.Provider
import play.api.libs.json.Json
import play.api.mvc.{Result, RequestHeader}
import play.api.routing.Router
import play.api.{OptionalSourceMapper, Configuration, Environment}
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.Results
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * Created by salho on 12.08.15.
 */
class ErrorHandler @Inject() (
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router]
                              ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router){
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future { Results.Status(statusCode)(Json.obj("message"->message))}
}
