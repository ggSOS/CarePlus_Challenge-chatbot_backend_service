package br.com.chatbot.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import br.com.chatbot.exception.type.swagger.READMEInvalidException;
import br.com.chatbot.exception.type.swagger.READMENotFoundException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        String readmeContent = "";

        File file = new File("./README.md");

        if (file.exists()) {
            try {
                Resource resource = new FileSystemResource(file);
                readmeContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new READMEInvalidException("Erro ao ler o README local");
            }
        } else {
            throw new READMENotFoundException("README.md não encontrado na raiz. Usando texto padrão.");
        }

        return new OpenAPI()
                .info(new Info()
                        .title("Minha API")
                        .version("1.0.0")
                        .description(readmeContent));
    }
}
