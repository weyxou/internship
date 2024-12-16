package com.example.internship.services;

import com.example.internship.entities.Entry;
import com.example.internship.repositories.EntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EntryService {

    private static final Logger LOGGER = Logger.getLogger(EntryService.class.getName());
    private static final String UPLOAD_DIR = "images/";

    private final EntryRepo entryRepo;

    @Autowired
    public EntryService(EntryRepo entryRepo) {
        this.entryRepo = entryRepo;
    }

    public List<Entry> getAllEntries() {
        // Вернуть все записи
        return entryRepo.findAll();
    }

    public Entry getEntryById(Long id) {
        // Найти запись по ID
        return entryRepo.findById(id).orElse(null);
    }

    public Entry createEntry(Entry entry) {
        // Сохранить новую запись
        return entryRepo.save(entry);
    }

    public Entry updateEntry(Long id, Entry updatedEntry) {
        // Найти существующую запись
        Entry existingEntry = entryRepo.findById(id).orElse(null);
        if (existingEntry == null) {
            LOGGER.warning("Entry not found for ID: " + id);
            return null;
        }

        // Обновить поля записи
        existingEntry.setTitle(updatedEntry.getTitle());
        existingEntry.setContent(updatedEntry.getContent());
        existingEntry.setStatus(updatedEntry.getStatus());

        return entryRepo.save(existingEntry);
    }

    public boolean deleteEntry(Long id) {
        // Удалить запись по ID
        if (entryRepo.existsById(id)) {
            entryRepo.deleteById(id);
            return true;
        } else {
            LOGGER.warning("Entry not found for ID: " + id);
            return false;
        }
    }

    public List<String> addImagesToEntry(Long id, List<MultipartFile> images) {
        Entry entry = entryRepo.findById(id).orElse(null);
        if (entry == null) {
            LOGGER.warning("Entry not found for ID: " + id);
            return null;
        }

        List<String> uploadedImages = new ArrayList<>();

        for (MultipartFile image : images) {
            try {
                String fileName = saveImage(image);
                if (fileName != null) {
                    uploadedImages.add(fileName);
                    entry.getImages().add(fileName);
                }
            } catch (IllegalArgumentException | IOException e) {
                LOGGER.log(Level.SEVERE, "Error while processing image upload: " + e.getMessage(), e);
            }
        }

        entryRepo.save(entry);
        LOGGER.info("Images successfully added to entry. Current images: " + entry.getImages());
        return uploadedImages;
    }

    public boolean deleteImageFromEntry(Long id, String imageUrl) {
        Entry entry = entryRepo.findById(id).orElse(null);
        if (entry == null) {
            LOGGER.warning("Entry not found for ID: " + id);
            return false;
        }

        // Убедимся, что сравниваем только имя файла
        String fileName = imageUrl.replace("images/", "");
        if (!entry.getImages().contains(fileName)) {
            LOGGER.warning("Image file name not found in entry images: " + fileName);
            return false;
        }

        entry.getImages().remove(fileName);
        entryRepo.save(entry);

        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        LOGGER.info("Attempting to delete file: " + filePath.toAbsolutePath());

        try {
            boolean fileDeleted = Files.deleteIfExists(filePath);
            LOGGER.info("File deletion status: " + fileDeleted);
        } catch (IOException e) {
            LOGGER.severe("Error deleting file: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private String saveImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + originalFilename);
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path path = Paths.get(UPLOAD_DIR, fileName);
        LOGGER.info("Saving file: " + fileName + " at path: " + path.toAbsolutePath());
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());

        return fileName;
    }
}
