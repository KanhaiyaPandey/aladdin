package com.store.aladdin.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.store.aladdin.models.Collection;
import com.store.aladdin.repository.CollectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    public Collection createCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    public List<Collection> getFeaturedCollections() {
        return collectionRepository.findByFeaturedTrue();
    }

    public Collection updateCollection(String id, Collection updatedCollection) {
        Optional<Collection> existingOpt = collectionRepository.findById(id);
        if (existingOpt.isPresent()) {
            Collection existing = existingOpt.get();
            existing.setName(updatedCollection.getName());
            existing.setDescription(updatedCollection.getDescription());
            existing.setImage(updatedCollection.getImage());
            existing.setType(updatedCollection.getType());
            existing.setFeatured(updatedCollection.isFeatured());
            return collectionRepository.save(existing);
        }
        throw new RuntimeException("Collection not found with id: " + id);
    }

    public void deleteCollection(String id) {
        collectionRepository.deleteById(id);
    }
}
