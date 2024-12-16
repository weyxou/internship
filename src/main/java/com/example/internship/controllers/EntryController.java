package com.example.internship.controllers;

import com.example.internship.entities.Entry;
import com.example.internship.exceptions.ErrorResponse;
import com.example.internship.services.EntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    public ResponseEntity<Entry> getEntryById(@PathVariable Long id) {
        Entry entry = entryService.getEntryById(id);
        if (entry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(entry);
    }

    @PostMapping
    public ResponseEntity<Entry> createEntry(@RequestBody Entry entry) {
        Entry createdEntry = entryService.createEntry(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entry> updateEntry(@PathVariable Long id, @RequestBody Entry updatedEntry) {
        Entry entry = entryService.updateEntry(id, updatedEntry);
        if (entry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id, @RequestParam(required = false) Boolean confirm) {
        if (confirm == null || !confirm) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Confirm deletion with parameter 'confirm=true'."));
        }

        boolean deleted = entryService.deleteEntry(id);

        if (deleted) {
            return ResponseEntity.ok("Successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Entry not found."));
        }
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImages(@PathVariable Long id,
                                          @RequestParam("images") List<MultipartFile> images) {
        List<String> uploadedImages = entryService.addImagesToEntry(id, images);

        if (uploadedImages == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entry not found.");
        }

        return ResponseEntity.ok(uploadedImages);
    }

    @DeleteMapping(value = "/{id}/images/{fileName}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id,
                                         @PathVariable String fileName) {
        String imageUrl = "images/" + fileName;

        boolean success = entryService.deleteImageFromEntry(id, imageUrl);

        if (!success) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image or entry not found.");
        }

        return ResponseEntity.ok("Image successfully deleted.");
    }

    @GetMapping("/{id}/images/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable Long id, @PathVariable String fileName) {
        File file = new File("images/" + fileName);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Image not found");
        }

        try {
            byte[] imageData = Files.readAllBytes(file.toPath());
            String contentType = "image/jpeg";
            if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf(contentType))
                    .body(imageData);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading the image file");
        }
    }
}
