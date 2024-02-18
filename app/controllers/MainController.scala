package controllers

import model.Note
import play.api.libs.Files
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import service.{NoteService, NoteUpdateLogService}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

class MainController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val notesService: NoteService,
                                val notesUpdateLogService: NoteUpdateLogService
                              )(implicit ec: ExecutionContext) extends BaseController {

  def getNotes(name: String): Action[AnyContent] = Action.async { _ =>
    notesService.getNotes(name).map { historyLog =>
      Ok(Json.toJson(historyLog))
    }
  }

  def createNote: Action[JsValue] = Action(parse.json) { request =>
    val noteResult = request.body.validate[Note]
    noteResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors)))
      },
      newNote => {
        notesService.createNote(newNote)
        Ok("Note for " + newNote.name + " created")
      }
    )
  }

  def updateNote(id: Int, password: String): Action[AnyContent] = Action.async { _ =>
    notesService.updateNotePassword(id, password).map { _ =>
      Ok("Password for note " + id + " updated")
    }
  }

  def deleteNote(id: Int): Action[AnyContent] = Action.async { _ =>
    notesService.deleteNote(id).map { _ =>
      Ok("Note " + id + " deleted")
    }
  }

  def getHistoryLogAll: Action[AnyContent] = Action.async { _ =>
    notesUpdateLogService.getHistoryLog().map { historyLog =>
      Ok(Json.toJson(historyLog))
    }
  }

  def getHistoryLogByNoteId(noteId: Int): Action[AnyContent] = Action.async { _ =>
    notesUpdateLogService.getHistoryLog(Some(noteId)).map { historyLog =>
      Ok(Json.toJson(historyLog))
    }
  }

  def importNotes: Action[MultipartFormData[Files.TemporaryFile]] = Action.async(parse.multipartFormData) { request =>
    val csvFiles = request.body.files.filter { filePart =>
      filePart.filename.endsWith(".csv")
    }
    if (csvFiles.isEmpty) {
      Future.successful(BadRequest("Файл CSV не найден"))
    }
    val importResult = csvFiles.map { csvFile =>
      notesService.importFromCSV(csvFile.ref)
    }
    Future.sequence(importResult).map { _ =>
      Ok("Импорт завершен успешно")
    }
  }

  def exportNotes: Action[AnyContent] = Action.async { _ =>
    val futureTempFile = notesService.exportToSCV()

    futureTempFile.map { tempFile =>
      Ok.sendFile(
        content = tempFile,
      ).withHeaders(
        CONTENT_TYPE -> "text/csv",
      )
    }
  }


}
