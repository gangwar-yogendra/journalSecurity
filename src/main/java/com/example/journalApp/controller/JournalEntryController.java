package com.example.journalApp.controller;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.UserEntity;
import com.example.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.journalApp.service.JournalEntryService;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    // The @Autowired annotation is used to automatically inject the JournalEntryService dependency into this controller class.
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    // This annotation indicates that this method will handle POST requests to the "/create" endpoint.
    // And it will create a new journal entry with the data provided in the request body.
    @PostMapping("/create")
    // The @RequestBody annotation indicates that the method parameter should be bound to the body of the HTTP request.
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry entry) {

        try {
            // Logic to create a new journal entry in the database
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();

            journalEntryService.saveEntry(entry, userName);
            return new ResponseEntity<>(entry, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This is when you will enter a journal entry for a user then it will keep
    // the reference in user collection as Id of journal entry
    @GetMapping("/all")
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesOfUser() {

        // It wil authenticate the user in mongodb then you can fetch the details
        // otherwise unable to fetch the particular user details, Now we are not
        // allow to access for all details for any user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        // Logic to retrieve all journal entries of a specific user from the database
        UserEntity user = userService.findByUserName(userName);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<JournalEntry> entries = user.getJournalEntries();

        if(entries!= null && !entries.isEmpty()) {
            return new ResponseEntity<>(entries, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // This annotation indicates that this method will handle GET requests to the "/{id}" endpoint.
    // And it will retrieve the journal entry with the specified ID.
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable ObjectId id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        UserEntity user = userService.findByUserName(userName); // Ensure the user exists, otherwise return NOT_FOUND

        // Finding the journal entry id in user journal entry id list
        List<JournalEntry> journalEntries = user.getJournalEntries().stream()
                .filter(entry -> entry.getId().equals(id)).collect(Collectors.toList());

        if(journalEntries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Logic to retrieve a specific journal entry by ID from the database
        // Return null if the entry with the given ID does not exist
        Optional<JournalEntry> entry = journalEntryService.getEntryById(id);
        if(entry.isPresent()) {
            return new ResponseEntity<>(entry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // This annotation indicates that this method will handle DELETE requests to the "/{id}" endpoint.
    // And it will delete the journal entry with the specified ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        Optional<JournalEntry> entryToDelete = journalEntryService.getEntryById(id);
        if (entryToDelete.isPresent() && journalEntryService.deleteEntryById(id, userName)) {
            return new ResponseEntity<>(entryToDelete.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // This annotation indicates that this method will handle PUT requests to the "/{id}" endpoint.
    // And it will update the journal entry with the specified ID.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable ObjectId id, @RequestBody JournalEntry updatedEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        UserEntity user = userService.findByUserName(userName); // Ensure the user exists, otherwise return NOT_FOUND

        // Finding the journal entry id in user journal entry id list
        List<JournalEntry> journalEntries = user.getJournalEntries().stream()
                .filter(entry -> entry.getId().equals(id)).collect(Collectors.toList());

        if(journalEntries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Logic to update a specific journal entry by ID in the database
        JournalEntry existingEntry = journalEntryService.getEntryById(id).orElse(null);
        if (existingEntry != null) {
            existingEntry.setTitle(updatedEntry.getTitle());
            existingEntry.setContent(updatedEntry.getContent());
            existingEntry.setDate(new java.util.Date()); // Update the date to the current date
            journalEntryService.updateEntry(existingEntry);
            return new ResponseEntity<>(existingEntry, HttpStatus.OK); // Return the updated entry
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return NOT_FOUND if the entry with the given ID does not exist
    }
}





