package com.github.xmaiax

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface ActiveRecord {

  companion object {
    private var appContext: ApplicationContext? = null
    fun getRepository() =
      ActiveRecord.appContext?.let {
        it.getBean(CommonRepository::class.java)
      } ?: run { throw IllegalAccessError("ApplicationContext not set!") }
    fun setApplicationContext(appContext: ApplicationContext) {
      ActiveRecord.appContext = appContext
    }
    fun <T: Any> listAll(entityClass: Class<T>, page: Int = 1, rowsPerQuery: Int? = null) =
      this.getRepository().listAll(entityClass, page, rowsPerQuery)
    inline fun <reified T: Any> listAll(page: Int = 1, rowsPerQuery: Int? = null) =
      this.listAll(T::class.java, page, rowsPerQuery)
    fun listAll(classTable: String, page: Int = 1, rowsPerQuery: Int? = null) =
      this.getRepository().listAll(classTable, page, rowsPerQuery)
    fun <T: Any> countAll(entityClass: Class<T>) =
      this.getRepository().countAll(entityClass)
    inline fun <reified T: Any> countAll() =
      this.countAll(T::class.java)
    fun countAll(classTable: String) =
      this.getRepository().countAll(classTable)
  }

  fun primaryKeyValue() =
    this.javaClass.getDeclaredFields().filter {
      it.getDeclaredAnnotation(javax.persistence.Id::class.java) != null
    }.map {
      if(!it.isAccessible()) it.setAccessible(true)
      it.get(this)
    }.first()

  fun save() = this.primaryKeyValue()?.let {
    ActiveRecord.getRepository().update(this) } ?:
    run { ActiveRecord.getRepository().create(this) }

  fun delete() = this.primaryKeyValue()?.let {
    ActiveRecord.getRepository().remove(this)
    this
  }

  fun reload() = this.primaryKeyValue()?.let {
    ActiveRecord.getRepository().findByPK(this.javaClass, it)
  }

}

@Repository
class CommonRepository(
  @PersistenceContext val entityManager: EntityManager,
  @Autowired val applicationContext: ApplicationContext,
  @Value("\${database.default.rowsperquery}") val defaultRowsPerQuery: Int
) {

  @javax.annotation.PostConstruct
  fun postConstruct() {
    ActiveRecord.setApplicationContext(this.applicationContext)
  }

  @Transactional(readOnly = false)
  fun create(instance: Any): Any {
    this.entityManager.persist(instance)
    return instance
  }

  @Transactional(readOnly = false)
  fun update(instance: Any) = this.entityManager.merge(instance)

  @Transactional(readOnly = false)
  fun remove(instance: Any) = this.entityManager.remove(this.update(instance))

  @Transactional(readOnly = true)
  fun findByPK(entityClass: Class<Any>, pk: Any) = this.entityManager.find(entityClass, pk)

  private fun createSelectAllQuery(tableIdentifier: String, page: Int, rowsPerQuery: Int?): List<Any?> {
    fun getRowsPerQuery() = rowsPerQuery?.let { it } ?: run { this.defaultRowsPerQuery }
    return this.entityManager.createQuery("FROM ${tableIdentifier} t")
      .setFirstResult((page - 1) * getRowsPerQuery())
      .setMaxResults(getRowsPerQuery()).getResultList().toList()
  }

  @Transactional(readOnly = true)
  fun <T: Any> listAll(entityClass: Class<T>, page: Int, rowsPerQuery: Int?) =
    this.createSelectAllQuery(entityClass.getSimpleName(), page, rowsPerQuery)

  @Transactional(readOnly = true)
  fun listAll(classTable: String, page: Int, rowsPerQuery: Int?) =
    this.createSelectAllQuery(classTable, page, rowsPerQuery)

  private fun createCountAllQuery(tableIdentifier: String) =
    this.entityManager.createQuery("SELECT COUNT(t) FROM ${tableIdentifier} t")
      .getSingleResult() as Long

  @Transactional(readOnly = true)
  fun <T: Any> countAll(entityClass: Class<T>) =
    this.createCountAllQuery(entityClass.getSimpleName())

  @Transactional(readOnly = true)
  fun countAll(classTable: String) =
    this.createCountAllQuery(classTable)

}
