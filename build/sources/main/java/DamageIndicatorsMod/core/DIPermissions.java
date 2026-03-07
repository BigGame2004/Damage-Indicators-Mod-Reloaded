package DamageIndicatorsMod.core;

import DamageIndicatorsMod.DIMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DIPermissions implements IMessage {
    byte message;

    public DIPermissions() {
    }

    public DIPermissions(byte message) {
        this.message = message;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            this.message = buf.readByte();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    public void toBytes(ByteBuf buf) {
        try {
            buf.writeByte(this.message);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    public static class Handler implements IMessageHandler<DIPermissions, IMessage> {
        public static boolean allDisabled = false;
        public static boolean mouseOversDisabled = false;
        public static boolean potionEffectsDisabled = false;
        public static boolean popOffsDisabled = false;

        public DIPermissions onMessage(DIPermissions message, MessageContext ctx) {
            processPermissions(DIMod.proxy.getPlayer(), (byte)0);
            return null;
        }

        public static void processPermissions(EntityPlayer player, byte toggles) {
            allDisabled = (toggles & 1) != 0;
            mouseOversDisabled = (toggles & 2) != 0;
            potionEffectsDisabled = (toggles & 4) != 0;
            popOffsDisabled = (toggles & 8) != 0;
            String darkRed = "§4";
            String darkGreen = "§2";
            if (!mouseOversDisabled && !allDisabled) {
                player.sendMessage(new TextComponentString("[DamageIndicators] " + darkGreen + "Mouseovers enabled."));
            } else {
                player.sendMessage(new TextComponentString("[DamageIndicators] " + darkRed + "Server has disabled mouseovers."));
            }

            if (!potionEffectsDisabled && !allDisabled) {
                player.sendMessage(new TextComponentString("[DamageIndicators] " + darkGreen + "Potion Effects enabled."));
            } else {
                player.sendMessage(new TextComponentString("[DamageIndicators] " + darkRed + "Server has disabled potion effects."));
            }

            if (!popOffsDisabled && !allDisabled) {
                player.sendMessage(new TextComponentString("[DamageIndicators] " + darkGreen + "Popoffs enabled."));
            } else {
                player.sendMessage(new TextComponentString("[DamageIndicators] " + darkRed + "Server has disabled damage popoffs."));
            }

        }
    }
}
