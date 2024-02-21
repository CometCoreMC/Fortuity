package net.cometcore.fortuity.servers.gameServer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.cometcore.fortuity.FortuitySpigot;
import net.minestom.server.ServerProcess;
import net.minestom.server.advancements.AdvancementManager;
import net.minestom.server.adventure.bossbar.BossBarManager;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.exception.ExceptionManager;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.item.armor.TrimManager;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.PacketProcessor;
import net.minestom.server.network.socket.Server;
import net.minestom.server.recipe.RecipeManager;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.snapshot.*;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.thread.ThreadDispatcher;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.PropertyUtils;
import net.minestom.server.utils.collection.MappedCollection;
import net.minestom.server.world.DimensionTypeManager;
import net.minestom.server.world.biomes.BiomeManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"UnstableApiUsage", "NonExtendableApiUsage"})
final class GameServerProcess implements ServerProcess {
   private static final Boolean SHUTDOWN_ON_SIGNAL = PropertyUtils.getBoolean("minestom.shutdown-on-signal", true);
    private final ExceptionManager exception = new ExceptionManager();
    private final ConnectionManager connection = new ConnectionManager();
    private final PacketListenerManager packetListener = new PacketListenerManager();
    private final PacketProcessor packetProcessor;
    private final InstanceManager instance;
    private final BlockManager block;
    private final CommandManager command;
    private final RecipeManager recipe;
    private final TeamManager team;
    private final GlobalEventHandler eventHandler;
    private final SchedulerManager scheduler;
    private final BenchmarkManager benchmark;
    private final DimensionTypeManager dimension;
    private final BiomeManager biome;
    private final AdvancementManager advancement;
    private final BossBarManager bossBar;
    private final TagManager tag;
    private final TrimManager trim;
    private final Server server;
    private final ThreadDispatcher<Chunk> dispatcher;
    @SuppressWarnings("NonExtendableApiUsage")
    private final ServerProcess.Ticker ticker;
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();
    private final GameServer gameServer;

    public GameServerProcess(GameServer server) throws IOException {
        this.gameServer = server;
        this.packetProcessor = new PacketProcessor(this.packetListener);
        this.instance = new InstanceManager();
        this.block = new BlockManager();
        this.command = new CommandManager();
        this.recipe = new RecipeManager();
        this.team = new TeamManager();
        this.eventHandler = new GlobalEventHandler();
        this.scheduler = new SchedulerManager();
        this.benchmark = new BenchmarkManager();
        this.dimension = new DimensionTypeManager();
        this.biome = new BiomeManager();
        this.advancement = new AdvancementManager();
        this.bossBar = new BossBarManager();
        this.tag = new TagManager();
        this.trim = new TrimManager();
        this.server = new Server(this.packetProcessor);
        this.dispatcher = ThreadDispatcher.singleThread();
        this.ticker = new GameTicker();
    }

    public @NotNull ConnectionManager connection() {
        return this.connection;
    }

    public @NotNull InstanceManager instance() {
        return this.instance;
    }

    public @NotNull BlockManager block() {
        return this.block;
    }

    public @NotNull CommandManager command() {
        return this.command;
    }

    public @NotNull RecipeManager recipe() {
        return this.recipe;
    }

    public @NotNull TeamManager team() {
        return this.team;
    }

    public @NotNull GlobalEventHandler eventHandler() {
        return this.eventHandler;
    }

    public @NotNull SchedulerManager scheduler() {
        return this.scheduler;
    }

    public @NotNull BenchmarkManager benchmark() {
        return this.benchmark;
    }

    public @NotNull DimensionTypeManager dimension() {
        return this.dimension;
    }

    public @NotNull BiomeManager biome() {
        return this.biome;
    }

    public @NotNull AdvancementManager advancement() {
        return this.advancement;
    }

    public @NotNull BossBarManager bossBar() {
        return this.bossBar;
    }

    public @NotNull TagManager tag() {
        return this.tag;
    }

    public @NotNull TrimManager trim() {
        return this.trim;
    }

    public @NotNull ExceptionManager exception() {
        return this.exception;
    }

    public @NotNull PacketListenerManager packetListener() {
        return this.packetListener;
    }

    public @NotNull PacketProcessor packetProcessor() {
        return this.packetProcessor;
    }

    public @NotNull Server server() {
        return this.server;
    }

    public @NotNull ThreadDispatcher<Chunk> dispatcher() {
        return this.dispatcher;
    }

    @SuppressWarnings("NonExtendableApiUsage")
    @NotNull
    public ServerProcess.@NotNull Ticker ticker() {
        return this.ticker;
    }

    public void start(@NotNull SocketAddress socketAddress) {
        if (!this.started.compareAndSet(false, true)) {
            throw new IllegalStateException("Server already started");
        } else {
            FortuitySpigot.getLOGGER().info("Starting " + gameServer.getBrandName() + " server.");

            try {
                this.server.init(socketAddress);
            } catch (IOException var3) {
                this.exception.handleException(var3);
                throw new RuntimeException(var3);
            }

            this.server.start();
            FortuitySpigot.getLOGGER().info(gameServer.getBrandName() + " server started successfully.");
            if (SHUTDOWN_ON_SIGNAL) {
                Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            }

        }
    }

    public void stop() {
        if (this.stopped.compareAndSet(false, true)) {
            FortuitySpigot.getLOGGER().info("Stopping " + gameServer.getBrandName() + " server.");
            this.scheduler.shutdown();
            this.connection.shutdown();
            this.server.stop();
            FortuitySpigot.getLOGGER().info("Shutting down all thread pools.");
            this.benchmark.disable();
            this.dispatcher.shutdown();
            FortuitySpigot.getLOGGER().info(gameServer.getBrandName() + " server stopped successfully.");
        }
    }

    public boolean isAlive() {
        return this.started.get() && !this.stopped.get();
    }

    public @NotNull ServerSnapshot updateSnapshot(@NotNull SnapshotUpdater updater) {
        List<AtomicReference<InstanceSnapshot>> instanceRefs = new ArrayList<>();
        Int2ObjectOpenHashMap<AtomicReference<EntitySnapshot>> entityRefs = new Int2ObjectOpenHashMap<>();

        for (Instance instance : this.instance.getInstances()) {
            instanceRefs.add(updater.reference(instance));

            for (Entity entity : instance.getEntities()) {
                entityRefs.put(entity.getEntityId(), updater.reference(entity));
            }
        }

        return new SnapshotImpl.Server(MappedCollection.plainReferences(instanceRefs), entityRefs);
    }

    @SuppressWarnings("NonExtendableApiUsage")
    private final class GameTicker implements ServerProcess.Ticker {
        private GameTicker() {
        }

        public void tick(long nanoTime) {
            long msTime = System.currentTimeMillis();
            GameServerProcess.this.scheduler().processTick();
            GameServerProcess.this.connection().tick(msTime);
            this.serverTick(msTime);
            PacketUtils.flush();
            GameServerProcess.this.server().tick();
            double acquisitionTimeMs = (double) Acquirable.resetAcquiringTime() / 1000000.0;
            double tickTimeMs = (double)(System.nanoTime() - nanoTime) / 1000000.0;
            TickMonitor tickMonitor = new TickMonitor(tickTimeMs, acquisitionTimeMs);
            EventDispatcher.call(new ServerTickMonitorEvent(tickMonitor));
        }

        private void serverTick(long tickStart) {

            for (Instance instance : GameServerProcess.this.instance().getInstances()) {
                try {
                    instance.tick(tickStart);
                } catch (Exception var6) {
                    GameServerProcess.this.exception().handleException(var6);
                }
            }

            GameServerProcess.this.dispatcher().updateAndAwait(tickStart);
            long tickTime = System.currentTimeMillis() - tickStart;
            GameServerProcess.this.dispatcher().refreshThreads(tickTime);
        }
    }
}
