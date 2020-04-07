package models

import play.api.data.Form
import play.api.data.Forms._
import slick.jdbc.MySQLProfile.api._

case class Todo(var id: Int, var title: String, var isCompleted: Boolean)
case class TodoFormFormat(title: String, isCompleted: Boolean)

object TodoForm {
  var form = Form(
    mapping(
      "title" -> nonEmptyText,
      "is_completed" -> boolean
    )(TodoFormFormat.apply)(TodoFormFormat.unapply)
  )
}

class TodoTable(tag: Tag) extends Table[Todo](tag, "todos") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def isCompleted = column[Boolean]("is_completed")

  override def * = (id, title, isCompleted) <> (Todo.tupled, Todo.unapply)
}