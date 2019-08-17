package otserver4s.database

case class ConexaoBancoDados()
object ConexaoBancoDados {
  val DATABASE_URL = otserver4s.PropriedadeConfiguracoes("db.url")
  def criarConexao: com.j256.ormlite.support.ConnectionSource = new 
    com.j256.ormlite.jdbc.JdbcConnectionSource(DATABASE_URL)
}
