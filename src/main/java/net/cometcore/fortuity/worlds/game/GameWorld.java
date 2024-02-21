package net.cometcore.fortuity.worlds.game;

import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.logging.log4j.LogManager.getLogger;

public class GameWorld {

    public World world;
    private final WorldCreator worldCreator;
    private final Set<Location> spawnLocations = new HashSet<>();

    public static final String name = "world_fortuity";
    private static List<Block> foundation;

    public GameWorld(World.Environment environment){

            foundation = new ArrayList<>();

            worldCreator = new WorldCreator(name);
            worldCreator.environment(environment);
            worldCreator.generator(new GameWorldGenerator()); // let's create a class for generator of void
            worldCreator.generateStructures(false);
            worldCreator.type(WorldType.FLAT);
            worldCreator.createWorld();

            world = Bukkit.createWorld(worldCreator);

            if(world != null) {
                world.setAutoSave(false);
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            }

            preloadChunks(world, 0, 0, 0);
            getLogger().info("World 'fortuity' created!");
    }

    public static void preloadChunks(World world, int centerX, int centerZ, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                world.loadChunk(x, z, true);
            }
        }
    }

    public void generateWorld(int orbitingBlocks){
        // Generate void world with center block
        int centerX = 0; // Center X coordinate
        int centerY = world.getMaxHeight() / 2; // Center Y coordinate
        int centerZ = 0; // Center Z coordinate
        world.getBlockAt(centerX, centerY, centerZ).setType(Material.AIR);

        // Generate orbiting pillars
        double angleIncrement = 2 * Math.PI / (double)orbitingBlocks;
        double radius = 6; // Radius of orbit
        for (int i = 0; i < orbitingBlocks; i++) {
            double angle = i * angleIncrement;
            int x = (int) Math.round(8 + radius * Math.cos(angle));
            int z = (int) Math.round(8 + radius * Math.sin(angle));
            generatePillar(world, x, centerY, z);
        }
    }

    private void generatePillar(World world, int x, int y, int z) {
        for (int dy = 0; dy <= y; dy++) {

            world.getBlockAt(x, dy, z).setType(Material.BEDROCK);
            foundation.add(world.getBlockAt(x, dy, z));
            spawnLocations.add(new Location(world, x, y, z));
        }
    }

    public void reset(){
        for(Block block : foundation){
            block.setType(Material.AIR);
        }
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations.stream().toList();
    }
}
