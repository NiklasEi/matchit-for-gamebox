package me.nikl.matchit;

import me.nikl.gamebox.GameBox;
import me.nikl.gamebox.GameBoxSettings;
import me.nikl.gamebox.game.Game;
import me.nikl.gamebox.game.GameSettings;
import me.nikl.gamebox.utility.ItemStackUtility;
import me.nikl.gamebox.utility.StringUtility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niklas Eicker
 */
public class MatchIt extends Game {
    private List<ItemStack> pairItems;
    private ItemStack cover, border;

    public MatchIt(GameBox gameBox) {
        super(gameBox, GameBox.MODULE_MATCHIT);
    }

    private void setDefaultItems() {
        try {
            String defaultConfigName = "games/matchit/config.yml";
            FileConfiguration defaultConfig = YamlConfiguration
                    .loadConfiguration(new InputStreamReader(gameBox.getResource(defaultConfigName), "UTF-8"));
            // load default items from file
            ConfigurationSection itemsSec = defaultConfig.getConfigurationSection("items");
            ItemStack itemStack;
            for (String key : itemsSec.getKeys(false)) {
                if (!itemsSec.isConfigurationSection(key))
                    continue;
                itemStack = loadItem(itemsSec.getConfigurationSection(key));
                if (itemStack != null && !pairItems.contains(itemStack))
                    pairItems.add(itemStack.clone());
            }
        } catch (UnsupportedEncodingException e2) {
            gameBox.getLogger().warning("Failed to load default config file for: " + module.getModuleID());
            e2.printStackTrace();
        }
    }

    private ItemStack loadItem(ConfigurationSection itemSection) {
        ItemStack toReturn = ItemStackUtility.loadItem(itemSection);
        if (toReturn == null) {
            warn(" missing or invalid 'matData' config-key: " + itemSection.getName());
            return null;
        }
        if (itemSection.isBoolean("glow")) {
            toReturn = nms.addGlow(toReturn);
        }
        if (!GameBoxSettings.version1_8) {
            ItemMeta meta = toReturn.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            toReturn.setItemMeta(meta);
        }
        return toReturn;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void init() {
        pairItems = new ArrayList<>();
        if (!config.isConfigurationSection("items")) {
            gameBox.warning(" there are no items defined for this game...");
            gameBox.warning(" falling back to default...");
            setDefaultItems();
        } else {
            ConfigurationSection itemsSec = config.getConfigurationSection("items");
            ItemStack itemStack;
            for (String key : itemsSec.getKeys(false)) {
                if (!itemsSec.isConfigurationSection(key))
                    continue;

                itemStack = loadItem(itemsSec.getConfigurationSection(key));

                if (itemStack != null) pairItems.add(itemStack.clone());
            }
            if (pairItems.isEmpty()) {
                warn(" there are no items defined!");
                warn("    falling back to default...");
                setDefaultItems();
            }
        }
        if (config.isConfigurationSection("coverItem")) {
            cover = loadItem(config.getConfigurationSection("coverItem"));
        }
        if (cover == null) {
            warn(" missing or invalid cover item in %config%");
            warn("    falling back to default...");
            cover = ItemStackUtility.getItemStack("STAINED_GLASS_PANE:3");
            ItemMeta meta = cover.getItemMeta();
            meta.setDisplayName(StringUtility.color("&1Click to uncover"));
            cover.setItemMeta(meta);
        }
        if (config.isConfigurationSection("borderItem")) {
            border = loadItem(config.getConfigurationSection("borderItem"));
        }
        if (border == null) {
            warn(" missing or invalid border item in %config%");
            warn("    falling back to default...");
            border = ItemStackUtility.getItemStack("STAINED_GLASS_PANE:15");
            ItemMeta meta = border.getItemMeta();
            meta.setDisplayName("&r");
            border.setItemMeta(meta);
        }
    }

    @Override
    public void loadSettings() {
        gameSettings.setGameType(GameSettings.GameType.SINGLE_PLAYER);
        gameSettings.setGameGuiSize(54);
        gameSettings.setHandleClicksOnHotbar(false);
    }

    @Override
    public void loadLanguage() {
        this.gameLang = new MILanguage(this);
    }

    @Override
    public void loadGameManager() {
        this.gameManager = new MIGameManager(this);
    }

    List<ItemStack> getPairItems() {
        return pairItems;
    }

    ItemStack getCover() {
        return cover;
    }

    ItemStack getBorder() {
        return border;
    }

    public enum GridSize {
        BIG(54), MIDDLE(4 * 7), SMALL(2 * 5);

        private int size;

        GridSize(int size) {
            this.size = size;
        }

        int getSize() {
            return size;
        }
    }
}
