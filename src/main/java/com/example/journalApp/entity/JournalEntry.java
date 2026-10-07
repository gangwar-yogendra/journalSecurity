package com.example.journalApp.entity;


import com.example.journalApp.enums.Sentiment;
import lombok.*;
import org.bson.types.ObjectId;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// This is a simple entity class representing a journal entry. It has three fields: id, title, and content.
// In a real application, you would typically use a database to store journal entries.
// The @Document annotation indicates that this class is a MongoDB document.
@Document(collection = "journal_entries")
// @Getter
// @Setter
// OR
@Data
@NoArgsConstructor
public class JournalEntry {
    // In a real application, you would typically use a database to store journal entries.
    // The @Id annotation indicates that this field is the unique identifier for the journal entry.
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ObjectId id;
    @NonNull
    private String title;
    private String content;
    private Date date;

    // The sentiment of the journal entry
    // And since we have updated the Sentimental a new field
    // then we need to update the database also using with this new filed
    // to test
    private Sentiment sentiment;
}
