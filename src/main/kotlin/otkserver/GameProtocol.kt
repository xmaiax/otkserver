package otkserver

class ProtocoloInGame {
	
	private val packet = Packet()
	private var pular = -1
	
	private fun adicionarCriatura(
		  id: Int, nome: String, percentualVida: Int, direcao: Direcao,
		  outfit: Outfit, luz: Luz, velocidade: Int, caveira: Caveira) {
		
		// Assumindo desconhecida
		packet.escreverByte(0x61)
		packet.escreverInt32(0)
		packet.escreverInt32(id.toLong() + 268435456L)
		packet.escreverString(nome)
		
		packet.escreverByte(percentualVida)
		packet.escreverByte(direcao.codigo.toInt())
		packet.escreverByte(outfit.tipo.toInt())
		packet.escreverByte(outfit.cabeca.toInt())
		packet.escreverByte(outfit.corpo.toInt())
		packet.escreverByte(outfit.pernas.toInt())
		packet.escreverByte(outfit.pes.toInt())
		packet.escreverByte(luz.raio.toInt())
		packet.escreverByte(luz.cor.toInt())
		packet.escreverInt16(velocidade)
		packet.escreverByte(caveira.codigo.toInt())
		packet.escreverByte(Escudo.NENHUM.codigo.toInt())
	}
	
	private fun descricaoChao(x: Int, y: Int, z: Int,
		comp: Short, alt: Short, offset: Int) {
		for(nx in 0..comp) for(ny in 0..alt) {
			val posicaoPiso = Posicao(
			  x + nx + offset, y + ny + offset,
				(z and 0xff).toByte())
		  Mapa.getPiso(posicaoPiso)?.let { piso ->
				if(pular > -1) {
			    packet.escreverByte(pular and 0xff)
					packet.escreverByte(0xff)
				}
			  pular = 0
			  packet.escreverInt16(piso.tipoPiso.codigo.toInt())
			  piso.entidades.forEach { entidade ->
				  adicionarCriatura(
					  entidade.identificador?.let { it.toInt() } ?: run { 1 },
					  entidade.nome,
					  100,
					  entidade.direcao(),
					  entidade.outfit(),
					  Luz(),
					  20,
					  entidade.caveira()
					)
			  }
		  } ?: run {
			  pular++
			  if(pular == 0xff) {
				  packet.escreverByte(0xff)
				  packet.escreverByte(0xff)
				  pular = -1
				}
		  }
		}
	}
	
	private fun descricaoMapa(
		  posicao: Posicao, comp: Short, alt: Short) {
		var inicioZ = 7
    var fimZ = 0
    var passoZ = -1
    if(posicao.z > 7) {
      inicioZ = posicao.z - 2
      fimZ = Math.min(15, posicao.z + 2)
      passoZ = 1
    }
    var nz = inicioZ
    while(nz != (fimZ + passoZ)) {
      descricaoChao(posicao.x, posicao.y,
		    nz, comp, alt, posicao.z - nz)
      nz += passoZ
    }
    if(pular > -1) {
      packet.escreverByte(pular and 0xff)
      packet.escreverByte(0xff)
    }
	}
	
	private fun escreverPosicao(posicao: Posicao) {
		packet.escreverInt16(posicao.x)
		packet.escreverInt16(posicao.y)
		packet.escreverByte(posicao.z.toInt() and 0xff)
	}
	
	fun iniciarJogo(jogador: Personagem): Packet {
    packet.escreverByte(Packet.CODIGO_PACKET_PROCESSAR_LOGIN_SUCESSO)
		packet.escreverInt32(jogador.identificador?.let { it } ?: run { 1L })
		packet.escreverInt16(0x32) // Velocidade de renderização do client
    packet.escreverByte(0x01) // Pode reportar erros
		
		packet.escreverByte(0x64) // Adicionar descricao do mapa
		escreverPosicao(jogador.posicao())
		descricaoMapa(jogador.posicao(), 18, 14)
		
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.CABECA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.CABECA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.AMULETO.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.MOCHILA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.ARMADURA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.MAO_DIREITA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.MAO_ESQUERDA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.PERNAS.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.PES.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.ANEL.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.EXTRA.codigo.toInt())
		packet.escreverByte(0x79)
		packet.escreverByte(Slot.ULTIMO.codigo.toInt())
		
		packet.escreverByte(0xa1)
    packet.escreverByte(jogador.fistLevel and 0xff)
    packet.escreverByte(0x00)
    packet.escreverByte(jogador.clubLevel and 0xff)
    packet.escreverByte(0x00)
    packet.escreverByte(jogador.swordLevel and 0xff)
    packet.escreverByte(0x00)
    packet.escreverByte(jogador.axeLevel and 0xff)
    packet.escreverByte(0x00)
    packet.escreverByte(jogador.distanceLevel and 0xff)
    packet.escreverByte(0x00)
    packet.escreverByte(jogador.shieldLevel and 0xff)
    packet.escreverByte(0x00)
    packet.escreverByte(jogador.fishingLevel and 0xff)
    packet.escreverByte(0x00)
		
		packet.escreverByte(0x83)
		escreverPosicao(jogador.posicao())
		packet.escreverByte(0x04)
		
		packet.escreverByte(0x82)
    packet.escreverByte(0xff)
    packet.escreverByte(215 and 0xff)
		
    return packet
	}
}
