package com.cxi;

import com.cxi.entities.client.ChomperRenderer;
import com.cxi.entities.custom.ChomperEntity;
import com.cxi.utils.IOUtil;
import com.cxi.utils.ModUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class CxiFirstMod implements ModInitializer {
    public static final String MOD_ID = "cxifirstmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        try {

            LOGGER.info("CxiFirstMod loaded!");
            PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
                String text = IOUtil.readStringProp(IOUtil.root + "chainSwitch", player.getUuidAsString(), "on");
                if (text.equals("on")) {
                    JsonArray jsonArray = new Gson().fromJson(IOUtil.readStringProp(IOUtil.root + "chain", player.getUuidAsString(), "[]"), JsonArray.class);
                    if (!jsonArray.isEmpty() && new Gson().toJson(jsonArray).contains("\"" + state.getBlock().getName().getString() + "\"")) {
                        int a = ModUtil.blockChain(player, world, state.getBlock(), pos) + 1;
                        if (a > 1) player.sendMessage(Text.literal("成功连锁" + a + "个方块"));
                    }
                }
            });
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                        CommandManager.literal("cxi")
                                .then(CommandManager.literal("chain")
                                        .then(CommandManager.literal("add").then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                                .executes(context -> {
                                                    BlockPos blockPos = BlockPosArgumentType.getBlockPos(context, "position");
                                                    Block block = context.getSource().getWorld().getBlockState(blockPos).getBlock();
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    context.getSource().sendMessage(Text.literal(block.toString()));
                                                    if (block.equals(Blocks.AIR)) {
                                                        context.getSource().sendMessage(Text.literal("请将十字准心对准要添加的实体方块"));
                                                    } else {
                                                        if (player != null) {
                                                            JsonArray jsonArray = new Gson().fromJson(IOUtil.readStringProp(IOUtil.root + "chain", player.getUuidAsString(), "[]"), JsonArray.class);
                                                            if (!jsonArray.isEmpty() && new Gson().toJson(jsonArray).contains("\"" + block.getName().getString() + "\"")) {
                                                                context.getSource().sendMessage(Text.literal("已经存在此方块"));
                                                            } else {
                                                                jsonArray.add(block.getName().getString());
                                                                IOUtil.writeStringProp(IOUtil.root + "chain", player.getUuidAsString(), new Gson().toJson(jsonArray));
                                                                context.getSource().sendMessage(Text.literal("添加成功"));
                                                            }
                                                        }
                                                    }
                                                    return 1;
                                                })))
                                        .then(CommandManager.literal("remove").then(CommandManager.argument("position", BlockPosArgumentType.blockPos())
                                                .executes(context -> {
                                                    BlockPos blockPos = BlockPosArgumentType.getBlockPos(context, "position");
                                                    Block block = context.getSource().getWorld().getBlockState(blockPos).getBlock();
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    context.getSource().sendMessage(Text.literal(block.toString()));
                                                    if (block.equals(Blocks.AIR)) {
                                                        context.getSource().sendMessage(Text.literal("请将十字准心对准要移除的实体方块"));
                                                    } else {
                                                        if (player != null) {
                                                            JsonArray jsonArray = new Gson().fromJson(IOUtil.readStringProp(IOUtil.root + "chain", player.getUuidAsString(), "[]"), JsonArray.class);
                                                            if (jsonArray.isEmpty() || !new Gson().toJson(jsonArray).contains("\"" + block.getName().getString() + "\"")) {
                                                                context.getSource().sendMessage(Text.literal("不存在此方块"));
                                                            } else {
                                                                for (int i = 0; i < jsonArray.size(); i++) {
                                                                    if (block.getName().getString().equals(jsonArray.get(i).getAsString())) {
                                                                        jsonArray.remove(i);
                                                                    }
                                                                }
                                                                IOUtil.writeStringProp(IOUtil.root + "chain", player.getUuidAsString(), new Gson().toJson(jsonArray));
                                                                context.getSource().sendMessage(Text.literal("移除成功"));
                                                            }
                                                        }
                                                    }
                                                    return 1;
                                                })))
                                        .then(CommandManager.literal("list")
                                                .executes(context -> {
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        JsonArray jsonArray = new Gson().fromJson(IOUtil.readStringProp(IOUtil.root + "chain", player.getUuidAsString(), "[]"), JsonArray.class);
                                                        if (jsonArray.isEmpty()) {
                                                            context.getSource().sendMessage(Text.literal("请先添加方块"));
                                                        } else
                                                            jsonArray.forEach(block -> context.getSource().sendMessage(Text.literal(block.getAsString())));
                                                    }
                                                    return 1;
                                                }))
                                        .then(CommandManager.literal("on")
                                                .executes(context -> {
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        String text = IOUtil.readStringProp(IOUtil.root + "chainSwitch", player.getUuidAsString(), "on");
                                                        if (text.equals("on")) {
                                                            context.getSource().sendMessage(Text.literal("连锁已经打开"));
                                                        } else {
                                                            IOUtil.writeStringProp(IOUtil.root + "chainSwitch", player.getUuidAsString(), "on");
                                                            context.getSource().sendMessage(Text.literal("连锁打开成功"));
                                                        }
                                                    }
                                                    return 1;
                                                }))
                                        .then(CommandManager.literal("off")
                                                .executes(context -> {
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        String text = IOUtil.readStringProp(IOUtil.root + "chainSwitch", player.getUuidAsString(), "on");
                                                        if (text.equals("off")) {
                                                            context.getSource().sendMessage(Text.literal("连锁已经关闭"));
                                                        } else {
                                                            IOUtil.writeStringProp(IOUtil.root + "chainSwitch", player.getUuidAsString(), "off");
                                                            context.getSource().sendMessage(Text.literal("连锁关闭成功"));
                                                        }
                                                    }
                                                    return 1;
                                                }))
                                )
                                .then(CommandManager.literal("kill").executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        player.kill();
                                        context.getSource().sendMessage(Text.literal("自杀成功"));
                                    }
                                    return 1;
                                }))
                                .then(CommandManager.literal("back").executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        BlockPos pos = player.getLastDeathPos().get().getPos();
                                        player.teleport(pos.getX(), pos.getY(), pos.getZ());
                                        context.getSource().sendMessage(Text.literal("传送成功"));
                                    }
                                    return 1;
                                }))
                                .then(CommandManager.literal("tp").then(CommandManager.argument("position", EntityArgumentType.player()).executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        PlayerEntity entity = EntityArgumentType.getPlayer(context, "position");
                                        player.teleport(entity.getX(), entity.getY(), entity.getZ());
                                        context.getSource().sendMessage(Text.literal("传送成功"));
                                    }
                                    return 1;
                                })))
                                .then(CommandManager.literal("give").then(CommandManager.argument("position", EntityArgumentType.player()).executes(context -> {
                                    PlayerEntity player = context.getSource().getPlayer();
                                    if (player != null) {
                                        PlayerEntity entity = EntityArgumentType.getPlayer(context, "position");
                                        ItemStack item = player.getStackInHand(Hand.MAIN_HAND);
                                        if (item.isEmpty()) {
                                            context.getSource().sendMessage(Text.literal("请手持要传送的物品"));
                                            return 1;
                                        }
                                        int count = item.getCount();
                                        if (!ModUtil.canFitItemsInInventory(entity, item, count)) {
                                            context.getSource().sendMessage(Text.literal("他的背包装不下了"));
                                            return 1;
                                        }
                                        ModUtil.removeItemsFromInventory(player, item, count);
                                        ModUtil.addItemToInventory(entity, item, count);
                                        context.getSource().sendMessage(Text.literal("传送成功"));
                                    }
                                    return 1;
                                }))));
            });
            final long[] t1 = {System.currentTimeMillis() / 1000};
            ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
                long time = System.currentTimeMillis();
                if (t1[0] != time / 1000) {
                    t1[0] = time / 1000;
                    if (time / 1000 % 1000 == 0) {
                        for (ItemEntity item : serverWorld.getEntitiesByType(EntityType.ITEM, item -> true)) {
                            item.remove(Entity.RemovalReason.KILLED);
                        }
                        for (PlayerEntity player : serverWorld.getPlayers()) {
                            player.sendMessage(Text.literal("清理掉落物成功"));
                        }
                    } else if (time / 1000 % 100 == 0 || time / 1000 % 1000 > 990) {
                        for (PlayerEntity player : serverWorld.getPlayers()) {
                            player.sendMessage(Text.literal("还有" + (1000 - time / 1000 % 1000) + "秒清理掉落物"));
                        }
                    }
                }
            });
            ToolMaterial copper_tool = new ToolMaterial() {
                @Override
                public int getDurability() {
                    return ToolMaterials.STONE.getDurability();
                }

                @Override
                public float getMiningSpeedMultiplier() {
                    return ToolMaterials.IRON.getMiningSpeedMultiplier();
                }

                @Override
                public float getAttackDamage() {
                    return ToolMaterials.IRON.getAttackDamage();
                }

                @Override
                public int getMiningLevel() {
                    return ToolMaterials.IRON.getMiningLevel();
                }

                @Override
                public int getEnchantability() {
                    return ToolMaterials.IRON.getMiningLevel();
                }

                @Override
                public Ingredient getRepairIngredient() {
                    return Ingredient.ofItems(Items.COPPER_INGOT);
                }
            };
            Item copper_sword = ModUtil.registerItem("copper_sword", new SwordItem(copper_tool, 3, -2.4F, new FabricItemSettings()));
            Item copper_pickaxe = ModUtil.registerItem("copper_pickaxe", new PickaxeItem(copper_tool, 1, -2.8F, new FabricItemSettings()));
            Item copper_axe = ModUtil.registerItem("copper_axe", new AxeItem(copper_tool, 6, -3.2F, new FabricItemSettings()));
            Item copper_shovel = ModUtil.registerItem("copper_shovel", new ShovelItem(copper_tool, 1.5F, -3F, new FabricItemSettings()));
            ModUtil.addToGroups(copper_sword, ItemGroups.COMBAT);
            ModUtil.addToGroups(copper_pickaxe, ItemGroups.TOOLS);
            ModUtil.addToGroups(copper_axe, ItemGroups.TOOLS);
            ModUtil.addToGroups(copper_shovel, ItemGroups.TOOLS);
            GeckoLib.initialize();
            EntityType<ChomperEntity> chomper = Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(MOD_ID, "chomper"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ChomperEntity::new)
                            .dimensions(EntityDimensions.fixed(0.4f,1.5f)).build());
            FabricDefaultAttributeRegistry.register(chomper, ChomperEntity.setAttributes());
            EntityRendererRegistry.register(chomper, ChomperRenderer::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}