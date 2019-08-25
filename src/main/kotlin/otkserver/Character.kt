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
	
	var level: Int = 1,
	var exp: Long = 0,
	
	var vida: Int = 150,
	var vidaMax: Int = 150,
	
	var mana: Int = 0,
	var manaMax: Int = 0,
	
	var capacidade: Int = 400,
	
	var posicaox: Int = 50,
	var posicaoy: Int = 50,
  var posicaoz: Byte = 7,
	var direcao: Byte = Direcao.SUL.codigo,
	
	var outfitTipo: Int = 128,
	var outfitCabeca: Int = 10,
	var outfitCorpo: Int = 20,
	var outfitPernas: Int = 30,
	var outfitPes: Int = 40,
	var outfitExtra: Int = Slot.ULTIMO.codigo.toInt(),
	
	var caveira: Byte = Caveira.NENHUMA.codigo,
	
	var magicLevel: Int = 0,
	var fistLevel: Int = 10,
	var clubLevel: Int = 10,
	var swordLevel: Int = 10,
	var axeLevel: Int = 10,
	var distanceLevel: Int = 10,
	var shieldLevel: Int = 10,
	var fishingLevel: Int = 0
) {
	override fun toString() =
		"Character(name=$nome, account=${conta.codigo})"
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
	fun percentualProxLevel(): Int = 0
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
}
