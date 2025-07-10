package com.bgsoftware.wildchests.command.commands;

import com.bgsoftware.wildchests.WildChestsPlugin;
import com.bgsoftware.wildchests.api.objects.chests.StorageChest;
import com.bgsoftware.wildchests.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.*;

public final class CommandAdd implements ICommand {

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
        return "add";
    }

    @Override
    public String getUsage() {
        return "chests add <Amount>";
    }

    @Override
    public String getPermission() {
        return "wildchests.add";
    }

    @Override
    public String getDescription() {
        return "Add to the amount of the item in the chest you're looking at.";
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
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

        if (storageChest.getItemStack() == null || storageChest.getItemStack().getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "This chest has no item set. Use /chests set first.");
            return;
        }

        int addAmount;
        try {
            addAmount = Integer.parseInt(args[1]);
            if (addAmount <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount: " + args[1]);
            return;
        }

        BigInteger newAmount = storageChest.getAmount().add(BigInteger.valueOf(addAmount));
        storageChest.setAmount(newAmount);

        sender.sendMessage(ChatColor.GREEN + "Added " + addAmount + " to chest at "
                + targetBlock.getX() + "," + targetBlock.getY() + "," + targetBlock.getZ()
                + ". New total: " + newAmount + "x " + storageChest.getItemStack().getType().toString());
    }

    @Override
    public List<String> tabComplete(WildChestsPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
