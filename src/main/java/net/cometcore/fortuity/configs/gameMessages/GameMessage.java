package net.cometcore.fortuity.configs.gameMessages;

import java.util.HashMap;
import java.util.Map;

public class GameMessage {
    private final String name;
    private final Map<String, String> messages;

    public GameMessage(String name) {
        this.name = name;
        this.messages = new HashMap<>();
    }

    public void setMessage(String key, String message) {
        messages.put(key, message);
    }

    public String getMessage(String key) {
        return messages.get(key);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getMessages() {
        return messages;
    }
}
