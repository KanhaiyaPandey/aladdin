package com.store.aladdin.services;

import org.springframework.stereotype.Service;
import com.store.aladdin.models.Store;
import com.store.aladdin.repository.StoreRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public Store getStoreSettings() {
        List<Store> stores = storeRepository.findAll();
        if (stores.isEmpty()) {
            Store newStore = new Store();
            newStore.setStoreName("Aladdin Store");
            return storeRepository.save(newStore);
        }
        return stores.get(0);
    }

    public Store updateStoreSettings(Store updatedStore) {
        Store store = getStoreSettings();
        store.setStoreName(updatedStore.getStoreName());
        store.setEmail(updatedStore.getEmail());
        store.setPhone(updatedStore.getPhone());
        store.setPrivacyPolicy(updatedStore.getPrivacyPolicy());
        store.setReturnExchangePolicy(updatedStore.getReturnExchangePolicy());
        store.setGlobalBanner(updatedStore.getGlobalBanner());
        store.setGlobalDescription(updatedStore.getGlobalDescription());
        return storeRepository.save(store);
    }
}
