package edu.cs518.angelopoulos.research.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "edu.cs518.angelopoulos.research.common"
})
@EntityScan(basePackages = {
        "edu.cs518.angelopoulos.research.common.models"
})
@EnableJpaRepositories(basePackages = {
        "edu.cs518.angelopoulos.research.common.repositories"
})
@EnableElasticsearchRepositories(basePackages = {
        "edu.cs518.angelopoulos.research.common.repositories"
})
public class CommonLibraryConfig {
}
