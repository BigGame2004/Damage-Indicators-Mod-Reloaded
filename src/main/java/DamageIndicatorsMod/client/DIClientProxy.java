package DamageIndicatorsMod.client;

import DITextures.JarSkinRegistration;
import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.DIEventBus;
import DamageIndicatorsMod.core.Tools;
import DamageIndicatorsMod.rendering.DIWordParticles;
import DamageIndicatorsMod.server.DIProxy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class DIClientProxy extends DIProxy {
    public static KeyBinding kb;
    int wordParticle = 1051414;

    public void register() {
        super.register();
        DIEventBus seh = new DIEventBus();
        MinecraftForge.EVENT_BUS.register(seh);
        Tools.getInstance().RegisterRenders();
        JarSkinRegistration.init();
        Minecraft.getMinecraft().effectRenderer.registerParticle(this.wordParticle, (particleID, worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, p_178902_15_) -> {
            DIWordParticles customParticle = new DIWordParticles(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            if (p_178902_15_[0] == 1) {
                customParticle.shouldOnTop = true;
            }

            return customParticle;
        });
    }

    public void doCritical(Entity target) {
        int shouldbeseen = 0;
        if (Minecraft.getMinecraft().player.canEntityBeSeen(target)) {
            shouldbeseen = 1;
        } else if (Minecraft.getMinecraft().isSingleplayer()) {
            shouldbeseen = DIConfig.mainInstance().alwaysRender ? 1 : 0;
        }

        if (target != Minecraft.getMinecraft().player || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
            double var10003 = target.posY + (double)target.height;
            Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(this.wordParticle, target.posX, var10003, target.posZ, 0.001, (double)(0.05F * DIConfig.mainInstance().BounceStrength), 0.001, new int[]{shouldbeseen});
        }

    }

    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    public void trysendmessage() {
        try {
            Iterator modsIT = Loader.instance().getModList().iterator();

            while(modsIT.hasNext()) {
                this.dimod = (ModContainer)modsIT.next();
                if (this.dimod != null && this.dimod.getName().equals("Damage Indicators")) {
                    break;
                }
            }

            System.out.println(this.dimod.getMetadata().version);
            (new Thread(new Runnable() {
                public void run() {
                    try {
                        InputStreamReader fr = new InputStreamReader((new URL("https://github.com/BigGame2004/Damage-Indicators-Mod-Patched/blob/main/changelog.txt")).openStream(), "UTF-8");
                        BufferedReader br = new BufferedReader(fr);
                        String version = "";
                        version = br.readLine().trim();

                        try {
                            String nextDonater = null;

                            while((nextDonater = br.readLine()) != null) {
                                nextDonater = nextDonater.trim();
                                if (!nextDonater.isEmpty()) {
                                    DIMod.donators.add(nextDonater.toLowerCase());
                                }
                            }
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                        // Something in the logic that tries to tell the player the mod failed to check for updates can crash the game.
                        br.close();
                        fr.close();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        if (DIConfig.mainInstance().checkForUpdates >= 2) {
                            DIMod.s_sUpdateMessage = "Damage Indicators was unable to check for updates!";
                        }
                    }

                    if (DIConfig.mainInstance().checkForUpdates == 0) {
                        DIMod.s_sUpdateMessage = null;
                    }

                }
            })).start();
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (DIConfig.mainInstance().checkForUpdates >= 2) {
                DIMod.s_sUpdateMessage = "Damage Indicators was unable to check for updates!";
            }
        }

    }
}
