package com.example.internship.repositories;

import com.example.internship.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryRepo extends JpaRepository<Entry, Long> {
    List<Entry> findByUserId(Long userId);
}
