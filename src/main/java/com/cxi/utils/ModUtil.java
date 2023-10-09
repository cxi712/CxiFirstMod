package com.cxi.utils;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static com.cxi.CxiFirstMod.MOD_ID;

/**
 * @author 常袭
 * @version 1.0
 * @description: TODO
 * @date 2023/10/9 17:34
 */
public class ModUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item registerItem(Item item, ItemGroup... itemGroups) {
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, item.getName().getString()), item);
    }

    public static Item addToGroups(Item item, ItemGroup... itemGroups) {
        for (ItemGroup itemGroup : itemGroups) {
            RegistryKey<ItemGroup> registryKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, itemGroup.getDisplayName().getString()));
            ItemGroupEvents.modifyEntriesEvent(registryKey).register(entries -> entries.add(item));
        }
        return item;
    }

    public static ItemGroup registerItemsGroup(ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, itemGroup.getDisplayName().getString()), itemGroup);
    }

    public static int blockChain(PlayerEntity player, World world, Block block, BlockPos blockPos) {
        int max = 100;
        ArrayList<BlockPos> blockPoss1 = getBlockChain(world, block, blockPos, new ArrayList<>(), new ArrayList<>(), 0);
        ArrayList<BlockPos> blockPoss2 = new ArrayList<>();
        blockPoss1.forEach(blockPos1 -> {
            boolean a = true;
            for (BlockPos blockPos2 : blockPoss2) {
                Pos pos1 = new Pos(blockPos1);
                Pos pos2 = new Pos(blockPos2);
                Pos pos0 = new Pos(player.getPos());
                if (getDistance(pos0, pos1) < getDistance(pos0, pos2)) {
                    blockPoss2.add(blockPoss2.indexOf(blockPos2), blockPos1);
                    a = false;
                    break;
                }
            }
            if (a) blockPoss2.add(blockPos1);
        });
        for (int i = 0; i < max && i < blockPoss2.size(); i++) {
            world.breakBlock(blockPoss2.get(i), true);
        }
        return Math.min(blockPoss2.size(), max);
    }

    public static ArrayList<BlockPos> getBlockChain(World world, Block block, BlockPos blockPos, ArrayList<BlockPos> blockPoss, ArrayList<Integer> come, int depth) {
        BlockPos[] blockPoss1 = {
                blockPos.up(),
                blockPos.down(),
                blockPos.east(),
                blockPos.west(),
                blockPos.north(),
                blockPos.south()
        };
        depth++;
        for (int i = 0; i < blockPoss1.length; i++) {
            if (depth <= 8 && !come.contains(i) && world.getBlockState(blockPoss1[i]).getBlock().equals(block)) {
                if (!blockPoss.contains(blockPoss1[i])) blockPoss.add(blockPoss1[i]);
                ArrayList<Integer> c = (ArrayList<Integer>) come.clone();
                c.add(i % 2 == 0 ? i + 1 : i - 1);
                ModUtil.getBlockChain(world, block, blockPoss1[i], blockPoss, c, depth);
            }
        }
        return blockPoss;
    }

    public static double getDistance(Pos blockPos1, Pos blockPos2) {
        return Math.sqrt(Math.pow(blockPos1.x - blockPos2.x, 2) + Math.pow(blockPos1.y - blockPos2.y, 2) + Math.pow(blockPos1.z - blockPos2.z, 2));
    }

    static class Pos {
        double x;
        double y;
        double z;

        public Pos(Position pos) {
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        }

        public Pos(BlockPos pos) {
            x = pos.getX();
            y = pos.getY();
            z = pos.getZ();
        }
    }
}
