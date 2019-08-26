package otkserver

class ProtocoloLoginOk {
	companion object {
		val logger = org.apache.log4j.Logger.getLogger(ProtocoloLoginOk::class.java)
		val CONST_IDENTIFICADOR_JOGADOR = 0x0fffffff
		val CONST_RENDERIZACAO_CLIENT = 0x32
		val CONST_FLAG_REPORTAR_ERROS = 0x00
		val CONST_MARCACAO_INFO_MAPA = 0x64
		val CONST_MARCACAO_INVENTARIO_POPULADO = 0x78
		val CONST_MARCACAO_INVENTARIO_VAZIO = 0x79
		val CONST_MARCACAO_STATS = 0xa0
		val CONST_MARCACAO_SKILLS = 0xa1
		val CONST_MARCACAO_EFEITO_SPAWN = 0x83
		val CONST_LUZ_AMBIENTE = 0x82
		fun criarPacketLoginOk(jogador: Personagem): Packet {
			logger.debug("Logando: $jogador")
			val identificador: Long = jogador.identificador.let { it }
				?: run { throw OTServerLoginException() }
			val packet = Packet()
			fun escreverInfoClient() {
				packet.escreverByte(Packet.CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO)
  			packet.escreverInt32(identificador + CONST_IDENTIFICADOR_JOGADOR)
  			packet.escreverInt16(CONST_RENDERIZACAO_CLIENT)
  			packet.escreverByte(CONST_FLAG_REPORTAR_ERROS)
			}
			fun escreverPosicao(posicao: Posicao) {
  			packet.escreverInt16(posicao.x)
  			packet.escreverInt16(posicao.y)
  			packet.escreverByte(posicao.z)
  		}
  		fun escreverLuz(luz: Luz) {
  			packet.escreverByte(luz.raio)
  			packet.escreverByte(luz.cor)
  		}
			fun escreverLuzAmbiente(luz: Luz) {
				packet.escreverByte(CONST_LUZ_AMBIENTE)
				escreverLuz(luz)
			}
			fun escreverSkill(skill: Skill) {
				packet.escreverByte(skill.level)
				packet.escreverByte(skill.percentual)
			}
			fun escreverInventario() {
				fun escreverSlot(slot: Slot) {
					packet.escreverByte(CONST_MARCACAO_INVENTARIO_VAZIO)
					packet.escreverByte(slot.codigo)
				}
				escreverSlot(Slot.CABECA)
  			escreverSlot(Slot.AMULETO)
				escreverSlot(Slot.MOCHILA)
				escreverSlot(Slot.ARMADURA)
				escreverSlot(Slot.MAO_DIREITA)
				escreverSlot(Slot.MAO_ESQUERDA)
				escreverSlot(Slot.PERNAS)
				escreverSlot(Slot.PES)
				escreverSlot(Slot.ANEL)
				escreverSlot(Slot.EXTRA)
			}
			fun escreverStats() {
				packet.escreverByte(CONST_MARCACAO_STATS)
				val level = ExpUtils.levelDaExp(jogador.exp)
				val vidaMax = jogador.vocacao().vidaBase +
					(jogador.vocacao().vidaLevel * (level - 1))
				jogador.vida = if(jogador.vida > vidaMax) vidaMax else jogador.vida
  			packet.escreverInt16(jogador.vida)
  			packet.escreverInt16(vidaMax)
  			packet.escreverInt16(jogador.vocacao().capacidadeBase +
					(jogador.vocacao().capacidadeLevel * (level - 1)))
  			packet.escreverInt32(jogador.exp)
  			packet.escreverInt16(level)
  			packet.escreverByte(ExpUtils.percentualProxLevel(jogador.exp))
				val manaMax = jogador.vocacao().manaBase +
					(jogador.vocacao().manaLevel * (level - 1))
				jogador.mana = if(jogador.mana > manaMax) manaMax else jogador.mana
  			packet.escreverInt16(jogador.mana)
  			packet.escreverInt16(manaMax)
  			packet.escreverByte(jogador.magicLevel)
  			packet.escreverByte(jogador.mlPercentual)
  			packet.escreverByte(jogador.vocacao().almaBase)
			}
			fun escreverSkills() {
  			packet.escreverByte(CONST_MARCACAO_SKILLS)
  			escreverSkill(jogador.fistSkill())
  			escreverSkill(jogador.clubSkill())
  			escreverSkill(jogador.swordSkill())
  			escreverSkill(jogador.axeSkill())
  			escreverSkill(jogador.distanceSkill())
  			escreverSkill(jogador.shieldSkill())
  			escreverSkill(jogador.fishingSkill())
			}
			fun escreverEfeitoSpawn() {
				packet.escreverByte(CONST_MARCACAO_EFEITO_SPAWN)
  			escreverPosicao(jogador.posicao())
  			packet.escreverByte(EfeitosEspeciais.SPAWN.codigo)
			}
			fun escreverMapa() {
				packet.escreverByte(CONST_MARCACAO_INFO_MAPA)
  			escreverPosicao(jogador.posicao())
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
  				else packet.escreverByte(0)
  				packet.escreverByte(0xff)
  			}
  			packet.escreverByte(106)
  			packet.escreverByte(0)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
  			packet.escreverByte(0xff)
        packet.escreverByte(228)
        packet.escreverByte(0xff)
			}
			escreverInfoClient()
			escreverMapa()
			escreverInventario()
			escreverStats()
			escreverSkills()
			escreverEfeitoSpawn()
			escreverLuzAmbiente(Luz())
			logger.debug("${jogador.nome} logado com sucesso!")
			return packet
		}
	}
}
