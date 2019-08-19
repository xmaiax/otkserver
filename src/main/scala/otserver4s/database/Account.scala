package otserver4s.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.support.ConnectionSource
import org.apache.log4j.Logger
import otserver4s.OTServerLoginException
import otserver4s.login.LoginRequest
import otserver4s.utils.MD5Utils

@com.j256.ormlite.table.DatabaseTable(tableName = "contas")
class Conta {

  @DatabaseField(id = true, unique = true)
  var number: Integer = null

  @DatabaseField(canBeNull = false) 
  var hashSenha: String = null

  @DatabaseField(canBeNull = false) 
  var diasPremiumRestantes: Integer = null

  var personagens: List[Personagem] = null

  def adicionarPersonagens(personagens: List[Personagem]) = {
    this.personagens = personagens
    this
  }
  
  override def toString = 
    s"Conta(number: $number, hashSenha: $hashSenha, " +
    s"premium: $diasPremiumRestantes" +
    (if(personagens != null) s", personagens: ${personagens.length}" 
     else "") + ")" 
  
}

object Conta {
  def criarTabelaSeNaoExistir(conexao: ConnectionSource) =
    com.j256.ormlite.table.TableUtils.createTableIfNotExists(
      conexao, classOf[Conta])
  private def criarDAO(conexao: ConnectionSource):
    com.j256.ormlite.dao.Dao[Conta, Integer] = 
      com.j256.ormlite.dao.DaoManager.createDao(conexao, classOf[Conta])
  def contarTodos(conexao: ConnectionSource) = 
    criarDAO(conexao).countOf
  def persistirNova(number: Integer, senha: String, conexao: ConnectionSource) = 
    (Option(number), Option(senha)) match {
      case (Some(accNumber), Some(senhaDescriptografada)) => {
        val account = new Conta
        account.number = accNumber
        account.hashSenha = MD5Utils.str2md5(senhaDescriptografada)
        account.diasPremiumRestantes = 0
        criarDAO(conexao).create(account)
      }
      case (_, _) => throw new OTServerLoginException("mensagem.login.salvar.invalido")
    }
  def apply(loginRequest: LoginRequest, conexao: ConnectionSource, 
      carregarPersonagens: Boolean = true): Conta =
    loginRequest.versao match {
      case LoginRequest.VERSAO => {
        (loginRequest.accountNumber, loginRequest.password) match {
          case (None, None) => throw new OTServerLoginException("mensagem.login.conta.senha.vazia")
          case (None, Some(y)) => throw new OTServerLoginException("mensagem.login.conta.vazia")
          case (Some(x), None) => throw new OTServerLoginException("mensagem.login.senha.vazia")
          case (Some(x), Some(y)) => {
            val dao = criarDAO(conexao)
            Option(dao.queryForId(loginRequest.accountNumber.get)) match {
              case None => throw new OTServerLoginException("mensagem.login.conta.inexistente")
              case Some(account) =>
                if(loginRequest.password.get == account.hashSenha) 
                  if(carregarPersonagens)account.adicionarPersonagens(
                    Personagem.listarPersonagensPorAccount(account.number, conexao))
                  else account
                else throw new OTServerLoginException("mensagem.login.senha.incorreta")
            }
          }
        }
      }
      case _ => throw new OTServerLoginException("mensagem.login.versao.errada")
    }
  
}
