package controllers

import model.Note
import play.api.libs.Files
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import service.{NoteService, NoteUpdateLogService}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MainController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val notesService: NoteService,
                                val notesUpdateLogService: NoteUpdateLogService
                              )(implicit ec: ExecutionContext) extends BaseController {

  def getNotes(name: String): Action[AnyContent] = Action {
    Ok(Json.toJson(notesService.getNotes(name)))
  }

  def createNote: Action[JsValue] = Action(parse.json) { request =>
    val noteResult = request.body.validate[Note]
    noteResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors)))
      },
      noteDTO => {
        notesService.createNote(noteDTO)
        Ok("Note for " + noteDTO.name + " created")
      }
    )
  }

  def updateNote(id: Int, password: String): Action[AnyContent] = Action {
    notesService.updateNotePassword(id, password)
    Ok("Password for note " + id + " updated")
  }

  def deleteNote(id: Int): Action[AnyContent] = Action {
    notesService.deleteNote(id)
    Ok("Note " + id + " deleted")
  }

  def getHistoryLogAll: Action[AnyContent] = Action {
    Ok(Json.toJson(notesUpdateLogService.getHistoryLog()))
  }

  def getHistoryLogByNoteId(noteId: Int): Action[AnyContent] = Action {
    Ok(Json.toJson(notesUpdateLogService.getHistoryLog(Some(noteId))))
  }

  def importNotes: Action[MultipartFormData[Files.TemporaryFile]] = Action.async(parse.multipartFormData) { request =>
    val csvFiles = request.body.files.filter { filePart =>
      filePart.filename.endsWith(".csv")
    }
    if (csvFiles.isEmpty) {
      Future.successful(BadRequest("Файл CSV не найден"))
    }
    val importFutures = csvFiles.map { csvFile =>
      notesService.importFromCSV(csvFile.ref)
    }
    Future.sequence(importFutures).map { _ =>
      Ok("Импорт завершен успешно")
    }
  }

  def exportNotes: Action[AnyContent] = Action { _ =>
    val tempFile = notesService.exportToSCV()
    Ok.sendFile(
      content = tempFile,
    ).withHeaders(
      CONTENT_TYPE -> "text/csv",
      CONTENT_DISPOSITION -> s"inline; filename=tempFile.csv"
    )
  }


}
