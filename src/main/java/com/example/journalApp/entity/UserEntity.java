package com.example.journalApp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    @NonNull
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NonNull
    private String password;
    private List<String> roles;

    /* this being added since we do not have any implementation of the UserRepository yet,
    but we will need to have a list of UserRepository for each user */
    private String email;
    private boolean sentimentalAnalysisEnabled;

    /* In Spring Boot with Spring Data MongoDB, @DBRef is used to create a reference from one
    MongoDB document to another document, instead of embedding the entire document inside
    the parent document */
    @DBRef
    private List<JournalEntry> journalEntries = new ArrayList<>();
}
