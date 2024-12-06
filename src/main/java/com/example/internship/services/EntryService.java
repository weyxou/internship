package com.example.internship.services;

import com.example.internship.entities.Entry;
import com.example.internship.entities.User;
import com.example.internship.repositories.EntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {

    private final EntryRepo entryRepo;
    private final UserService userService;

    @Autowired
    public EntryService(EntryRepo entryRepo, UserService userService) {
        this.entryRepo = entryRepo;
        this.userService = userService;
    }

    // Изменение метода для получения записей по email
    public List<Entry> getAllEntries(String email) {
        User user = userService.findByEmail(email);  // Получаем пользователя по email
        return entryRepo.findByUserId(user.getId());  // Получаем записи, принадлежащие этому пользователю
    }

    public Entry getEntryById(Long id, String email) {
        User user = userService.findByEmail(email);  // Получаем пользователя по email
        return entryRepo.findByIdAndUserId(id, user.getId()).orElse(null);
    }

    public Entry createEntry(Entry entry, String email) {
        User user = userService.findByEmail(email);  // Получаем пользователя по email
        entry.setUser(user);  // Привязываем запись к пользователю
        return entryRepo.save(entry);
    }

    public Entry updateEntry(Long id, Entry updatedEntry, String email) {
        User user = userService.findByEmail(email);  // Получаем пользователя по email
        Entry entry = entryRepo.findByIdAndUserId(id, user.getId()).orElse(null);
        if (entry != null) {
            entry.setTitle(updatedEntry.getTitle());
            entry.setContent(updatedEntry.getContent());
            entry.setStatus(updatedEntry.getStatus());
            return entryRepo.save(entry);
        }
        return null;
    }

    public boolean deleteEntry(Long id, String email) {
        User user = userService.findByEmail(email);  // Получаем пользователя по email
        Entry entry = entryRepo.findByIdAndUserId(id, user.getId()).orElse(null);
        if (entry != null) {
            entryRepo.delete(entry);
            return true;
        }
        return false;
    }
}
