package otserver4kotlin

class ConexaoBancoDados {
	companion object {
		val DATABASE_URL = PropriedadeConfiguracoes("db.url")
    fun criarConexao(): com.j256.ormlite.support.ConnectionSource {
      val cs = com.j256.ormlite.jdbc.JdbcPooledConnectionSource(DATABASE_URL)
      cs.setMaxConnectionAgeMillis(
		    PropriedadeConfiguracoes(
				  "db.tempo.vida.maxima.conexao.segundos").toInt() * 1000L)
      return cs
    }
	}
}
