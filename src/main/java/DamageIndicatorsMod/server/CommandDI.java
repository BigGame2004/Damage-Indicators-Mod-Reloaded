package DamageIndicatorsMod.server;

import DamageIndicatorsMod.configuration.DIConfig;
import java.io.File;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandDI extends CommandBase {
    public String getName() {
        return "direload";
    }

    public String getUsage(ICommandSender icommandsender) {
        return "commands.damageindicatorsmod.direload";
    }

    public void execute(MinecraftServer server, ICommandSender icommandsender, String[] astring) {
        if (icommandsender.getName().toLowerCase().equals("server")) {
            DIConfig.loadConfig((File)null);

            for(EntityPlayer player : (List)server.getPlayerList()) {
                if (player != null) {
                    ServerEventHandler.sendServerSettings(player);
                }
            }
        } else if (FMLCommonHandler.instance().getSide().isClient()) {
            ((EntityPlayer)icommandsender).sendMessage(new TextComponentString("Configuration Reloading"));
            DIConfig.loadConfig((File)null);
        }

    }
}
