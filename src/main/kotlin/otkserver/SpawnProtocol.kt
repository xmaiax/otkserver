package otkserver

class ProtocoloLoginOk {
	companion object {
		private fun escreverPosicao(posicao: Posicao, packet: Packet) {
			packet.escreverInt16(posicao.x)
			packet.escreverInt16(posicao.y)
			packet.escreverByte(posicao.z.toInt())
		}
		private fun escreverLuz(luz: Luz, packet: Packet) {
			packet.escreverByte(luz.raio)
			packet.escreverByte(luz.cor)
		}
		fun iniciarJogo(jogador: Personagem): Packet {
			println("Logando com o jogador: $jogador")
			val packet = Packet()
			packet.escreverByte(Packet.CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO)
			val identificador: Long = jogador.identificador.let { it }
				?: run { throw OTServerLoginException() }
			packet.escreverInt32(identificador + 0x0fffffff)
			
			packet.escreverInt16(0x32) // Velocidade de renderização do client
			packet.escreverByte(0x00) // Pode reportar erros
			packet.escreverByte(0x64) // Início info do mapa
			escreverPosicao(jogador.posicao(), packet) // Posição do jogador
			
			for(i in 0..251) {
				packet.escreverByte(106)
				packet.escreverByte(0) // Itens no chão
				if(i == 118) { // Posição jogador
    			packet.escreverByte(97)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(16)
    			packet.escreverByte(4)
    			packet.escreverByte(0)
    			packet.escreverByte(77)
    			packet.escreverByte(97)
    			packet.escreverByte(105)
    			packet.escreverByte(97)
    			packet.escreverByte(100)
    			packet.escreverByte(2)
    			packet.escreverByte(128)
    			packet.escreverByte(10)
    			packet.escreverByte(20)
    			packet.escreverByte(30)
    			packet.escreverByte(40)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
    			packet.escreverByte(0)
				}
				else packet.escreverByte(0) // Nenhuma criatura
				packet.escreverByte(0xff)
			}
			packet.escreverByte(106)
			packet.escreverByte(0)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			packet.escreverByte(255)
			
      packet.escreverByte(228)
      packet.escreverByte(255)
			
      packet.escreverByte(0x79)
			packet.escreverByte(Slot.CABECA.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.AMULETO.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.MOCHILA.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.ARMADURA.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.MAO_DIREITA.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.MAO_ESQUERDA.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.PERNAS.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.PES.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.ANEL.codigo)
			
			packet.escreverByte(0x79)
			packet.escreverByte(Slot.EXTRA.codigo)
			
			packet.escreverByte(0xa0) // Indicador início stats
			packet.escreverInt16(jogador.vida)
			packet.escreverInt16(jogador.vidaMax)
			packet.escreverInt16(jogador.capacidade)
			packet.escreverInt32(jogador.exp)
			packet.escreverInt16(jogador.level)
			packet.escreverByte(jogador.percentualProxLevel())
			packet.escreverInt16(jogador.mana)
			packet.escreverInt16(jogador.manaMax)
			
			// ML
			packet.escreverByte(4)
			
			// % ML
			packet.escreverByte(20)
			
			// Alma
			packet.escreverByte(92)
			
			// Indicador início skills
			packet.escreverByte(0xa1)
			
			// Fist Skill
			packet.escreverByte(12)
			packet.escreverByte(0)
			
			// Club Skill
			packet.escreverByte(15)
			packet.escreverByte(0)
			
			// Sword Skill
			packet.escreverByte(11)
			packet.escreverByte(0)
			
			// Axe Skill
			packet.escreverByte(14)
			packet.escreverByte(0)
			
			// Distance Skill
			packet.escreverByte(19)
			packet.escreverByte(0)
			
			// Shield Skill
			packet.escreverByte(13)
			packet.escreverByte(0)
			
			// Fishing Skill
			packet.escreverByte(17)
			packet.escreverByte(0)
			
			packet.escreverByte(0x83) // Indicador de efeito de spawn
			escreverPosicao(jogador.posicao(), packet) // Posicao efeito
			packet.escreverByte(0x04) // Código efeito
			
			// Luz jogador
			packet.escreverByte(0x82)
			escreverLuz(Luz(), packet)
			
			//----
			
			return packet
		}
	}
}
