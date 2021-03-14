package com.github.xmaiax

import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@SpringBootApplication
open class App: WebMvcConfigurer {

  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/**")
      .addResourceLocations("classpath:/static/")
    registry.addResourceHandler("swagger-ui.html")
      .addResourceLocations("classpath:/META-INF/resources/")
  }

  companion object {
    @JvmStatic fun main(args: Array<String>) { run(App::class.java, *args) }
    val logger = org.slf4j.LoggerFactory.getLogger(App::class.java)
  }

  @org.springframework.beans.factory.annotation.Value("\${server.port}")
  var webServerPort: Int = 0

  @javax.annotation.PostConstruct
  fun startedMessage() {
    logger.info("Rest service starting in port: ${this.webServerPort}")
  }

}
