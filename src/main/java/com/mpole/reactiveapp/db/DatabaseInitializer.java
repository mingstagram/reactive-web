package com.mpole.reactiveapp.db;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final DatabaseClient databaseClient;
    private final ResourceLoader resourceLoader;

    public DatabaseInitializer(DatabaseClient databaseClient, ResourceLoader resourceLoader) {
        this.databaseClient = databaseClient;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        executeSqlFromFile("classpath:db/create_tables.sql")
                .doOnError(e -> System.err.println("Error executing SQL file: " + e.getMessage()))
                .subscribe();
    }

    private Mono<Void> executeSqlFromFile(String filePath) {
        try {
            Resource resource = resourceLoader.getResource(filePath);
            String sql = Files.readString(Paths.get(resource.getURI()));
            return databaseClient.sql(sql)
                    .then(); // 쿼리 실행 후 완료를 나타내기 위해 'then()' 사용
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
