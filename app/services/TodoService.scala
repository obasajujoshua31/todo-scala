package services
import com.google.inject.Inject
import models.{Todo, TodoTable}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}


class TodoService @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) (implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  var todoList = TableQuery[TodoTable]

   def addTodo(todo: Todo): Future[String] = {
     dbConfig.db.run(todoList += todo).
       map(res => "Todo Item successfully added").
       recover {
       case ex: Exception => {
         printf(ex.getMessage)
         ex.getMessage
       }
     }
   }

  def deleteTodo(id: Int): Future[Int] = {
    dbConfig.db.run(todoList.filter(_.id === id).delete)
  }


  def getTodo(id: Int): Future[Option[Todo]] = {
     dbConfig.db.run(todoList.filter(_.id === id).result.headOption)
  }

  def listAllTodos: Future[Seq[Todo]]= {
    dbConfig.db.run(todoList.result)
  }

  def updateTodo(todo: Todo): Future[Int] = {
      dbConfig.db.run(
        todoList.filter(_.id === todo.id)
          .map(x => (x.title, x.isCompleted))
          .update(todo.title, todo.isCompleted)
      )
  }

  def completeTodo(id: Int): Future[Int] = {
    dbConfig.db.run(
      todoList.filter(_.id === id)
        .map(x => x.isCompleted).
        update(true)
    )
  }
}
