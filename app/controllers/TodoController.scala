package controllers

import javax.inject._
import models.{Todo, TodoForm}
import play.api.libs.json._
import play.api.mvc._
import services.TodoService
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TodoController @Inject()(cc: ControllerComponents, todosService: TodoService) extends AbstractController(cc) {
  implicit val todoFormat = new Writes[Todo] {
    override def writes(o: Todo): JsValue = {
      Json.obj(
        "id" -> o.id,
        "title" -> o.title,
        "is_completed" -> o.isCompleted
      )
    }
  }

  def index(): Action[AnyContent] = Action.async { implicit request =>
        todosService.listAllTodos map {
         todos =>  Ok(Json.toJson(todos))
        }
  }

  def add() : Action[AnyContent] = Action.async { implicit request =>
    TodoForm.form.bindFromRequest.fold(
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error"))
      },
      data => {
        val newTodoItem = Todo(0, data.title, data.isCompleted)
        todosService.addTodo(newTodoItem).map {
          msg => Created(msg)
        }
      }
    )
  }

  def getById(id: String) : Action[AnyContent] = Action.async { implicit request =>
    try {
       val idInt = id.toInt
      todosService.getTodo(idInt).map {
        data =>  if (data.isEmpty) {
          NotFound(s"todo not found for id $id")
        } else {
          Ok(Json.toJson(data))
        }
      }
    }catch {
      case e: NumberFormatException =>
          Future.successful(BadRequest(s"bad id param for $id"))
      case e: Exception => Future.successful(BadRequest("Unknown error occured"))
    }
  }

  def updateById(id: String): Action[AnyContent] = Action.async { implicit request =>
    try {
      val idInt = id.toInt
      TodoForm.form.bindFromRequest.fold(
        errorForm => {
          errorForm.errors.foreach(println)
          Future.successful(BadRequest("errors"))
        },
        data => {
          todosService.updateTodo(Todo(idInt, data.title, data.isCompleted)).map {
             res => if (res == 0) {
               NotFound(s"todo not found with $id")
             } else {
               Ok("Todo updated successfully")
             }
          }
        }
      )
    }catch {
      case e: NumberFormatException => Future.successful(BadRequest(s"bad id param $id"))
      case e: Exception => Future.successful(BadRequest("Unknown error occured"))
    }
  }

  def complete(id: String) :Action[AnyContent] = Action.async  { implicit request =>
    try {
      val idInt = id.toInt
      todosService.completeTodo(idInt).map {
        res => {
          if (res == 0) {
            NotFound(s"todo not found for id: $id")
          } else {
            Ok("Todo updated to completed")
          }
        }
      }
    }catch {
      case e: NumberFormatException => Future.successful(BadRequest(s"bad id param $id"))
      case e: Exception => Future.successful(BadRequest("Unknown error occured"))
    }
  }

  def delete(id: String) : Action[AnyContent] = Action.async { implicit  request =>
    try {
      val idInt = id.toInt
      todosService.deleteTodo(idInt).map {
        res => {
          if (res == 0) {
            NotFound(s"todo not found for id: $id")
          } else {
            NoContent
          }
        }
      }
    }catch {
      case e: NumberFormatException => Future.successful(BadRequest(s"bad id param $id"))
      case e: Exception => Future.successful(BadRequest("Unknown error occured"))
    }
  }
}
