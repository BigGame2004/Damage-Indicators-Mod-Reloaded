package DamageIndicatorsMod.core;

import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.configuration.DIConfig;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class Tools {
    private static HashMap<Class, EntityConfigurationEntry> entityMap = new HashMap();
    public static int timeTillFlush = 500;
    private Minecraft mc = Minecraft.getMinecraft();
    public List<Object[]> unloadedEntities = new ArrayList();
    private static Tools instance;
    boolean lasttimefailed = false;

    public static Tools getInstance() {
        if (instance == null) {
            instance = new Tools();
        }

        return instance;
    }

    public void checkIfLoaded() {
    }

    public BufferedImage doFilter(BufferedImage src) throws OutOfMemoryError, Throwable {
        int upScaleDim = MathHelper.floor((float)src.getWidth() * DIConfig.mainInstance().ScaleFilter);
        BufferedImage dst = new BufferedImage(upScaleDim, upScaleDim, src.getType());
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance((double)DIConfig.mainInstance().ScaleFilter, (double)DIConfig.mainInstance().ScaleFilter), DIConfig.mainInstance().hints);
        ato.filter(src, dst);
        return dst;
    }

    public HashMap<Class, EntityConfigurationEntry> getEntityMap() {
        if (entityMap.isEmpty()) {
            this.scanforEntities();
        }

        return entityMap;
    }

    public void giveUpdateInformation() {
        if (DIMod.s_sUpdateMessage == null) {
            DIMod.s_sUpdateMessage = "Damage Indicators was unable to check for updates.";
        }

        if (!"".equals(DIMod.s_sUpdateMessage) && this.mc.player != null) {
            this.mc.player.sendMessage(new TextComponentString(DIMod.s_sUpdateMessage));
            DIMod.s_sUpdateMessage = "";
        }

    }

    public void registerCommands() {
    }

    public void RegisterRenders() {
        this.scanforEntities();
        MinecraftForge.EVENT_BUS.register(DITicker.instance);
    }

    public static Map<Class<? extends Entity>, String> getEntityList() {
        Map<Class<? extends Entity>, String> ret = new HashMap();

        for(ResourceLocation rl : EntityList.getEntityNameList()) {
            ret.put(EntityList.getClass(rl), EntityList.getTranslationName(rl));
        }

        ret.put(EntityOtherPlayerMP.class, "OtherPlayers");
        return ret;
    }

    public void scanforEntities() {
        this.searchMapForEntities(getEntityList());
    }

    private void searchMapForEntities(Map theMap) {
        Configuration config = EntityConfigurationEntry.getEntityConfiguration();
        this.lasttimefailed = false;

        for(Class entry : theMap.keySet()) {
            try {
                if (entry != null && EntityLiving.class.isAssignableFrom(entry)) {
                    entityMap.put(entry, EntityConfigurationEntry.generateDefaultConfiguration(config, entry));
                }
            } catch (Throwable var7) {
            }
        }

        config.save();
    }
}
