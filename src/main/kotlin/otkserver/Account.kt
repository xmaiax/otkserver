package otkserver

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.dao.ForeignCollection

@com.j256.ormlite.table.DatabaseTable(tableName = "contas")
class Conta(
  @DatabaseField(id = true, unique = true)
  var codigo: Int = 0,
  
  @DatabaseField(canBeNull = false) 
  var hashSenha: String = "",
  
  @DatabaseField(canBeNull = false) 
  var diasPremiumRestantes: Int = 0,
  
  @ForeignCollectionField(eager = false)
  var personagens: ForeignCollection<Personagem>? = null
) {
	override fun toString() =
		"Account(number=$codigo, senha=$hashSenha, premmy=$diasPremiumRestantes)"
	fun recarregar(conexao: ConnectionSource): Conta {
		Conta.criarDAO(conexao).refresh(this)
		return this
	}
	fun salvar(conexao: ConnectionSource) {
		this.hashSenha = MD5Utils.str2md5(this.hashSenha)
    Conta.criarDAO(conexao).create(this)
	}
	companion object {
		fun criarTabelaSeNaoExistir(conexao: ConnectionSource) =
			com.j256.ormlite.table.TableUtils.createTableIfNotExists(
      conexao, Conta::class.java)
		private fun criarDAO(conexao: ConnectionSource):
		  com.j256.ormlite.dao.Dao<Conta, Int> = 
      com.j256.ormlite.dao.DaoManager.createDao(conexao, Conta::class.java)
		fun contarTodos(conexao: ConnectionSource) = criarDAO(conexao).countOf()
		fun buscarPorProtocoloLogin(
			  protocoloLogin: ProtocoloLogin,
			  conexao: ConnectionSource): Conta {
			criarDAO(conexao).queryForId(protocoloLogin.account.number)?.let {
				return if(protocoloLogin.account.password.equals(it.hashSenha)) it
				else throw OTServerLoginException("mensagem.login.senha.incorreta")
			}
			throw OTServerLoginException("mensagem.login.conta.inexistente")
		}
	}
}
