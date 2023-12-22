package com.example.debeziumPostgres.service;

import com.example.debeziumPostgres.domain.Change;
import com.example.debeziumPostgres.domain.ChangeContentDoc;
import com.example.debeziumPostgres.repository.ChangeRepository;
import com.example.debeziumPostgres.utils.Operation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChangeService {

    private final ChangeRepository changeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addChangeFromListener(Map<String, Map<String, Object>> message, Operation operation, String tableName, Long recordId) throws JsonProcessingException {
        ChangeContentDoc changeContentDoc = ChangeContentDoc.builder().build();
        if (!operation.equals(Operation.DELETE)) {
            changeContentDoc.setChangeContent(objectMapper.writeValueAsString(message));
        }

        Change change = Change.builder()
                .changeContent(changeContentDoc)
                .operation(operation)
                .tableName(tableName)
                .recordId(recordId)
                .build();
        changeRepository.save(change);
    }

}
