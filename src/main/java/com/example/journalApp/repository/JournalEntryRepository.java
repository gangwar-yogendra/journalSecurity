package com.example.journalApp.repository;

import com.example.journalApp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

// It will be used to perform CRUD operations on journal entries in the database.
public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {
    // This interface will automatically inherit methods for CRUD operations from MongoRepository.
    // You can also define custom query methods here if needed.
}
