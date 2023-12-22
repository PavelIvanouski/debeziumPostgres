package com.example.debeziumPostgres.listener;


import com.example.debeziumPostgres.service.ChangeService;
import com.example.debeziumPostgres.utils.Operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class DebeziumListener {


    private final Executor executor = Executors.newSingleThreadExecutor();

    private final EmbeddedEngine engine;

    private final ChangeService changeService;


    private DebeziumListener(Configuration tableConnector, ChangeService changeService) {
        this.engine = EmbeddedEngine
                .create()
                .using(tableConnector)
                .notifying(this::handleEvent).build();
        this.changeService = changeService;
    }


    @PostConstruct
    private void start() {
        this.executor.execute(engine);
    }


    @PreDestroy
    private void stop() {
        if (this.engine != null) {
            this.engine.stop();
        }
    }


    private void handleEvent(SourceRecord sourceRecord) {
        Struct sourceRecordValue = (Struct) sourceRecord.value();
        Struct sourceRecordKey = (Struct) sourceRecord.key();

        if (sourceRecordValue != null) {
            Operation operation = Operation.forCode((String) sourceRecordValue.get(OPERATION));

            //Only if this is a transactional operation.
            if (operation != Operation.READ) {

                Map<String, Map<String, Object>> message = new HashMap<>();
                Struct struct;

                if (operation == Operation.DELETE || operation == Operation.UPDATE) {
                    struct = (Struct) sourceRecordValue.get(BEFORE);
                    message.put("before",returnMessage(struct));
                }

                struct = (Struct) sourceRecordValue.get(AFTER);
                message.put("after",returnMessage(struct));

                //Call the service to handle the data change.
                try {
                    changeService.addChangeFromListener(message, operation, sourceRecord.topic().substring(sourceRecord.topic().indexOf(".") + 1), (Long) sourceRecordKey.get("id"));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                log.info("Data Changed: {} with Operation: {}", message, operation.name());
            }
        }
    }

    private Map<String, Object> returnMessage(Struct struct) {
        return struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
    }
}
