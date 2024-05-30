package de.rexlmanu.gbank.notification;

import java.time.LocalDateTime;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public record Notification(UUID receiverId, Component message, LocalDateTime createdAt) {}
