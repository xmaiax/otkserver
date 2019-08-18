package otserver4s.login

import otserver4s.{ PropriedadeConfiguracoes => PConf }

case class SistemaOperacional(tipo: String) {
  override def toString = tipo.toString
}
object SistemaOperacional {
  val UNIX_LIKE = SistemaOperacional("Unix-like")
  val WINDOWS = SistemaOperacional("Windows")
  val OUTRO = SistemaOperacional("Outro")
  private val TODOS = List(UNIX_LIKE, WINDOWS, OUTRO) 
  def lerSistemaOperacional(codigoDoPacket: Int) =
    codigoDoPacket match {
      case 0x01 => UNIX_LIKE
      case 0x02 => WINDOWS
      case _ => OUTRO
    }
}
case class MOTD(mensagem: String, codigo: Byte = 0x01)
case class Mundo(nome: String, porta: Int, host: String, motd: MOTD)
object Mundo {
  final val INSTANCE = Mundo(
    PConf("mundo"), PConf("porta").toInt,
    PConf("host"), MOTD(PConf("motd"))
  )
}
