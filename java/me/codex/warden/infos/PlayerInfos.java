package me.codex.warden.infos;

import me.codex.warden.Main;
import me.codex.warden.storage.yml.DefaultConfigManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerInfos {

    public static final String TABLE = "player_infos";

    public void update(Player player) {
        Main.getInstance().cache.addInfo(player.getName(), player.getUniqueId());

    }

    public boolean exist(String playerName) {
        return Main.getInstance().cache.existInfo(playerName);
    }

    public UUID getUuid(String playerName) {
        return Main.getInstance().cache.getUuid(playerName);
    }

}
