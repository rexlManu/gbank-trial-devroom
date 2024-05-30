package de.rexlmanu.gbank.config.type;

import de.rexlmanu.gbank.storage.StorageType;

public record StorageConf(StorageType type, String url, String username, String password) {}
