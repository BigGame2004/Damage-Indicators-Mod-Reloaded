package DITextures;

import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.configuration.DIConfig;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.opengl.GL11;

public abstract class AbstractSkin {
    public static final List<String> AVAILABLESKINS = new ArrayList();
    private static String lastSkinUsed;
    public static final Minecraft MCINSTANCE = Minecraft.getMinecraft();
    public static final Map<String, AbstractSkin> SKINS = new HashMap();
    private final EnumMap<EnumSkinPart, Object> skinMap = new EnumMap(EnumSkinPart.class);

    public static BufferedImage fixDim(BufferedImage nonpoweroftwo) {
        int width = nonpoweroftwo.getWidth();
        int scaledwidth = width;
        if (!isPowerOfTwoFast(width)) {
            scaledwidth = upperPowerOfTwo(width);
        }

        int height = nonpoweroftwo.getHeight();
        int scaledheight = height;
        if (!isPowerOfTwoFast(height)) {
            scaledheight = upperPowerOfTwo(height);
        }

        BufferedImage resized;
        if (width == scaledwidth && height == scaledheight) {
            resized = nonpoweroftwo;
        } else {
            try {
                resized = new BufferedImage(scaledwidth, scaledheight, nonpoweroftwo.getType());
            } catch (Throwable var7) {
                resized = new BufferedImage(scaledwidth, scaledheight, 5);
            }

            Graphics2D graphics = resized.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(nonpoweroftwo, 0, 0, scaledwidth, scaledheight, 0, 0, width, height, (ImageObserver)null);
            graphics.dispose();
        }

        return resized;
    }

    public static AbstractSkin getActiveSkin() {
        return setSkin(DIConfig.mainInstance().selectedSkin);
    }

    public static String getAuthor(String internalName) {
        return (String)((AbstractSkin)SKINS.get(internalName)).getSkinValue(EnumSkinPart.CONFIGAUTHOR);
    }

    public static String getSkinName(String internalName) {
        return (String)((AbstractSkin)SKINS.get(internalName)).getSkinValue(EnumSkinPart.CONFIGDISPLAYNM);
    }

    public static void init() {
        JarSkinRegistration.scanJarForSkins(DIMod.class);
        File file = new File(Minecraft.getMinecraft().gameDir, "CustomDISkins");
        file.mkdirs();
        FileSkinRegistration.scanFilesForSkins(file);

        for(String s : AVAILABLESKINS) {
            AbstractSkin skin = null;
            if (s.startsWith("file:")) {
                skin = new FileSkinRegistration(s);
            } else {
                skin = new JarSkinRegistration(s);
            }

            releaseCurrentTextures();
            skin.loadSkin();
            SKINS.put(s, skin);
        }

        if (!AVAILABLESKINS.contains(DIConfig.mainInstance().selectedSkin)) {
            DIConfig.mainInstance().selectedSkin = "/assets/defaultskins/default/";
            DIConfig.overrideConfigAndSave(DIConfig.mainInstance());
            setSkin(DIConfig.mainInstance().selectedSkin);
        }

        releaseCurrentTextures();
        getActiveSkin().loadSkin();
    }

    private static boolean isPowerOfTwoFast(int num) {
        return num != 0 && (num & num - 1) == 0;
    }

    public static void refreshSkin() {
        releaseCurrentTextures();
        getActiveSkin().loadSkin();
    }

    public abstract void loadConfig();

    private static void releaseCurrentTextures() {
        AbstractSkin lastSkin = (AbstractSkin)SKINS.get(lastSkinUsed);
        if (lastSkin != null) {
            for(EnumSkinPart esp : EnumSet.allOf(EnumSkinPart.class)) {
                if (esp.name().endsWith("ID")) {
                    if ((DynamicTexture)lastSkin.skinMap.get(esp) != null) {
                        GL11.glDeleteTextures(((DynamicTexture)lastSkin.skinMap.get(esp)).getGlTextureId());
                    }

                    lastSkin.skinMap.put(esp, (Object)null);
                }
            }
        }

    }

    public static AbstractSkin setSkin(String skin) {
        if (lastSkinUsed != null && !lastSkinUsed.equals(skin)) {
            releaseCurrentTextures();
            if (SKINS.containsKey(skin)) {
                ((AbstractSkin)SKINS.get(skin)).loadSkin();
            }
        }

        if (!SKINS.containsKey(skin)) {
            try {
                if (skin.startsWith("file:")) {
                    SKINS.put(skin, new FileSkinRegistration(skin));
                } else {
                    SKINS.put(skin, new JarSkinRegistration(skin));
                }

                ((AbstractSkin)SKINS.get(skin)).loadSkin();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        lastSkinUsed = skin;
        return (AbstractSkin)SKINS.get(skin);
    }

    private static int upperPowerOfTwo(int num) {
        int newnum = num - 1;
        newnum |= newnum >> 1;
        newnum |= newnum >> 2;
        newnum |= newnum >> 4;
        newnum |= newnum >> 8;
        newnum |= newnum >> 16;
        ++newnum;
        return newnum;
    }

    public AbstractSkin() {
        for(EnumSkinPart esp : EnumSet.allOf(EnumSkinPart.class)) {
            this.skinMap.put(esp, esp.getConfigDefault());
        }

    }

    public final void bindTexture(EnumSkinPart enumSkinPart) {
        ((DynamicTexture)this.skinMap.get(enumSkinPart)).updateDynamicTexture();
    }

    public final String getInternalName() {
        return (String)this.skinMap.get(EnumSkinPart.INTERNAL);
    }

    public final Object getSkinValue(EnumSkinPart enumSkinPart) {
        return this.skinMap.get(enumSkinPart);
    }

    protected final void loadConfig(Configuration config) {
        config.load();
        String strKey = null;

        for(EnumSkinPart enumSkinPart : EnumSet.allOf(EnumSkinPart.class)) {
            String spName = enumSkinPart.name();
            strKey = (String)enumSkinPart.getExtended();
            if (strKey != null) {
                Object defaultVal = enumSkinPart.getConfigDefault();
                String strCat;
                if (!spName.endsWith("WIDTH") && !spName.endsWith("HEIGHT")) {
                    if (!spName.endsWith("X") && !spName.endsWith("Y") && !spName.endsWith("OFFSET")) {
                        if (spName.contains("CONFIGTEXTEXT")) {
                            strCat = "Skin config.TextSettings";
                        } else {
                            strCat = "Skin config.Info";
                        }
                    } else {
                        strCat = "Skin config.Positions";
                    }
                } else {
                    strCat = "Skin config.Sizes";
                }

                if (defaultVal instanceof Integer) {
                    this.skinMap.put(enumSkinPart, config.get(strCat, strKey, (Integer)defaultVal).getInt((Integer)defaultVal));
                } else {
                    this.skinMap.put(enumSkinPart, config.get(strCat, strKey, (String)defaultVal).getString());
                }
            }
        }

        this.skinMap.put(EnumSkinPart.ORDERING, this.populateOrdering(config));
        config.save();
    }

    public abstract void loadSkin();

    private Ordering[] populateOrdering(Configuration config) {
        Ordering[] ordering = new Ordering[9];
        String strCat = "Skin config.Ordering";
        ordering[config.get(strCat, "HealthBarOrder", 3).getInt(3) - 1] = Ordering.HEALTHBAR;
        ordering[config.get(strCat, "FrameOrder", 5).getInt(5) - 1] = Ordering.FRAME;
        ordering[config.get(strCat, "BackgroundOrder", 1).getInt(1) - 1] = Ordering.BACKGROUND;
        ordering[config.get(strCat, "NamePlateOrder", 4).getInt(4) - 1] = Ordering.NAMEPLATE;
        ordering[config.get(strCat, "MobPreviewOrder", 2).getInt(2) - 1] = Ordering.MOBPREVIEW;
        ordering[config.get(strCat, "MobTypeOrder", 6).getInt(6) - 1] = Ordering.MOBTYPES;
        ordering[config.get(strCat, "PotionBoxOrder", 7).getInt(7) - 1] = Ordering.POTIONS;
        ordering[config.get(strCat, "HealthBarTextOrder", 8).getInt(8) - 1] = Ordering.HEALTHTEXT;
        ordering[config.get(strCat, "NamePlateTextOrder", 9).getInt(9) - 1] = Ordering.NAMETEXT;
        return ordering;
    }

    public final void setInternalName(String newInternalName) {
        this.skinMap.put(EnumSkinPart.INTERNAL, newInternalName);
    }

    public final void setSkinValue(EnumSkinPart enumSkinPart, Object value) {
        this.skinMap.put(enumSkinPart, value);
    }

    public final DynamicTexture setupTexture(BufferedImage bufImg, EnumSkinPart uniqueName) {
        DynamicTexture check = (DynamicTexture)this.skinMap.get(uniqueName);
        if (check == null) {
            check = new DynamicTexture(bufImg);
            this.skinMap.put(uniqueName, check);
        } else {
            bufImg.getRGB(0, 0, bufImg.getWidth(), bufImg.getHeight(), check.getTextureData(), 0, bufImg.getWidth());
        }

        return check;
    }
}
