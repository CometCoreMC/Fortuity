package net.cometcore.fortuity.servers.gameServer;

import net.minestom.server.ServerProcess;
import net.minestom.server.advancements.AdvancementManager;
import net.minestom.server.adventure.bossbar.BossBarManager;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.exception.ExceptionManager;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.item.armor.TrimManager;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.packet.server.common.PluginMessagePacket;
import net.minestom.server.network.packet.server.play.ServerDifficultyPacket;
import net.minestom.server.network.socket.Server;
import net.minestom.server.recipe.RecipeManager;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.thread.TickSchedulerThread;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.validate.Check;
import net.minestom.server.world.Difficulty;
import net.minestom.server.world.DimensionTypeManager;
import net.minestom.server.world.biomes.BiomeManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@SuppressWarnings("UnstableApiUsage")
public class GameServer {
    public static final String VERSION_NAME = "1.20.4";
    public static final int PROTOCOL_VERSION = 765;
    public static final String THREAD_NAME_BENCHMARK = "Ms-Benchmark";
    public static final String THREAD_NAME_TICK_SCHEDULER = "Ms-TickScheduler";
    public static final String THREAD_NAME_TICK = "Ms-Tick";

    private volatile ServerProcess serverProcess;
    private int compressionThreshold = 256;
    private String brandName = "CometCore";
    private Difficulty difficulty = Difficulty.NORMAL;

    public GameServer() {
    }

    public GameServer init() {
        updateProcess();
        return new GameServer();
    }

    @ApiStatus.Internal
    public void updateProcess() {
        try {
            serverProcess = new GameServerProcess(this);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    public @NotNull String getBrandName() {
        return brandName;
    }

    public void setBrandName(@NotNull String brandName) {
        this.brandName = brandName;
        PacketUtils.broadcastPlayPacket(PluginMessagePacket.getBrandPacket());
    }

    public @NotNull Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(@NotNull Difficulty difficulty) {
        this.difficulty = difficulty;
        PacketUtils.broadcastPlayPacket(new ServerDifficultyPacket(difficulty, true));
    }

    @ApiStatus.Experimental
    public @UnknownNullability ServerProcess process() {
        return serverProcess;
    }

    public @NotNull GlobalEventHandler getGlobalEventHandler() {
        return serverProcess.eventHandler();
    }

    public @NotNull PacketListenerManager getPacketListenerManager() {
        return serverProcess.packetListener();
    }

    public @NotNull InstanceManager getInstanceManager() {
        return serverProcess.instance();
    }

    public @NotNull BlockManager getBlockManager() {
        return serverProcess.block();
    }

    public @NotNull CommandManager getCommandManager() {
        return serverProcess.command();
    }

    public @NotNull RecipeManager getRecipeManager() {
        return serverProcess.recipe();
    }

    public @NotNull TeamManager getTeamManager() {
        return serverProcess.team();
    }

    public @NotNull SchedulerManager getSchedulerManager() {
        return serverProcess.scheduler();
    }

    public @NotNull BenchmarkManager getBenchmarkManager() {
        return serverProcess.benchmark();
    }

    public @NotNull ExceptionManager getExceptionManager() {
        return serverProcess.exception();
    }

    public @NotNull ConnectionManager getConnectionManager() {
        return serverProcess.connection();
    }

    public @NotNull BossBarManager getBossBarManager() {
        return serverProcess.bossBar();
    }

    public @NotNull PacketProcessor getPacketProcessor() {
        return serverProcess.packetProcessor();
    }

    public boolean isStarted() {
        return serverProcess.isAlive();
    }

    public boolean isStopping() {
        return !isStarted();
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public void setCompressionThreshold(int compressionThreshold) {
        Check.stateCondition(serverProcess != null && serverProcess.isAlive(), "The compression threshold cannot be changed after the server has been started.");
        this.compressionThreshold = compressionThreshold;
    }

    public DimensionTypeManager getDimensionTypeManager() {
        return serverProcess.dimension();
    }

    public BiomeManager getBiomeManager() {
        return serverProcess.biome();
    }

    public AdvancementManager getAdvancementManager() {
        return serverProcess.advancement();
    }

    public TagManager getTagManager() {
        return serverProcess.tag();
    }

    public TrimManager getTrimManager() {
        return serverProcess.trim();
    }

    public Server getServer() {
        return serverProcess.server();
    }

    public void start(@NotNull SocketAddress address) {
        serverProcess.start(address);
        (new TickSchedulerThread(serverProcess)).start();
    }

    public void start(@NotNull String address, int port) {
        this.start(new InetSocketAddress(address, port));
    }

    public void stopCleanly() {
        serverProcess.stop();
    }

}
