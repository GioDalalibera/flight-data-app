package com.giojo.flightdata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@Profile("dev")
public class OpenApiConfig {

        @Value("${spring.application.name}")
        private String appTitle;

        @Value("${spring.application.description}")
        private String appDescription;

        @Value("${spring.application.author.name}")
        private String appAuthorName;

        @Value("${spring.application.author.contact}")
        private String appAuthorContact;

        @Value("${spring.application.external-link}")
        private String appExternaLink;

        @Value("${spring.application.current-version}")
        private String appCurrentVersion;

        @Bean
        OpenAPI flightApi() {
                return new OpenAPI()
                                .info(new Info()
                                                .title(appTitle)
                                                .description(appDescription)
                                                .version(appCurrentVersion)
                                                .contact(new Contact().name(appAuthorName).email(appAuthorContact)))
                                .externalDocs(new ExternalDocumentation()
                                                .description("README")
                                                .url(appExternaLink));
        }
}
