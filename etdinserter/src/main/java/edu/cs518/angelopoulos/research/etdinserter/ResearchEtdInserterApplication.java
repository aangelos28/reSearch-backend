package edu.cs518.angelopoulos.research.etdinserter;

import edu.cs518.angelopoulos.research.etdinserter.services.EtdInserterService;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableJpaRepositories("edu.cs518.angelopoulos.research.common.repositories")
@EnableElasticsearchRepositories("edu.cs518.angelopoulos.research.common.repositories")
@EntityScan(basePackages = "edu.cs518.angelopoulos.research.common.models")
@ComponentScan(basePackages = {
        "edu.cs518.angelopoulos.research.common.services",
        "edu.cs518.angelopoulos.research.etdinserter.services"
})
public class ResearchEtdInserterApplication {

    private static Namespace cliArgs;

    public static void main(String[] args) throws IOException {
        parseArgs(args);

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ResearchEtdInserterApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        // Insert ETD entries to databases
        EtdInserterService etdInserterService = context.getBean(EtdInserterService.class);
        etdInserterService.insertFromDirectory(cliArgs.getString("directory"), false);
        System.exit(0);
    }

    public static void parseArgs(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("etdinserter").build()
                .defaultHelp(true)
                .description("Scans a directory for EDT documents and inserts them in MySQL and ElasticSearch");

        parser.addArgument("-d", "--directory").required(true).type(String.class).help("Root of directory containing ETD documents.");

        try {
            cliArgs = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }
}
