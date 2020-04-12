package com.github.xmaiax

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.service.ResponseMessage
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Profile("!prod")
@Configuration
@EnableSwagger2
class SwaggerConfig(
  @Value("\${info.app.name}") val name: String,
  @Value("\${info.app.description}") val description: String,
  @Value("\${info.app.version}") val version: String
) {

  @Bean
  fun docket() = Docket(DocumentationType.SWAGGER_2)
    .select().apis(
      RequestHandlerSelectors.basePackage(
        SwaggerConfig::class.java.getPackage().getName()
      )
    )
    .paths(PathSelectors.regex("/.*"))
    .build().apiInfo(
      ApiInfoBuilder()
        .title(this.name)
        .version(this.version)
        .description(this.description.trim())
        .build()
    )
    .useDefaultResponseMessages(false)
    .globalResponseMessage(
      RequestMethod.GET,
      ArrayList(
        arrayOf<ResponseMessage>(
          ResponseMessageBuilder().code(404)
            .message("Are you connected to the internet?").build(),
          ResponseMessageBuilder().code(500)
            .message("Internal server error, our fault...").build()
        ).toList()
      )
    )
    .globalResponseMessage(
      RequestMethod.POST,
      ArrayList(
        arrayOf<ResponseMessage>(
          ResponseMessageBuilder().code(400)
            .message("We can't understand what you tried to send...").build(),
          ResponseMessageBuilder().code(404)
            .message("Are you connected to the internet?").build(),
          ResponseMessageBuilder().code(500)
            .message("Internal server error, did you send something unexpected?").build()
        ).toList()
      )
    )

}
