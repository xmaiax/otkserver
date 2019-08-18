package otserver4s.game

import scala.util.Try

case class TipoMensagem(codigo: Byte)
object TipoMensagem {
  val AMARELA = TipoMensagem(0x01)
  val AZUL_CLARO = TipoMensagem(0x04)
  val LARANJA = TipoMensagem(0x11)
  val AVISO = TipoMensagem(0x02)
  val EVENTO_ANDAMENTO = TipoMensagem(0x13)
  val EVENTO = TipoMensagem(0x14)
  val STATUS = TipoMensagem(0x15)
  val INFO = TipoMensagem(0x16)
  val STATUS_DISCRETO = TipoMensagem(0x17)
  val AZUL = TipoMensagem(0x18)
  val VERMELHO = TipoMensagem(0x19)
  val values = List(
    AMARELA, AZUL_CLARO, LARANJA, 
    AVISO, EVENTO_ANDAMENTO, EVENTO,
    STATUS, INFO, STATUS_DISCRETO,
    AZUL, VERMELHO)
  def getByCodigo(codigo: Byte) =
    values.filter(_.codigo == codigo).head
}

case class Canal(nome: String, codigo: Int, 
  isPublico: Boolean, isEspecial: Boolean)
object Canal {
  val CLAN = Canal("Clan", 0x00, false, false)
  val VIOLACOES = Canal("Violação de regras", 0x03, false, true)
  val CHAT = Canal("Chat", 0x04, true, false)
  val TROCA = Canal("Troca", 0x05, true, false)
  val RL_CHAT = Canal("Chat RL", 0x06, true, false)
  val AJUDA = Canal("Ajuda", 0x07, true, false)
  val DEVELS = Canal("Desenvolvedores", 0x08, false, true)
  val TUTOR = Canal("Tutor", 0x09, false, true)
  val GM = Canal("GM", 0x10, false, true)
  val PRIVADO = Canal("Privado", 0xff, false, false)
}

case class Item(codigo: Short)

case class Posicao(x: Short, y: Short, z: Byte) {
  def getPosicaoDaDirecao(direcao: Direcao) =
    direcao match {
      case Direcao.LESTE => this.copy(x = (x + 1).toShort)
      case Direcao.NORTE => this.copy(y = (y - 1).toShort)
      case Direcao.SUL => this.copy(y = (y + 1).toShort)
      case Direcao.OESTE => this.copy(x = (x - 1).toShort)
      case Direcao.NORDESTE => this.copy(x = (x + 1).toShort, y = (y - 1).toShort)
      case Direcao.NOROESTE => this.copy(x = (x - 1).toShort, y = (y - 1).toShort)
      case Direcao.SUDESTE => this.copy(x = (x + 1).toShort, y = (y + 1).toShort)
      case Direcao.SUDOESTE => this.copy(x = (x - 1).toShort, y = (y + 1).toShort)
      case _ => this
    }
}

case class Luz(raio: Integer, cor: Integer)

case class Outfit(
  tipo: Byte, cabeca: Byte, corpo: Byte, 
  pernas: Byte, pes: Byte, extra: Byte
)

case class Direcao(codigo: Byte)
object Direcao {
  val NORTE = Direcao(0x00)
  val LESTE = Direcao(0x01)
  val SUL = Direcao(0x02)
  val OESTE = Direcao(0x03)
  val NORDESTE = Direcao(0x04)
  val NOROESTE = Direcao(0x05)
  val SUDESTE = Direcao(0x06)
  val SUDOESTE = Direcao(0x07)
  val values = List(
    NORTE, LESTE, SUL, 
    OESTE, NORDESTE, NOROESTE, 
    SUDESTE, SUDOESTE)
  def getByCodigo(codigo: Byte) =
    values.filter(_.codigo == codigo).head
}

case class Caveira(codigo: Byte)
object Caveira {
  val NENHUMA = Caveira(0x00)
  val BRANCA = Caveira(0x01)
  val AMARELA = Caveira(0x02)
  val VERMELHA = Caveira(0x03)
  val values = List(NENHUMA, BRANCA, AMARELA, VERMELHA)
  def getByCodigo(codigo: Byte) =
    values.filter(_.codigo == codigo).head
}

case class Escudo(codigo: Byte)
object Escudo {
  val NENHUM = Escudo(0x00)
}

case class Slot(codigo: Byte)
object Slot {
  val CABECA = Slot(0x01)
  val AMULETO = Slot(0x02)
  val MOCHILA = Slot(0x03)
  val ARMADURA = Slot(0x04)
  val MAO_DIREITA = Slot(0x05)
  val MAO_ESQUERDA = Slot(0x06)
  val PERNAS = Slot(0x07)
  val PES = Slot(0x08)
  val ANEL = Slot(0x09)
  val EXTRA = Slot(0x0a)
  val ULTIMO = Slot(0x0b)
  val values = List(
    CABECA, AMULETO, MOCHILA,
    ARMADURA, MAO_DIREITA, 
    MAO_ESQUERDA, PERNAS, PES, 
    ANEL, EXTRA, ULTIMO
  )
  def getByCodigo(codigo: Byte) =
    values.filter(_.codigo == codigo).head
}

case class TipoConversa(codigo: Byte)
object TipoConversa {
  val NORMAL = TipoConversa(0x01)
  val COCHICHAR = TipoConversa(0x02)
  val GRITAR = TipoConversa(0x03)
  val PVT = TipoConversa(0x04)
  val CANAL_AMARELO = TipoConversa(0x05)
  val CANAL_VIOL_REGRS = TipoConversa(0x06)
  val RESP_VIOL_REGRS = TipoConversa(0x07)
  val CONT_VIOL_REGRS = TipoConversa(0x08)
  val BROADCAST = TipoConversa(0x09)
  val CANAL_VERMELHO = TipoConversa(0x0a)
  val PVT_VERMELHO = TipoConversa(0x0b)
  val CANAL_LARANJA = TipoConversa(0x0c)
  val VERMELHO_ANONIMO = TipoConversa(0x0d)
  val MONSTRO = TipoConversa(0x10)
  val MONSTRO_BERRANDO = TipoConversa(0x11)
  val values = List(
    NORMAL, COCHICHAR, GRITAR, PVT,
    CANAL_AMARELO, CANAL_VIOL_REGRS,
    RESP_VIOL_REGRS, CONT_VIOL_REGRS,
    BROADCAST, CANAL_VERMELHO, PVT_VERMELHO,
    CANAL_LARANJA, VERMELHO_ANONIMO,
    MONSTRO, MONSTRO_BERRANDO
  )
  def getByCodigo(codigo: Byte) =
    values.filter(_.codigo == codigo).head
}

case class Criatura()

case class Piso(codigo: Byte)
object Piso {
  val GRAMA = Piso(106)
}

object Mapa {
  final val campoVisaoX = 8
  final val campoVisaoY = 6
  
  private var mapa: Map[Posicao, Piso] = {
    var mapa = Map[Posicao, Piso]()
    for(x <- 0 until 100) for(y <- 0 until 100) 
      mapa += (Posicao(x.toShort, y.toShort, 7) -> Piso.GRAMA)
    mapa
  }
  def getPiso(posicao: Posicao) = Try(mapa(posicao)).getOrElse(null)
}
