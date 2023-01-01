package dev.entirety.enoughslime.common.network.packets;

import dev.entirety.enoughslime.common.network.IPacketId;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.tuple.Pair;

public abstract class PacketSlime {

    public final Pair<FriendlyByteBuf, Integer> getPacketData() {
        IPacketId packetId = getPacketId();
        int packetIdOrdinal = packetId.ordinal();

        FriendlyByteBuf packetData = new FriendlyByteBuf(Unpooled.buffer());
        packetData.writeByte(packetIdOrdinal);
        writePacketData(packetData);

        return Pair.of(packetData, packetIdOrdinal);
    }

    protected abstract IPacketId getPacketId();

    protected abstract void writePacketData(FriendlyByteBuf buffer);

}
