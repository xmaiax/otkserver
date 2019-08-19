package otserver4s.game

import otserver4s.{ Client, Conectados, Packet }

object PacketInGame {
  
  def iniciarConexaoAtiva(client: Client): Packet = {
    val packet = Packet()
    client.jogador.map(jogador => {
      Conectados.adicionar(client)
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
      packet.escreverByte(Packet.CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO)
      packet.escreverInt32(jogador.idPersonagem)
      packet.escreverInt16(0x32) // Velocidade de renderização do client
      packet.escreverByte(0x01) // Pode reportar erros
      
      packet.escreverByte(0x64) // Adicionar descricao do mapa
      packet.escreverInt16(jogador.posicaox)
      packet.escreverInt16(jogador.posicaoy)
      packet.escreverByte(jogador.posicaoz)
      descricaoMapa(
        jogador.posicaox - 8, jogador.posicaoy - 6, 
        jogador.posicaoz, 18, 14)
      
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
      
      packet.escreverByte(0x83.toByte)
      packet.escreverInt16(jogador.posicaox)
      packet.escreverInt16(jogador.posicaoy)
      packet.escreverByte(jogador.posicaoz)
      packet.escreverByte(0x04)
      
      packet.escreverByte(0x82.toByte)
      packet.escreverByte(0xff.toByte)
      packet.escreverByte(215.toByte)
    })
    packet
  }
  
  def manterConexaoAtiva(client: Client) = {
    Packet.lerByte(client.socket.getInputStream) match {
      case 0x14 => client.desconectar()
      case 0x65 => {
        // TODO: Mover para norte
      }
      case 0x66 => {
        // TODO: Mover para leste
      }
      case 0x67 => {
        // TODO: Mover para sul
      }
      case 0x68 => {
        // TODO: Mover para oeste
      }
      case 0x6A => {
        // TODO: Mover para nordeste
      }
      case 0x6B => {
        // TODO: Mover para sudeste
      }
      case 0x6C => {
        // TODO: Mover para sudoeste
      }
      case 0x6D => {
        // TODO: Mover para nordeste
      }
      case 0x96 => {
        // TODO: Falar
      }
      case _ => Unit
    }
  }
  
}
