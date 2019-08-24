package otserver4kotlin

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.support.ConnectionSource

@com.j256.ormlite.table.DatabaseTable(tableName = "personagens")
class Personagem(
	@DatabaseField(generatedId = true)
  var identificador: Long? = null,
	
	@DatabaseField(canBeNull = false) 
  var nome: String = "",
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
  var conta: Conta = Conta()
) {
	override fun toString() =
		"Character(name=$nome, account=${conta.codigo})"
	fun salvar(conexao: ConnectionSource) =
		conta.recarregar(conexao).personagens?.let { it.add(this) }
	companion object {
		fun criarTabelaSeNaoExistir(conexao: ConnectionSource) =
			com.j256.ormlite.table.TableUtils.createTableIfNotExists(
      conexao, Personagem::class.java)
		private fun criarDAO(conexao: ConnectionSource):
		  com.j256.ormlite.dao.Dao<Personagem, Long> = 
      com.j256.ormlite.dao.DaoManager.createDao(conexao, Personagem::class.java)
		fun contarTodos(conexao: ConnectionSource) = criarDAO(conexao).countOf()
		fun buscarPorNome(nome: String, conexao: ConnectionSource): Personagem {
			val where = HashMap<String, Any>(1)
			where["nome"] = nome
			criarDAO(conexao).queryForFieldValues(where)?.let {
			  if(it.size > 0) return it.first()
			}
			throw OTServerLoginException("mensagem.personagem.nao.existe")
		}
	}
}
