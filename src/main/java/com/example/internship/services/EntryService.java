package com.example.internship.services;

import com.example.internship.entities.Entry;
import com.example.internship.repositories.EntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {

    @Autowired
    private EntryRepo entryRepo;

    public List<Entry> getAllEntries(Long userId) {
        return entryRepo.findByUserId(userId);
    }

    public Entry getEntryById(Long id) {
        return entryRepo.findById(id).orElseThrow(() -> new RuntimeException("Entry not found"));
    }

    public Entry createEntry(Entry entry) {
        return entryRepo.save(entry);
    }

    public Entry updateEntry(Long id, Entry updatedEntry) {
        Entry existingEntry = getEntryById(id);
        existingEntry.setTitle(updatedEntry.getTitle());
        existingEntry.setContent(updatedEntry.getContent());
        existingEntry.setStatus(updatedEntry.getStatus());
        return entryRepo.save(existingEntry);
    }

    public void deleteEntry(Long id) {
        entryRepo.deleteById(id);
    }
}
