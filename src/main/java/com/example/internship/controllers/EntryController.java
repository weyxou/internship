package com.example.internship.controllers;

import com.example.internship.entities.Entry;
import com.example.internship.exceptions.ErrorResponse;
import com.example.internship.services.EntryService;
import com.example.internship.services.UserDetailsImpl;
import com.example.internship.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;
    private final UserService userService;

    // Конструктор, который инжектирует EntryService и UserService
    public EntryController(EntryService entryService, UserService userService) {
        this.entryService = entryService;
        this.userService = userService;
    }

    // Получить все записи для аутентифицированного пользователя
    @GetMapping
    public List<Entry> getAllEntries(Authentication authentication) {
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getEmail(); // Получаем email из аутентификации
        return entryService.getAllEntries(email);
    }

    // Получить запись по ID
    @GetMapping("/{id}")
    public ResponseEntity<Entry> getEntryById(@PathVariable Long id, Authentication authentication) {
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getEmail(); // Получаем email из аутентификации
        Entry entry = entryService.getEntryById(id, email);
        if (entry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(entry);
    }

    // Создать новую запись
    @PostMapping
    public ResponseEntity<Entry> createEntry(@RequestBody Entry entry, Authentication authentication) {
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getEmail(); // Получаем email из аутентификации
        Entry createdEntry = entryService.createEntry(entry, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
    }

    // Обновить запись
    @PutMapping("/{id}")
    public ResponseEntity<Entry> updateEntry(@PathVariable Long id, @RequestBody Entry updatedEntry, Authentication authentication) {
        String email = ((UserDetailsImpl) authentication.getPrincipal()).getEmail(); // Получаем email из аутентификации
        Entry entry = entryService.updateEntry(id, updatedEntry, email);
        if (entry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(entry);
    }

    // Удалить запись
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id, @RequestParam(required = false) Boolean confirm, Authentication authentication) {
        if (confirm == null || !confirm) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Confirm deletion with parameter 'confirm=true'."));
        }

        String email = ((UserDetailsImpl) authentication.getPrincipal()).getEmail(); // Получаем email из аутентификации
        boolean deleted = entryService.deleteEntry(id, email);

        if (deleted) {
            return ResponseEntity.ok("Successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Entry not found or you don't have permission to delete this entry."));
        }
    }
}
