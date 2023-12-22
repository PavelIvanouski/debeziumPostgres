package com.example.debeziumPostgres.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.IOException;

@Configuration
public class DebeziumConnectorConfig {

    @Value("${transfer.datasource.host}")
    private String transferDBHost;

    @Value("${transfer.datasource.database}")
    private String transferDBName;

    @Value("${transfer.datasource.port}")
    private String transferDBPort;

    @Value("${transfer.datasource.username}")
    private String transferDBUserName;

    @Value("${transfer.datasource.password}")
    private String transferDBPassword;

    private String TEST_TABLE_NAME = "public.TEST_TABLE";

    @Bean
    public io.debezium.config.Configuration tableConnector(Environment env) throws IOException {
        return io.debezium.config.Configuration.create()
                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/Users/user/Documents/Projects/debeziumTest/table-offset.dat")
                .with("offset.flush.interval.ms", 60000)
                .with("name", "table-postgres-connector")
                .with("database.server.name", transferDBHost + "-" + transferDBName)
                .with("database.hostname", transferDBHost)
                .with("database.port", transferDBPort)
                .with("database.user", transferDBUserName)
                .with("database.password", transferDBPassword)
                .with("database.dbname", transferDBName)
                .with("topic.prefix", "test")
                .with("table.include.list", TEST_TABLE_NAME)
                .with("plugin.name", "pgoutput").build();
    }
}