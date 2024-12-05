package com.example.internship.controllers;

import com.example.internship.entities.Entry;
import com.example.internship.exceptions.ErrorResponse;
import com.example.internship.services.EntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @GetMapping
    public List<Entry> getAllEntries() {
        return entryService.getAllEntries();
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

//    @DeleteMapping("/{id}")
//    public void deleteEntry(@PathVariable Long id) {
//        entryService.deleteEntry(id);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id, @RequestParam(required = false) Boolean confirm) {
        if (confirm == null || !confirm) {
            // Формируем кастомный ответ с кодом 400
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Confirm deletion with parameter 'confirm=true'."));
        }
        entryService.deleteEntry(id);
        return ResponseEntity.ok("Successfully deleted");
    }

}
