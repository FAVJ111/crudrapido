package com.example.crudrapido.config;

import org.springframework.stereotype.Component;

@Component
public class SyncState {
    private boolean synced = false;

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }
}
