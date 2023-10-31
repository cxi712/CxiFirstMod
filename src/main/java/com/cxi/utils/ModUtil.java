package com.cxi.utils;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
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

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
    }

    public static Item addToGroups(Item item, ItemGroup... itemGroups) {
        for (ItemGroup itemGroup : itemGroups) {
            RegistryKey<ItemGroup> registryKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MOD_ID, itemGroup.getDisplayName().getString()));
            addToGroups(item, registryKey);
        }
        return item;
    }

    public static Item addToGroups(Item item, RegistryKey<ItemGroup> itemGroup) {
        ItemGroupEvents.modifyEntriesEvent(itemGroup).register(entries -> entries.add(item));
        return item;
    }

    public static ItemGroup registerItemsGroup(ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier(MOD_ID, itemGroup.getDisplayName().getString()), itemGroup);
    }

    public static int blockChain(PlayerEntity player, World world, Block block, BlockPos blockPos) {
        int max = 1000;
        max -= 1;
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
        int j = 0;
        for (int i = 0; i < max && i < blockPoss2.size(); i++) {
            world.breakBlock(blockPoss2.get(i), true);
            j++;
        }
        return j;
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
    //删除物品
    public static void removeItemsFromInventory(PlayerEntity player, ItemStack item, int count) {
        Inventory inventory = player.getInventory();
        int remainingCount = count;

        // 遍历玩家背包中的物品栏
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);

            // 检查物品是否匹配
            if (stack.getItem().equals(item.getItem())) {
                int stackCount = stack.getCount();

                // 如果当前物品栈数量大于需移除的数量
                if (stackCount > remainingCount) {
                    stack.decrement(remainingCount);
                    remainingCount = 0;
                    break; // 结束循环
                }

                // 如果当前物品栈数量等于或小于需移除的数量
                else {
                    inventory.setStack(slot, ItemStack.EMPTY);
                    remainingCount -= stackCount;
                }
            }
        }

        // 更新主手中的物品栈
        ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);
        if (heldItem.getItem().equals(item.getItem())) {
            int heldItemCount = heldItem.getCount();
            if (heldItemCount > remainingCount) {
                heldItem.decrement(remainingCount);
            } else {
                player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
    }

    //判断是否可以放下物品
    public static boolean canFitItemsInInventory(PlayerEntity player, ItemStack item, int count) {
        Inventory inventory = player.getInventory();
        int remainingCount = count;

        // 遍历玩家背包中的物品栏
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);

            // 如果物品栏为空，可以存储所有数量的物品
            if (stack.isEmpty()) {
                return true;
            }

            // 如果物品与要存储的物品匹配，计算还能存储的数量
            if (stack.getItem().equals(item.getItem())) {
                int maxStackSize = stack.getMaxCount();
                int stackCount = stack.getCount();
                int freeSpace = maxStackSize - stackCount;
                remainingCount -= freeSpace;
                if (remainingCount <= 0) {
                    return true;
                }
            }
        }

        // 检查背包是否可以存储剩余的物品
        ItemStack emptyStack = ItemStack.EMPTY;
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);

            if (stack.isEmpty() || stack == emptyStack) {
                return true;
            }
        }

        // 背包无法存储所有物品
        return false;
    }

    //放入物品
    public static void addItemToInventory(PlayerEntity player, ItemStack item, int count) {
        ItemStack stack = new ItemStack(item.getItem(), count);
        DefaultedList<ItemStack> inventory = player.getInventory().main;

        // 遍历玩家背包中的物品栏
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack slotStack = inventory.get(slot);

            // 如果物品栏为空，则将物品栈添加到该位置
            if (slotStack.isEmpty()) {
                inventory.set(slot, stack);
                return;
            }

            // 如果物品栈与要添加的物品栈匹配并且没有达到最大堆叠数量，则堆叠物品
            if (ItemStack.areItemsEqual(slotStack, stack) && ItemStack.areItemsEqual(slotStack, stack) && slotStack.getCount() < slotStack.getMaxCount()) {
                int remainingCount = count - slotStack.getCount();
                int stackCount = Math.min(stack.getMaxCount(), remainingCount);
                slotStack.increment(stackCount);
                stack.decrement(stackCount);

                // 如果物品已经全部添加完毕，则结束方法
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        // 如果背包已满，则将剩余的物品掉落在玩家脚下
        player.dropItem(stack, false);
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
