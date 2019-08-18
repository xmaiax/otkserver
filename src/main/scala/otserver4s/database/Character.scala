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
  
  @DatabaseField(canBeNull = false)
  var accountNumber: Integer = _
  
  @DatabaseField(canBeNull = false)
  var nome: String = _
  
  @DatabaseField(canBeNull = false)
  var capacidade: Short = _
  
  @DatabaseField(canBeNull = false)
  var exp: Long = _
  
  @DatabaseField(canBeNull = false)
  var vida: Short = _
  
  @DatabaseField(canBeNull = false)
  var mana: Short = _
  
  @DatabaseField(canBeNull = false)
  var vidaMaxima: Short = _
  
  @DatabaseField(canBeNull = false)
  var manaMaxima: Short = _
  
  @DatabaseField(canBeNull = false)
  var alma: Byte = _
  
  def posicao(pos: Posicao = Posicao(posicaox, posicaoy, posicaoz)) = {
    posicaox = pos.x
    posicaoy = pos.y
    posicaoz = pos.z
    Posicao(posicaox, posicaoy, posicaoz)
  }
  
  @DatabaseField(canBeNull = false)
  var posicaox: Short = _
  
  @DatabaseField(canBeNull = false)
  var posicaoy: Short = _
  
  @DatabaseField(canBeNull = false)
  var posicaoz: Byte = _

  @DatabaseField(canBeNull = false, columnName = "direcao")
  var _direcao: Byte = _
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
  
  @DatabaseField(canBeNull = false)
  var tipoOutfit: Byte = _
  
  @DatabaseField(canBeNull = false)
  var cabecaOutfit: Byte = _ 
  
  @DatabaseField(canBeNull = false)
  var corpoOutfit: Byte = _
  
  @DatabaseField(canBeNull = false)
  var pernasOutfit: Byte = _
  
  @DatabaseField(canBeNull = false)
  var pesOutfit: Byte = _
  
  @DatabaseField(canBeNull = false)
  var extraOutfit: Byte = _

  @DatabaseField(canBeNull = false, columnName = "caveira")
  var _caveira: Byte = _
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
  
  var luz: Luz = Luz(255, 215)
  
  var escudo: Escudo = Escudo.NENHUM

  var inventario: Array[Item] = {
    val invtr = Array.ofDim[Item](Slot.values.length)
    for(i <- 0 until invtr.length) invtr(i) = null
    invtr
  }
  
  var piso: Piso = null
  
  var canalPvt: Canal = null
  
  override def toString = s"Personagem(nome: $nome)"
  
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
    personagem.capacidade = 400
    personagem.exp = 0
    personagem.vida = 150
    personagem.mana = 0
    personagem.vidaMaxima = 150
    personagem.manaMaxima = 0
    personagem.alma = 100
    personagem.posicao(Posicao(50, 50, 7))
    personagem.direcao(Direcao.SUL)
    personagem.outfit(Outfit((128 & 0xff).toByte, 10, 20, 30, 40, Slot.ULTIMO.codigo))
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
