package net.cometcore.fortuity.worlds.game;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class GameWorldGenerator extends ChunkGenerator {

    public GameWorldGenerator(){}

    @Override
    public void generateSurface(WorldInfo info, @NotNull Random random, int x, int z, @NotNull ChunkData data) {
        for (int y = info.getMinHeight(); y < info.getMaxHeight(); y++) {
            data.setBlock(x, y, z, Material.AIR);
        }
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

}
