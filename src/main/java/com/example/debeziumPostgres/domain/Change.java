package com.example.debeziumPostgres.domain;

import com.example.debeziumPostgres.utils.Operation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "CHANGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Builder(toBuilder = true)
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    private String tableName;

    private Long recordId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    private ChangeContentDoc changeContent = ChangeContentDoc.builder().build();

}
