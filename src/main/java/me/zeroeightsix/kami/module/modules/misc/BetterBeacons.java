package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;

import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

/***
 * Created by 0x2E | PretendingToCode
 */
@Module.Info(name = "BetterBeacons", category = Module.Category.MISC, description = "Choose any of the 5 beacon effects regardless of beacon base height")
public class BetterBeacons extends Module {
    
    private static BetterBeacons INSTANCE = new BetterBeacons();
    public static boolean enabled() { return INSTANCE.isEnabled(); }
    public BetterBeacons() { INSTANCE = this; }

    private Setting<Effects> effects = register(Settings.e("Effect", Effects.SPEED));

    private int potionID = 1;
    private boolean doCancelPacket = true;

    private enum Effects { SPEED, HASTE, RESISTANCE, JUMP_BOOST, STRENGTH }

    @Override
    public void onUpdate() {
        if (effects.getValue().equals(Effects.SPEED)) {
            potionID = 1;
        }
        if (effects.getValue().equals(Effects.HASTE)) {
            potionID = 3;
        }
        if (effects.getValue().equals(Effects.RESISTANCE)) {
            potionID = 11;
        }
        if (effects.getValue().equals(Effects.JUMP_BOOST)) {
            potionID = 8;
        }
        if (effects.getValue().equals(Effects.STRENGTH)) {
            potionID = 5;
        }
    }

    @EventHandler
    public Listener<PacketEvent.Send> packetListener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketCustomPayload && ((CPacketCustomPayload) event.getPacket()).getChannelName().equals("MC|Beacon") && doCancelPacket) {
            doCancelPacket = false;

            PacketBuffer data = ((CPacketCustomPayload) event.getPacket()).getBufferData();
            int i1 = data.readInt(); //primary
            int k1 = data.readInt(); //secondary

            event.cancel();

            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
            buf.writeInt(potionID);
            buf.writeInt(k1);

            Wrapper.getPlayer().connection.sendPacket(new CPacketCustomPayload("MC|Beacon", buf));

            doCancelPacket = true;
        }
    });
}
