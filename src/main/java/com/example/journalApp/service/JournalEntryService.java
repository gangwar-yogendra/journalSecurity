package com.example.journalApp.service;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.User;
import com.mongodb.DBRef;
import com.example.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// This is a service class for handling business logic related to journal entries.
// So Controllers can call methods from this service class to perform operations on journal entries.
// Controller -> Service -> Repository
/*@Component*/

// Currently we have commented the @Component because as per standard we need to keep the
// annotation as @Service since we are writing the business logic in service classes
@Service
public class JournalEntryService {
    // The @Autowired annotation is used to automatically inject the JournalEntryRepository dependency into this service class.
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MongoTemplate mongoTemplate;


    private static final Logger logger = LoggerFactory.getLogger(JournalEntryService.class);

    // This method saves a journal entry to the database using the repository.
    // User of @Transaction process complete else will not do any operation in the given function
    // to store the data in collection of mongodb
    @Transactional
    public void saveEntry(JournalEntry entry, String userName) {
        if (userName == null || userName.isBlank()) {
            logger.error("userName must not be blank");
            throw new IllegalArgumentException("userName must not be blank");
        }
        User user = userService.findByUserName(userName);
        if (user == null) {
            logger.error("User not found: {}", userName);
            throw new IllegalArgumentException("User not found: " + userName);
        }

        entry.setDate(new java.util.Date()); // Set the current date for the journal entry
        logger.info("Saving journal entry for user: {}", userName);
        JournalEntry saved = journalEntryRepository.save(entry);

        // This code section is updating the journalEntry field of the User entity with the newly created journal entry.
        user.getJournalEntries().add(saved);
        userService.saveEntry(user);
    }

    // Get all database entries
    public List<JournalEntry> getAllEntries()
    {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteEntryById(ObjectId id, String userName) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("userName must not be blank");
        }

        try {
            User user = userService.findByUserName(userName);
            if (user == null) {
                return false;
            }

            boolean removedFromLoadedList = user.getJournalEntries()
                    .removeIf(entry -> entry != null && id.equals(entry.getId()));
            boolean removedFromUser = removedFromLoadedList;
            if (!removedFromUser) {
                Query query = Query.query(Criteria.where("userName").is(userName));
                Update update = new Update()
                        .pull("relatedEntries", new DBRef("journal_entries", id))
                        .pull("journalEntries", new DBRef("journal_entries", id));
                removedFromUser = mongoTemplate.updateFirst(query, update, User.class).getModifiedCount() > 0;
            }
            if (!removedFromUser) {
                return false;
            }

            if (removedFromLoadedList) {
                userService.saveEntry(user);
            }
            journalEntryRepository.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void updateEntry(JournalEntry entry) {
        journalEntryRepository.save(entry);
    }
}
