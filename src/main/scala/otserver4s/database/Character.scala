package otserver4s.database

import scala.collection.JavaConverters._
import org.apache.log4j.Logger
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.support.ConnectionSource

import otserver4s.game.{
  Posicao, Direcao, Outfit, Luz, Escudo,
  Caveira, Item, Piso, Canal, Slot
}

@com.j256.ormlite.table.DatabaseTable(tableName = "personagens")
class Personagem {
  
  @DatabaseField(generatedId = true)
  var idPersonagem: Long = _
  def idInGame = idPersonagem + 0x10000000
  
  @DatabaseField(canBeNull = false)
  var accountNumber: Integer = _
  
  @DatabaseField(canBeNull = false)
  var nome: String = _
  
  var capacidade: Short = 400
  var exp: Long = 0
  var vida: Short = 150
  var mana: Short = 0
  var vidaMaxima: Short = 150
  var manaMaxima: Short = 0
  var alma: Byte = 100
  
  def posicao(pos: Posicao = Posicao(posicaox, posicaoy, posicaoz)) = {
    posicaox = pos.x
    posicaoy = pos.y
    posicaoz = pos.z
    Posicao(posicaox, posicaoy, posicaoz)
  }
  
  var posicaox: Short = 50
  var posicaoy: Short = 50
  var posicaoz: Byte = 7

  var _direcao: Byte = Direcao.SUL.codigo
  def direcao(dir: Direcao = Direcao(_direcao)) = {
    _direcao = dir.codigo
    dir
  }
  
  def outfit(outf: Outfit = Outfit(
    tipoOutfit, cabecaOutfit, corpoOutfit, 
    pernasOutfit, pesOutfit, extraOutfit)) = {
    tipoOutfit = outf.tipo
    cabecaOutfit = outf.cabeca
    corpoOutfit = outf.corpo
    pernasOutfit = outf.pernas
    pesOutfit = outf.pes
    extraOutfit = outf.extra
    outf
  }
  
  var tipoOutfit: Byte = (128 & 0xff).toByte
  var cabecaOutfit: Byte = 10
  var corpoOutfit: Byte = 20
  var pernasOutfit: Byte = 30
  var pesOutfit: Byte = 40
  var extraOutfit: Byte = Slot.ULTIMO.codigo
  
  var _caveira: Byte = Caveira.NENHUMA.codigo
  def caveira(cav: Caveira = Caveira(_caveira)) = {
    _caveira = cav.codigo
    cav
  }
  
  var magicLevel: Byte = 0
  var fistLevel: Byte = 10
  var swordLevel: Byte = 10
  var clubLevel: Byte = 10
  var axeLevel: Byte = 10
  var shieldLevel: Byte = 10
  var distanceLevel: Byte = 10
  var fishingLevel: Byte = 10
  
  var luz: Luz = Luz(255.toByte, 215.toByte)
  var escudo: Escudo = Escudo.NENHUM
  var inventario: Array[Item] = {
    val invtr = Array.ofDim[Item](Slot.values.length)
    for(i <- 0 until invtr.length) invtr(i) = null
    invtr
  }
  
  var piso: Piso = null
  var canalPvt: Canal = null
  
  override def toString = s"Personagem(id: $idPersonagem, nome: $nome, acc: $accountNumber)"
  
}
object Personagem {
  def criarTabelaSeNaoExistir(conexao: ConnectionSource) = 
    com.j256.ormlite.table.TableUtils.createTableIfNotExists(
      conexao, classOf[Personagem])
  private def criarDAO(conexao: ConnectionSource):
    com.j256.ormlite.dao.Dao[Personagem, Integer] = 
      com.j256.ormlite.dao.DaoManager.createDao(conexao, classOf[Personagem])
  def contarTodos(conexao: ConnectionSource) = 
    criarDAO(conexao).countOf
  def persistirNovo(accountNumber: Integer, nome: String, conexao: ConnectionSource) = {
    val personagem = new Personagem
    personagem.accountNumber = accountNumber
    personagem.nome = nome
    criarDAO(conexao).create(personagem)
  }
  def listarPersonagensPorAccount(accountNumber: Integer, conexao: ConnectionSource) =
    criarDAO(conexao).queryForEq("accountNumber", accountNumber).asScala.toList
  def buscarPersonagemPorNomeEAccount(nome: String, accountNumber: Integer, 
      conexao: ConnectionSource) = {
    val p = new Personagem
    p.accountNumber = accountNumber
    p.nome = nome
    criarDAO(conexao).queryForMatching(p).asScala.headOption
  }
}
