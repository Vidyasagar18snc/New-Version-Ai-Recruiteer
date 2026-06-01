package com.Vendor.repository;

import com.Vendor.model.Panel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanelRepository extends MongoRepository<Panel, String> {

    List<Panel> findByRoleContainingIgnoreCase(String role);
    Optional<Panel> findByEmailAndPassword(
            String email,
            String password
    );
}