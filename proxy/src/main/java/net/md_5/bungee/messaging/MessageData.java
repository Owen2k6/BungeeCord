package net.md_5.bungee.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageData {
    private String username;
    private String targetServer;
}
