package otserver4s.game

class GamePacket {
  
}

/*
import java.io.{ InputStream, OutputStream }

case class GameProtocol(
  input: InputStream,
  output: OutputStream,
  var jogador: Personagem = null, 
  var isLogado: Boolean = false) {
  
  def efetuarLogin = {
    //if(!isLogado && jogador == null) {
      val sistemaOperacional = 
        SistemaOperacional.lerSistemaOperacional(Packet.lerInt16(input)) 
      val versao = Packet.lerInt16(input)
      val isGM = Packet.lerByte(input) == 1
      val accountNumber = Packet.lerInt16(input)
      Packet.pularLeitura(input, Packet.lerInt16(input).toLong)
      val nome = Packet.lerString(input).get
      val senha = Packet.lerString(input)
      val protocoloLogin = ProtocoloLogin(
        sistemaOperacional, versao, Account(Option(accountNumber), senha))
      val conexao = ConexaoBancoDados.criarConexao
      jogador = Personagem.buscarPersonagem(protocoloLogin, isGM, nome, conexao)
      conexao.close
      
      val packet = Packet()

      packet.escreverByte(TipoRequest.PROCESSAR_LOGIN.codigo)
      packet.escreverInt32(jogador.idPersonagem + 0x10000000)
      packet.escreverByte(0x32)
      packet.escreverByte(0x00)
      packet.escreverByte(0x00)
      
      packet.escreverByte(0x64)
      packet.escreverInt16(jogador.posicaox)
      packet.escreverInt16(jogador.posicaoy)
      packet.escreverByte(jogador.posicaoz)

      var pular = -1
      
      def descricaoPiso(piso: Piso) = {
        // TODO Acertar essa implementação depois
      }
      
      def descricaoChao(x: Int, y: Int, z: Int, 
          comp: Short, alt: Short, offset: Int) = {
        for(nx <- 0 until comp) for(ny <- 0 until alt)
          Option(Mapa.getPiso(
            Posicao(
              (x + nx + offset).toShort, 
              (y + ny + offset).toShort, 
              (z & 0xff).toByte
            )
          )) match {
          case Some(piso) => { 
            if(pular > -1) {
              packet.escreverByte((pular & 0xff).toByte)
              packet.escreverByte(0xff.toByte)
            }
            pular = 0
            descricaoPiso(piso)
          }
          case None => {
            pular += 1
            if(pular == 0xff) {
              packet.escreverByte(0xff.toByte)
              packet.escreverByte(0xff.toByte)
              pular = -1
            }
          }
        }
      }
      
      def descricaoMapa(x: Int, y: Int, z: Byte, 
          comp: Short, alt: Short) = {
        var inicioZ = 7
        var fimZ = 0
        var passoZ = -1
        if(z > 7) {
          inicioZ = z - 2
          fimZ = Math.min(15, z + 2)
          passoZ = 1
        }
        var nz = inicioZ
        while(nz != fimZ + passoZ) {
          descricaoChao(x, y, nz, comp, alt, z - nz)
          nz += passoZ
        }
        if(pular > -1) {
          packet.escreverByte((pular & 0xff).toByte)
          packet.escreverByte(0xff.toByte)
        }
      }
      
      descricaoMapa(
        jogador.posicaox - 8, jogador.posicaoy - 6, 
        jogador.posicaoz, 18, 14)
      
      packet.escreverByte(0x83.toByte)
      packet.escreverInt16(jogador.posicaox)
      packet.escreverInt16(jogador.posicaoy)
      packet.escreverByte(jogador.posicaoz)
      packet.escreverByte(0x0b.toByte)
      
      Slot.values.foreach(slot => {
        packet.escreverByte(0x79)
        packet.escreverByte(slot.codigo)
      })
      
      packet.escreverByte(0xa0.toByte)
      packet.escreverInt16(jogador.vida)
      packet.escreverInt16(jogador.vidaMaxima)
      packet.escreverInt16(jogador.capacidade)
      packet.escreverInt32(jogador.exp)
      packet.escreverInt16(1) // Level
      packet.escreverByte(0) // % Level
      packet.escreverInt16(jogador.mana)
      packet.escreverInt16(jogador.manaMaxima)
      packet.escreverByte(jogador.magicLevel)
      packet.escreverByte(0) // % ML
      packet.escreverByte(jogador.alma)      
      
      packet.escreverByte(0xa1.toByte)
      packet.escreverByte(jogador.fistLevel)
      packet.escreverByte(0x00)
      packet.escreverByte(jogador.clubLevel)
      packet.escreverByte(0x00)
      packet.escreverByte(jogador.swordLevel)
      packet.escreverByte(0x00)
      packet.escreverByte(jogador.axeLevel)
      packet.escreverByte(0x00)
      packet.escreverByte(jogador.distanceLevel)
      packet.escreverByte(0x00)
      packet.escreverByte(jogador.shieldLevel)
      packet.escreverByte(0x00)
      packet.escreverByte(jogador.fishingLevel)
      packet.escreverByte(0x00)
      
      packet.escreverByte(0x82.toByte)
      packet.escreverByte(0xff.toByte)
      packet.escreverByte(215.toByte)
      
      packet.escreverByte(0x8d.toByte)
      packet.escreverInt32(jogador.idPersonagem + 0x10000000)
      packet.escreverByte(0xff.toByte)
      packet.escreverByte(215.toByte)
      
      isLogado = true
      packet
    /*}
    else {
      println("PASSOU!")
      val packet = Packet()
      packet
    }*/
  }
  
}
*/