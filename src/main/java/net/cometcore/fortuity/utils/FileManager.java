package net.cometcore.fortuity.utils;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static void deleteWorldFolder(File worldFolder) {
        // Delete the world folder synchronously
        try {
            if (worldFolder.exists()) {
                Bukkit.getLogger().info("Deleting world folder: " + worldFolder.getAbsolutePath());
                deleteFolder(worldFolder);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to delete world folder: " + e.getMessage());
        }
    }

    private static void deleteFolder(File folder) throws IOException {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }
        if (!folder.delete()) {
            throw new IOException("Failed to delete folder: " + folder.getAbsolutePath());
        }
    }
}
