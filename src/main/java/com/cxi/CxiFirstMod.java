package com.cxi;

import com.cxi.utils.IOUtil;
import com.cxi.utils.ModUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    if (!jsonArray.isEmpty() && new Gson().toJson(jsonArray).contains("\"" + state.getBlock().getName().getString() + "\""))
                        player.sendMessage(Text.literal("成功连锁" + ModUtil.blockChain(player, world, state.getBlock(), pos) + "个方块"));
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
                                ));
            });
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }
}