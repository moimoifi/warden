package me.codex.warden;

import me.codex.warden.bans.BanManager;
import me.codex.warden.cache.Cache;
import me.codex.warden.commands.Commands;
import me.codex.warden.database.MySQL;
import me.codex.warden.infos.PlayerInfos;
import me.codex.warden.listeners.PlayerChat;
import me.codex.warden.listeners.PlayerJoin;
import me.codex.warden.mutes.MuteManager;
import me.codex.warden.storage.yml.BanYML;
import me.codex.warden.storage.yml.DefaultConfigManager;
import me.codex.warden.storage.yml.InfosYML;
import me.codex.warden.storage.yml.MuteYML;
import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private BasicDataSource connectionPool;
    private MySQL mysql;
    public BanManager banManager = new BanManager();
    public MuteManager muteManager = new MuteManager();
    public PlayerInfos playerInfos = new PlayerInfos();
    public Cache cache = new Cache();
    public DefaultConfigManager configManager;
    public BanYML banYML;
    public InfosYML infosYML;
    public MuteYML muteYML;

    public final String prefix = "§c[warden]§r ";

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.configManager = new DefaultConfigManager(this);
        this.configManager.loadConfig();

        banYML = new BanYML(this);
        infosYML = new InfosYML(this);
        muteYML = new MuteYML(this);

        if(configManager.USE_DATABASE)
            initConnection();

        registerListeners();
        registerCommands();

        cache.update();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.configManager.loadConfig();

        cache.update();

        super.onDisable();
    }

    private void registerCommands() {
        getCommand("ban").setExecutor(new Commands());
        getCommand("unban").setExecutor(new Commands());
        getCommand("mute").setExecutor(new Commands());
        getCommand("unmute").setExecutor(new Commands());
        getCommand("warden").setExecutor(new Commands());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerChat(), this);
    }

    public void initConnection() {
        if(connectionPool != null) {
            try {
                connectionPool.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        connectionPool = new BasicDataSource();
        connectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        connectionPool.setUsername(configManager.USERNAME);
        connectionPool.setPassword(configManager.PASSWORD);
        connectionPool.setUrl("jdbc:mysql://" + configManager.DB_URL + ":" + configManager.PORT + "/" + configManager.DB_NAME + "?autoReconnect=true");
        connectionPool.setInitialSize(1);
        connectionPool.setMaxTotal(10);
        mysql = new MySQL(connectionPool);
        mysql.createTables();
    }

    public MySQL getMysql() {
        return mysql;
    }

    public static Main getInstance() {
        return INSTANCE;
    }
}
