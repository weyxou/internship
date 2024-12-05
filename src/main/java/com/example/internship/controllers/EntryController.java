package com.example.internship.controllers;

import com.example.internship.entities.Entry;
import com.example.internship.services.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    @Autowired
    private EntryService entryService;

    @GetMapping("/user/{userId}")
    public List<Entry> getAllEntries(@PathVariable Long userId) {
        return entryService.getAllEntries(userId);
    }

    @GetMapping("/{id}")
    public Entry getEntryById(@PathVariable Long id) {
        return entryService.getEntryById(id);
    }

    @PostMapping
    public Entry createEntry(@RequestBody Entry entry) {
        return entryService.createEntry(entry);
    }

    @PutMapping("/{id}")
    public Entry updateEntry(@PathVariable Long id, @RequestBody Entry updatedEntry) {
        return entryService.updateEntry(id, updatedEntry);
    }

    @DeleteMapping("/{id}")
    public void deleteEntry(@PathVariable Long id) {
        entryService.deleteEntry(id);
    }
}
