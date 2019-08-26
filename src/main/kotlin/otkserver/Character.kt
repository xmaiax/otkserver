package otkserver

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.support.ConnectionSource

@com.j256.ormlite.table.DatabaseTable(tableName = "personagens")
class Personagem(
	@DatabaseField(generatedId = true)
  var identificador: Long? = null,
	
	@DatabaseField(canBeNull = false) 
  var nome: String = "",
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
  var conta: Conta = Conta(),
	
	@DatabaseField(canBeNull = false)
	var vocacao: Byte = 0x01,
	
	@DatabaseField(canBeNull = false) 
	var exp: Long = 270,
	
	@DatabaseField(canBeNull = false) 
	var vida: Int = 150,
	
	@DatabaseField(canBeNull = false) 
	var mana: Int = 50,
	
	@DatabaseField(canBeNull = false) 
	var posicaox: Int = Mapa.PONTO_RESPAWN.x,
	
	@DatabaseField(canBeNull = false) 
	var posicaoy: Int = Mapa.PONTO_RESPAWN.y,
	
	@DatabaseField(canBeNull = false) 
  var posicaoz: Byte = Mapa.PONTO_RESPAWN.z,
	
	@DatabaseField(canBeNull = false)
	var direcao: Byte = Direcao.SUL.codigo,
	
	@DatabaseField(canBeNull = false) 
	var outfitTipo: Int = 128,
	
	@DatabaseField(canBeNull = false) 
	var outfitCabeca: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var outfitCorpo: Byte = 20,
	
	@DatabaseField(canBeNull = false) 
	var outfitPernas: Byte = 30,
	
	@DatabaseField(canBeNull = false) 
	var outfitPes: Byte = 40,
	
	@DatabaseField(canBeNull = false) 
	var outfitExtra: Byte = Slot.ULTIMO.codigo,
	
	@DatabaseField(canBeNull = false) 
	var caveira: Byte = Caveira.NENHUMA.codigo,
	
	@DatabaseField(canBeNull = false) 
	var magicLevel: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var mlPercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var fistLevel: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var fistPercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var clubLevel: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var clubPercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var swordLevel: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var swordPercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var axeLevel: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var axePercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var distanceLevel: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var distancePercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var shieldLevel: Byte = 10,
	
	@DatabaseField(canBeNull = false) 
	var shieldPercentual: Byte = 0,
	
	@DatabaseField(canBeNull = false) 
	var fishingLevel: Byte = 0,
	
	@DatabaseField(canBeNull = false)
	var fishingPercentual: Byte = 0
) {
	override fun toString() =
		"Character(name=$nome, vocation=${vocacao()}, " +
		"level=${ExpUtils.levelDaExp(exp)}, account=${conta.codigo})"
	fun salvar(conexao: ConnectionSource) =
		conta.recarregar(conexao).personagens?.let { it.add(this) }
	companion object {
		fun criarTabelaSeNaoExistir(conexao: ConnectionSource) =
			com.j256.ormlite.table.TableUtils.createTableIfNotExists(
      conexao, Personagem::class.java)
		private fun criarDAO(conexao: ConnectionSource):
		  com.j256.ormlite.dao.Dao<Personagem, Long> = 
      com.j256.ormlite.dao.DaoManager.createDao(conexao, Personagem::class.java)
		fun contarTodos(conexao: ConnectionSource) = criarDAO(conexao).countOf()
		fun buscarPorNome(nome: String, conexao: ConnectionSource): Personagem {
			val where = HashMap<String, Any>(1)
			where["nome"] = nome
			criarDAO(conexao).queryForFieldValues(where)?.let {
			  if(it.size > 0) return it.first()
			}
			throw OTServerLoginException("mensagem.personagem.nao.existe")
		}
	}
	fun vocacao(_vocacao: Vocacao = Vocacao.getByCodigo(vocacao)): Vocacao {
		vocacao = _vocacao.codigo
		return Vocacao.getByCodigo(vocacao)
	}
	fun percentualProxLevel(): Byte = 0
	fun posicao(posicao: Posicao =
			Posicao(posicaox, posicaoy, posicaoz)): Posicao {
		posicaox = posicao.x
		posicaoy = posicao.y
		posicaoz = posicao.z
		return Posicao(posicaox, posicaoy, posicaoz)
	}
	fun direcao(direcao: Direcao =
		  Direcao.getByCodigo(this.direcao)): Direcao {
		this.direcao = direcao.codigo
		return Direcao.getByCodigo(this.direcao)
	}
	fun outfit(outfit: Outfit = Outfit(
		  outfitTipo, outfitCabeca, outfitCorpo,
		  outfitPernas, outfitPes, outfitExtra)): Outfit {
		outfitTipo = outfit.tipo
		outfitCabeca = outfit.cabeca
		outfitCorpo = outfit.corpo
		outfitPernas = outfit.pernas
		outfitPes = outfit.pes
		outfitExtra = outfit.extra
		return outfit
	}
	fun caveira(caveira: Caveira =
			Caveira.getByCodigo(this.caveira)): Caveira {
		this.caveira = caveira.codigo
		return Caveira.getByCodigo(this.caveira)
	}
	fun skillML(ml: Skill =
			Skill(magicLevel, mlPercentual)): Skill {
		magicLevel = ml.level
		mlPercentual = ml.percentual
		return Skill(magicLevel, mlPercentual)
	}
	fun fistSkill(fist: Skill =
			Skill(fistLevel, fistPercentual)): Skill {
		fistLevel = fist.level
		fistPercentual = fist.percentual
		return Skill(fistLevel, fistPercentual)
	}
	fun clubSkill(club: Skill =
			Skill(clubLevel, clubPercentual)): Skill {
		clubLevel = club.level
		clubPercentual = club.percentual
		return Skill(clubLevel, clubPercentual)
	}
	fun swordSkill(sword: Skill =
			Skill(swordLevel, swordPercentual)): Skill {
		swordLevel = sword.level
		swordPercentual = sword.percentual
		return Skill(swordLevel, swordPercentual)
	}
	fun axeSkill(axe: Skill =
			Skill(axeLevel, axePercentual)): Skill {
		axeLevel = axe.level
		axePercentual = axe.percentual
		return Skill(axeLevel, axePercentual)
	}
	fun distanceSkill(distance: Skill =
			Skill(distanceLevel, distancePercentual)): Skill {
		distanceLevel = distance.level
		distancePercentual = distance.percentual
		return Skill(distanceLevel, distancePercentual)
	}
	fun shieldSkill(shield: Skill =
			Skill(shieldLevel, shieldPercentual)): Skill {
		shieldLevel = shield.level
		shieldPercentual = shield.percentual
		return Skill(shieldLevel, shieldPercentual)
	}
	fun fishingSkill(fishing: Skill =
			Skill(fishingLevel, fishingPercentual)): Skill {
		fishingLevel = fishing.level
		fishingPercentual = fishing.percentual
		return Skill(fishingLevel, fishingPercentual)
	}
}
