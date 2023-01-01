package dev.entirety.enoughslime.forge;

import dev.entirety.enoughslime.common.ModIds;
import dev.entirety.enoughslime.common.Constants;
import dev.entirety.enoughslime.forge.events.PermanentEventSubscriptions;
import dev.entirety.enoughslime.forge.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.filters.VanillaPacketFilter;

@Mod(ModIds.ENOUGHSLIME_ID)
public class EnoughSlime {

    public EnoughSlime() {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PermanentEventSubscriptions subscriptions = new PermanentEventSubscriptions(eventBus, modEventBus);

        NetworkHandler networkHandler = new NetworkHandler(Constants.NETWORK_CHANNEL_ID, "1.0.0");

        EnoughSlimeClient client = new EnoughSlimeClient(networkHandler, subscriptions);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> client::register);

        VanillaPacketFilter
    }

}
