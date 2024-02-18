package repository

import io.getquill.{PostgresJdbcContext, SnakeCase}

class DbContext {
  val ctx = new PostgresJdbcContext(SnakeCase, "ctx")
}