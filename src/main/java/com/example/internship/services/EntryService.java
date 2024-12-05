package com.example.internship.services;

import com.example.internship.entities.Entry;
import com.example.internship.entities.User;
import com.example.internship.repositories.EntryRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {

    private final EntryRepo entryRepo;
    private final UserService userService;

    public EntryService(EntryRepo entryRepo, UserService userService) {
        this.entryRepo = entryRepo;
        this.userService = userService;
    }

    public List<Entry> getAllEntries() {
        User currentUser = userService.getCurrentUser();
        return entryRepo.findByUserId(currentUser.getId());
    }

    public Entry getEntryById(Long id) {
        User currentUser = userService.getCurrentUser();
        return entryRepo.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Entry not found or access denied"));
    }

    public Entry createEntry(Entry entry) {
        User currentUser = userService.getCurrentUser();
        entry.setUser(currentUser);
        return entryRepo.save(entry);
    }

    public Entry updateEntry(Long id, Entry updatedEntry) {
        Entry existingEntry = getEntryById(id);
        existingEntry.setTitle(updatedEntry.getTitle());
        existingEntry.setContent(updatedEntry.getContent());
        existingEntry.setStatus(updatedEntry.getStatus());
        existingEntry.setImage(updatedEntry.getImage()); // Обновление изображения
        return entryRepo.save(existingEntry);
    }

    public void deleteEntry(Long id) {
        Entry existingEntry = getEntryById(id);
        entryRepo.delete(existingEntry);
    }
}
