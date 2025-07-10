package com.bgsoftware.wildchests.command.commands;

import com.bgsoftware.wildchests.WildChestsPlugin;
import com.bgsoftware.wildchests.api.objects.chests.StorageChest;
import com.bgsoftware.wildchests.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.*;

public final class CommandSet implements ICommand {

    private static final int MAX_DISTANCE = 5;
    private static final Set<Material> MATERIALS;

    static {
        Set<Material> materials = new HashSet<>();
        for (Material material : Material.values()) {
            if (!material.isSolid() || material == Material.AIR) {
                materials.add(material);
            }
        }
        MATERIALS = Collections.unmodifiableSet(materials);
    }

    @Override
    public String getLabel() {
        return "set";
    }

    @Override
    public String getUsage() {
        return "chests set <Material> [Amount]";
    }

    @Override
    public String getPermission() {
        return "wildchests.set";
    }

    @Override
    public String getDescription() {
        return "Set the ItemStack for the chest you're looking at.";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 3;
    }

    @Override
    public void perform(WildChestsPlugin plugin, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return;
        }

        Player player = (Player) sender;

        Block targetBlock = player.getTargetBlock(MATERIALS, MAX_DISTANCE);
        if (targetBlock == null) {
            sender.sendMessage(ChatColor.RED + "You are not looking at any block within " + MAX_DISTANCE + " blocks.");
            return;
        }

        StorageChest storageChest = plugin.getChestsManager().getStorageChest(targetBlock.getLocation());
        if (storageChest == null) {
            sender.sendMessage(ChatColor.RED + "The block you're looking at is not a valid custom chest.");
            return;
        }

        Material material = Material.matchMaterial(args[1]);
        if (material == null) {
            sender.sendMessage(ChatColor.RED + "Invalid material: " + args[1]);
            return;
        }

        int amount = 1;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[2]);
                return;
            }
        }

        ItemStack itemStack = new ItemStack(material, 1);

        storageChest.setItemStack(itemStack);
        storageChest.setAmount(new BigInteger(String.valueOf(amount)));

        sender.sendMessage(ChatColor.GREEN + "Set item of the chest at "
                + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ() + " to "
                + amount + "x " + material.name());
    }

    @Override
    public List<String> tabComplete(WildChestsPlugin plugin, CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission()))
            return Collections.emptyList();

        if (args.length == 2) {
            List<String> materials = new LinkedList<>();
            String prefix = args[1].toUpperCase();
            for (Material mat : Material.values()) {
                if (mat.name().startsWith(prefix)) {
                    materials.add(mat.name());
                }
            }
            return materials;
        }

        return Collections.emptyList();
    }
}
