/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package dev.entirety.enoughslime.bukkit.network.filters;

import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;

import java.util.Map;
import java.util.function.Function;

public class NetworkFilters {

    private static final Map<String, Function<Connection, VanillaPacketFilter>> instances = ImmutableMap.of();

    private NetworkFilters() {}

    public static void injectIfNecessary(Connection manager) {
        ChannelPipeline pipeline = manager.channel.pipeline();
        if (pipeline.get("packet_handler") == null) return;

        instances.forEach((key, filterFactory) -> {

        });
    }

}
