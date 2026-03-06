package DamageIndicatorsMod.gui;

import DITextures.AbstractSkin;
import DITextures.EnumSkinPart;
import DITextures.JarSkinRegistration;
import DITextures.Ordering;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.DIEventBus;
import DamageIndicatorsMod.core.EntityConfigurationEntry;
import DamageIndicatorsMod.core.Tools;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.opengl.GL11;

public class DIGuiTools extends GuiIngame {
    public static Field foundField = null;
    public static DIGuiTools instance = new DIGuiTools(Minecraft.func_71410_x());
    public static Map<Class, Integer> mobRenderLists = new HashMap();
    public static Long offset;
    public static int opt = 0;
    public static double rotationCounter = (double)0.0F;
    public static boolean skinned = true;
    private static final Minecraft mc = Minecraft.func_71410_x();
    private static ScaledResolution scaledresolution;
    public static DynamicTexture inventoryPNG;
    public static DynamicTexture widgetsPNG;

    public static void addVertexWithUV(double x, double y, double z, double u, double v) {
        GL11.glTexCoord2d(u, v);
        GL11.glVertex3d(x, y, z);
    }

    public static void addVertex(double x, double y, double z) {
        GL11.glVertex3d(x, y, z);
    }

    public static void drawBackground(AbstractSkin skin, int locX, int locY) {
        int backgroundWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH);
        int backgroundHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT);
        int backgroundX = locX + (Integer)skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDX);
        int backgroundY = locY + (Integer)skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDY);
        skin.bindTexture(EnumSkinPart.BACKGROUNDID);
        GL11.glBegin(7);
        addVertexWithUV((double)backgroundX, (double)(backgroundY + backgroundHeight), (double)0.0F, (double)0.0F, (double)1.0F);
        addVertexWithUV((double)(backgroundX + backgroundWidth), (double)(backgroundY + backgroundHeight), (double)0.0F, (double)1.0F, (double)1.0F);
        addVertexWithUV((double)(backgroundX + backgroundWidth), (double)backgroundY, (double)0.0F, (double)1.0F, (double)0.0F);
        addVertexWithUV((double)backgroundX, (double)backgroundY, (double)0.0F, (double)0.0F, (double)0.0F);
        GL11.glEnd();
    }

    public static void drawFrame(AbstractSkin skin, int locX, int locY) {
        skin.bindTexture(EnumSkinPart.FRAMEID);
        int adjx = locX + (Integer)skin.getSkinValue(EnumSkinPart.CONFIGFRAMEX);
        int adjy = locY + (Integer)skin.getSkinValue(EnumSkinPart.CONFIGFRAMEY);
        int backgroundWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH);
        int backgroundHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT);
        GL11.glBegin(7);
        addVertexWithUV((double)adjx, (double)(adjy + backgroundHeight), (double)0.0F, (double)0.0F, (double)1.0F);
        addVertexWithUV((double)(adjx + backgroundWidth), (double)(adjy + backgroundHeight), (double)0.0F, (double)1.0F, (double)1.0F);
        addVertexWithUV((double)(adjx + backgroundWidth), (double)adjy, (double)0.0F, (double)1.0F, (double)0.0F);
        addVertexWithUV((double)adjx, (double)adjy, (double)0.0F, (double)0.0F, (double)0.0F);
        GL11.glEnd();
    }

    private static void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6, float zLevel) {
        float var7 = (float)(par5 >> 24 & 255) / 255.0F;
        float var8 = (float)(par5 >> 16 & 255) / 255.0F;
        float var9 = (float)(par5 >> 8 & 255) / 255.0F;
        float var10 = (float)(par5 & 255) / 255.0F;
        float var11 = (float)(par6 >> 24 & 255) / 255.0F;
        float var12 = (float)(par6 >> 16 & 255) / 255.0F;
        float var13 = (float)(par6 >> 8 & 255) / 255.0F;
        float var14 = (float)(par6 & 255) / 255.0F;
        GL11.glDisable(3553);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        Tessellator var15 = Tessellator.func_178181_a();
        GL11.glBegin(7);
        GL11.glColor4d((double)var8, (double)var9, (double)var10, (double)var7);
        addVertex((double)par3, (double)par2, (double)zLevel);
        addVertex((double)par1, (double)par2, (double)zLevel);
        GL11.glColor4d((double)var12, (double)var13, (double)var14, (double)var11);
        addVertex((double)par1, (double)par4, (double)zLevel);
        addVertex((double)par3, (double)par4, (double)zLevel);
        var15.func_78381_a();
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glEnable(3553);
    }

    public static void drawHealthBar(AbstractSkin skin, int locX, int locY, int health, int maxHealth, int entityID) {
        health = Math.min(health, maxHealth);
        int healthBarWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH);
        int healthBarHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT);
        int healthBarX = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX);
        int healthBarY = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY);
        skin.bindTexture(EnumSkinPart.DAMAGEID);
        GL11.glBegin(7);
        addVertexWithUV((double)(locX + healthBarX), (double)(locY + healthBarY + healthBarHeight), (double)0.0F, (double)((float)health / (float)maxHealth), (double)1.0F);
        addVertexWithUV((double)(locX + healthBarX + healthBarWidth), (double)(locY + healthBarY + healthBarHeight), (double)0.0F, (double)1.0F, (double)1.0F);
        addVertexWithUV((double)(locX + healthBarX + healthBarWidth), (double)(locY + healthBarY), (double)0.0F, (double)1.0F, (double)0.0F);
        addVertexWithUV((double)(locX + healthBarX), (double)(locY + healthBarY), (double)0.0F, (double)((float)health / (float)maxHealth), (double)0.0F);
        GL11.glEnd();
        float healthbarwidth;
        if (health < maxHealth) {
            float f = (float)health * 1.0F / ((float)maxHealth * 1.0F);
            f = (float)healthBarWidth * f;
            healthbarwidth = f;
            if (f < 0.0F) {
                healthbarwidth = 0.0F;
            }
        } else {
            healthbarwidth = (float)healthBarWidth;
            EntityConfigurationEntry.maxHealthOverride.put(entityID, health);
        }

        float tmp = (float)health / (float)maxHealth;
        skin.bindTexture(EnumSkinPart.HEALTHID);
        GL11.glBegin(7);
        addVertexWithUV((double)(locX + healthBarX), (double)(locY + healthBarY + healthBarHeight), (double)0.0F, (double)0.0F, (double)1.0F);
        addVertexWithUV((double)((float)(locX + healthBarX) + healthbarwidth), (double)(locY + healthBarY + healthBarHeight), (double)0.0F, (double)tmp, (double)1.0F);
        addVertexWithUV((double)((float)(locX + healthBarX) + healthbarwidth), (double)(locY + healthBarY), (double)0.0F, (double)tmp, (double)0.0F);
        addVertexWithUV((double)(locX + healthBarX), (double)(locY + healthBarY), (double)0.0F, (double)0.0F, (double)0.0F);
        GL11.glEnd();
    }

    public static void drawHealthText(AbstractSkin skin, int locX, int locY, int health, int maxHealth) {
        try {
            String Health = health + "/" + maxHealth;
            if (health > maxHealth) {
                Health = health + "/" + health;
            }

            int healthBarWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARWIDTH);
            int healthBarHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARHEIGHT);
            int healthBarX = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARX);
            int healthBarY = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGHEALTHBARY);
            int packedRGB = Integer.parseInt("FFFFFF", 16);

            try {
                packedRGB = Integer.parseInt((String)skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTHEALTHCOLOR), 16);
            } catch (Exception var14) {
            }

            if (mc.field_71466_p.field_78288_b + 2 > healthBarHeight) {
                GL11.glPushMatrix();

                try {
                    GL11.glTranslatef((float)(locX + healthBarX) + ((float)healthBarWidth - (float)mc.field_71466_p.func_78256_a(Health) * 0.7F) / 2.0F, (float)(locY + healthBarY + healthBarHeight) - (float)mc.field_71466_p.field_78288_b * 0.7F - 0.5F, 0.0F);
                    GL11.glScalef(0.7F, 0.7F, 1.0F);
                    mc.field_71466_p.func_175063_a(Health, 0.0F, 0.0F, packedRGB);
                } catch (Throwable var13) {
                }

                GL11.glPopMatrix();
            } else {
                try {
                    mc.field_71466_p.func_175063_a(Health, (float)(locX + healthBarX + (healthBarWidth - mc.field_71466_p.func_78256_a(Health)) / 2), (float)(locY + healthBarY + (healthBarHeight - mc.field_71466_p.field_78288_b) / 2), packedRGB);
                } catch (Throwable var12) {
                }
            }

            GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    public static void drawMobPreview(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
        GL11.glPushAttrib(8192);
        int backgroundWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH);
        int backgroundHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT);
        int MobPreviewOffsetX = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX);
        int MobPreviewOffsetY = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY);
        GL11.glEnable(3089);

        try {
            int ex = MathHelper.func_76141_d((float)((locX + MobPreviewOffsetX) * scaledresolution.func_78325_e()));
            int boxWidth = MathHelper.func_76141_d((float)(backgroundWidth * scaledresolution.func_78325_e()));
            int boxHeight = MathHelper.func_76141_d((float)(backgroundHeight * scaledresolution.func_78325_e()));
            int boxLocY = MathHelper.func_76141_d((float)((locY + MobPreviewOffsetY) * scaledresolution.func_78325_e()));
            if (!(mc.field_71462_r instanceof AdvancedGui)) {
                boxWidth = (int)((float)boxWidth * DIConfig.mainInstance().guiScale);
                boxHeight = (int)((float)boxHeight * DIConfig.mainInstance().guiScale);
            }

            GL11.glScissor(ex, Minecraft.func_71410_x().field_71440_d - boxLocY - boxHeight, boxWidth, boxHeight);
            drawTargettedMobPreview(el, locX + MobPreviewOffsetX, locY + MobPreviewOffsetY);
        } catch (Throwable var15) {
            var15.printStackTrace();
        }

        GL11.glDisable(3089);
        GL11.glPopAttrib();
    }

    public static void drawMobTypes(EntityLivingBase el, AbstractSkin skin, int locX, int locY) {
        if (!DIEventBus.enemies.contains(el.func_145782_y()) && !(el instanceof IMob)) {
            GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.6F);
            GL11.glColor4d((double)0.0F, (double)1.0F, (double)0.0F, (double)0.6F);
        } else {
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.6F);
            GL11.glColor4d((double)1.0F, (double)0.0F, (double)0.0F, (double)0.6F);
        }

        float step1 = 0.2F;
        float glTexX;
        if (!el.func_184222_aU()) {
            glTexX = 4.0F * step1;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, (double)0.6F);
        } else if (el.func_70668_bt() != EnumCreatureAttribute.UNDEAD && !el.func_70662_br()) {
            if (el.func_70668_bt() == EnumCreatureAttribute.ARTHROPOD) {
                glTexX = 3.0F * step1;
            } else if (!(el instanceof EntityPlayer) && !(el instanceof EntityWitch) && !(el instanceof EntityVillager) && !(el instanceof EntityIronGolem)) {
                glTexX = 1.0F * step1;
            } else {
                glTexX = 2.0F * step1;
            }
        } else {
            glTexX = 0.0F * step1;
        }

        float adjX = (float)(locX + (Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEX));
        float adjY = (float)(locY + (Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEY));
        skin.bindTexture(EnumSkinPart.TYPEICONSID);
        GL11.glBegin(7);
        addVertexWithUV((double)adjX, (double)adjY, (double)0.0F, (double)glTexX, (double)0.0F);
        addVertexWithUV((double)adjX, (double)(adjY + (float)(Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT)), (double)0.0F, (double)glTexX, (double)1.0F);
        addVertexWithUV((double)(adjX + (float)(Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH)), (double)(adjY + (float)(Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEHEIGHT)), (double)0.0F, (double)(glTexX + step1), (double)1.0F);
        addVertexWithUV((double)(adjX + (float)(Integer)skin.getSkinValue(EnumSkinPart.CONFIGMOBTYPEWIDTH)), (double)adjY, (double)0.0F, (double)(glTexX + step1), (double)0.0F);
        GL11.glEnd();
    }

    public static void drawNamePlate(AbstractSkin skin, int locX, int locY) {
        int NamePlateWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH);
        int NamePlateHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT);
        int NamePlateX = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX);
        int NamePlateY = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY);
        skin.bindTexture(EnumSkinPart.NAMEPLATEID);
        GL11.glBegin(7);
        addVertexWithUV((double)(locX + NamePlateX), (double)(locY + NamePlateY), (double)0.0F, (double)0.0F, (double)0.0F);
        addVertexWithUV((double)(locX + NamePlateX), (double)(locY + NamePlateY + NamePlateHeight), (double)0.0F, (double)0.0F, (double)1.0F);
        addVertexWithUV((double)(locX + NamePlateX + NamePlateWidth), (double)(locY + NamePlateY + NamePlateHeight), (double)0.0F, (double)1.0F, (double)1.0F);
        addVertexWithUV((double)(locX + NamePlateX + NamePlateWidth), (double)(locY + NamePlateY), (double)0.0F, (double)1.0F, (double)0.0F);
        GL11.glEnd();
    }

    public static void drawNameText(AbstractSkin skin, String Name, int locX, int locY) {
        int NamePlateWidth = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEWIDTH);
        int NamePlateHeight = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEHEIGHT);
        int NamePlateX = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEX);
        int NamePlateY = (Integer)skin.getSkinValue(EnumSkinPart.CONFIGNAMEPLATEY);
        int packedRGB = Integer.parseInt("FFFFFF", 16);

        try {
            packedRGB = Integer.parseInt((String)skin.getSkinValue(EnumSkinPart.CONFIGTEXTEXTNAMECOLOR), 16);
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        mc.field_71466_p.func_175063_a(Name, (float)(locX + NamePlateX + (NamePlateWidth - mc.field_71466_p.func_78256_a(Name)) / 2), (float)(locY + NamePlateY + (NamePlateHeight - mc.field_71466_p.field_78288_b) / 2), packedRGB);
        GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void DrawPortraitSkinned(int locX, int locY, String Name, int health, int maxHealth, EntityLivingBase el) {
        scaledresolution = new ScaledResolution(mc);
        int depthzfun = GL11.glGetInteger(2932);
        boolean depthTest = GL11.glGetBoolean(2929);
        boolean blend = GL11.glGetBoolean(3042);

        try {
            AbstractSkin ex = AbstractSkin.getActiveSkin();
            Ordering[] ordering = (Ordering[])ex.getSkinValue(EnumSkinPart.ORDERING);

            for(Ordering element : ordering) {
                GL11.glPushMatrix();

                try {
                    GL11.glDepthFunc(519);
                    if (element != Ordering.MOBPREVIEW) {
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        GL11.glDepthFunc(515);
                    }

                    OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0F, 0.003662109F);
                    GL11.glEnable(3553);
                    GL11.glDisable(3042);
                    GL11.glDepthMask(true);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glEnable(3553);
                    GL11.glEnable(2929);
                    GL11.glDisable(2896);
                    GL11.glBlendFunc(770, 771);
                    GL11.glEnable(3042);
                    GL11.glEnable(3008);
                    boolean drawMobAndBackground = (Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) != 0;
                    switch (element) {
                        case BACKGROUND:
                            if (drawMobAndBackground) {
                                drawBackground(ex, locX, locY);
                            }
                            break;
                        case MOBPREVIEW:
                            if (drawMobAndBackground && el.func_110143_aJ() > 0.0F) {
                                drawMobPreview(el, ex, locX, locY);
                            }
                            break;
                        case NAMEPLATE:
                            drawNamePlate(ex, locX, locY);
                            break;
                        case HEALTHBAR:
                            drawHealthBar(ex, locX, locY, health, maxHealth, el != null ? el.func_145782_y() : -1);
                            break;
                        case FRAME:
                            drawFrame(ex, locX, locY);
                            break;
                        case MOBTYPES:
                            drawMobTypes(el, ex, locX, locY);
                            break;
                        case POTIONS:
                            drawPotionBoxes(el);
                            break;
                        case HEALTHTEXT:
                            drawHealthText(ex, locX, locY, health, maxHealth);
                            break;
                        case NAMETEXT:
                            drawNameText(ex, Name, locX, locY);
                    }
                } catch (Throwable ex1) {
                    ex1.printStackTrace();
                }

                GL11.glPopMatrix();
            }
        } catch (Throwable var26) {
            var26.printStackTrace();
        }

        GL11.glDepthFunc(515);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthFunc(depthzfun);
        if (depthTest) {
            GL11.glEnable(2929);
        } else {
            GL11.glDisable(2929);
        }

        if (blend) {
            GL11.glEnable(3042);
        } else {
            GL11.glDisable(3042);
        }

        GL11.glClear(256);
    }

    public static void drawPotionBoxes(EntityLivingBase el) {
        if (inventoryPNG == null) {
            try {
                BufferedImage skin = ImageIO.read(Minecraft.class.getResourceAsStream("/assets/minecraft/textures/gui/container/inventory.png"));
                inventoryPNG = new DynamicTexture(skin);
            } catch (Throwable var25) {
                var25.printStackTrace();
            }
        }

        AbstractSkin var27 = JarSkinRegistration.getActiveSkin();
        int PotionBoxSidesWidth = (Integer)var27.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXWIDTH);
        int PotionBoxHeight = (Integer)var27.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXHEIGHT);
        int PotionBoxOffsetX = (Integer)var27.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXX);
        int PotionBoxOffsetY = (Integer)var27.getSkinValue(EnumSkinPart.CONFIGPOTIONBOXY);

        try {
            boolean ex = false;
            DIConfig diConfig = DIConfig.mainInstance();
            if (diConfig.enablePotionEffects && DIEventBus.potionEffects.get(el.func_145782_y()) != null && !((Collection)DIEventBus.potionEffects.get(el.func_145782_y())).isEmpty()) {
                int position = 0;
                if (DIEventBus.potionEffects.containsKey(el.func_145782_y())) {
                    for(PotionEffect adjy : (Collection)DIEventBus.potionEffects.get(el.func_145782_y())) {
                        int Duration = adjy.func_76459_b();
                        if (Duration > 0) {
                            Potion potion = adjy.func_188419_a();
                            if (potion != null && potion.func_76400_d() && Duration > 10) {
                                GL11.glPushMatrix();
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F);
                                if (!ex) {
                                    ex = true;
                                    int adjx1 = diConfig.locX + PotionBoxOffsetX;
                                    int adjy1 = diConfig.locY + PotionBoxOffsetY;
                                    var27.bindTexture(EnumSkinPart.LEFTPOTIONID);
                                    GL11.glBegin(7);
                                    addVertexWithUV((double)adjx1, (double)adjy1, (double)0.0F, (double)0.0F, (double)0.0F);
                                    addVertexWithUV((double)adjx1, (double)(adjy1 + PotionBoxHeight), (double)0.0F, (double)0.0F, (double)1.0F);
                                    addVertexWithUV((double)(adjx1 + PotionBoxSidesWidth), (double)(adjy1 + PotionBoxHeight), (double)0.0F, (double)1.0F, (double)1.0F);
                                    addVertexWithUV((double)(adjx1 + PotionBoxSidesWidth), (double)adjy1, (double)0.0F, (double)1.0F, (double)0.0F);
                                    GL11.glEnd();
                                }

                                int adjx1 = diConfig.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth;
                                int adjy1 = diConfig.locY + PotionBoxOffsetY;
                                var27.bindTexture(EnumSkinPart.CENTERPOTIONID);
                                GL11.glBegin(7);
                                addVertexWithUV((double)adjx1, (double)adjy1, (double)0.0F, (double)0.0F, (double)0.0F);
                                addVertexWithUV((double)adjx1, (double)(adjy1 + PotionBoxHeight), (double)0.0F, (double)0.0F, (double)1.0F);
                                addVertexWithUV((double)(adjx1 + 20), (double)(adjy1 + PotionBoxHeight), (double)0.0F, (double)1.0F, (double)1.0F);
                                addVertexWithUV((double)(adjx1 + 20), (double)adjy1, (double)0.0F, (double)1.0F, (double)0.0F);
                                GL11.glEnd();
                                int iconIndex = potion.func_76392_e();
                                String formattedtime = Potion.func_188410_a(adjy, 1.0F);
                                int posx = diConfig.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth + 2;
                                int posy = diConfig.locY + PotionBoxOffsetY + 2;
                                int ioffx = (0 + iconIndex % 8) * 18;
                                int ioffy = (0 + iconIndex / 8) * 18 + 198;
                                int width = PotionBoxHeight - 4;
                                inventoryPNG.func_110564_a();
                                instance.func_73729_b(posx, posy, ioffx, ioffy, width, width);

                                try {
                                    GL11.glTranslatef((float)(diConfig.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth + 13 - mc.field_71466_p.func_78256_a(formattedtime) / 2), (float)(diConfig.locY + PotionBoxOffsetY + PotionBoxHeight) - (float)mc.field_71466_p.field_78288_b * 0.815F, 0.1F);
                                    GL11.glScalef(0.815F, 0.815F, 0.815F);
                                    mc.field_71466_p.func_175063_a(formattedtime, 0.0F, 0.0F, (new Color(1.0F, 1.0F, 0.5F, 1.0F)).getRGB());
                                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                    GL11.glColor4d((double)1.0F, (double)1.0F, (double)1.0F, (double)1.0F);
                                } catch (Throwable var23) {
                                }

                                GL11.glPopMatrix();
                                ++position;
                            }
                        }
                    }

                    if (ex) {
                        int var28 = diConfig.locX + PotionBoxOffsetX + position * 20 + PotionBoxSidesWidth;
                        int var29 = diConfig.locY + PotionBoxOffsetY;
                        var27.bindTexture(EnumSkinPart.RIGHTPOTIONID);
                        GL11.glBegin(7);
                        addVertexWithUV((double)var28, (double)var29, (double)0.0F, (double)0.0F, (double)0.0F);
                        addVertexWithUV((double)var28, (double)(var29 + PotionBoxHeight), (double)0.0F, (double)0.0F, (double)1.0F);
                        addVertexWithUV((double)(var28 + PotionBoxSidesWidth), (double)(var29 + PotionBoxHeight), (double)0.0F, (double)1.0F, (double)1.0F);
                        addVertexWithUV((double)(var28 + PotionBoxSidesWidth), (double)var29, (double)0.0F, (double)1.0F, (double)0.0F);
                        GL11.glEnd();
                    }
                }
            }
        } catch (Throwable var26) {
            var26.printStackTrace();
        }

    }

    public static void drawTargettedMobPreview(EntityLivingBase el, int locX, int locY) {
        DIConfig config = DIConfig.mainInstance();
        Class entityclass = el.getClass();
        EntityConfigurationEntry configentry = (EntityConfigurationEntry)Tools.getInstance().getEntityMap().get(entityclass);
        if (configentry == null) {
            Configuration configfile = EntityConfigurationEntry.getEntityConfiguration();
            configentry = EntityConfigurationEntry.generateDefaultConfiguration(configfile, entityclass);
            configentry.save();
            Tools.getInstance().getEntityMap().put(entityclass, configentry);
        }

        GL11.glPushMatrix();

        try {
            if (el == Minecraft.func_71410_x().field_71439_g) {
                GL11.glTranslatef((float)(locX + 25) + configentry.XOffset, (float)(locY + 52) + configentry.YOffset - 30.0F, 1.0F);
            } else {
                GL11.glTranslatef((float)(locX + 25) + configentry.XOffset, (float)(locY + 52) + configentry.YOffset, 1.0F);
            }

            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float ex = (3.0F - el.func_70047_e()) * configentry.EntitySizeScaling;
            float finalScale = configentry.ScaleFactor + configentry.ScaleFactor * ex;
            if (el.func_70631_g_()) {
                finalScale = (configentry.ScaleFactor + configentry.ScaleFactor * ex) * configentry.BabyScaleFactor;
            }

            GL11.glScalef(finalScale * 0.85F, finalScale * 0.85F, 0.1F);
            if (config.lockPosition) {
                int hurt = el.field_70737_aN;
                float ex1 = el.field_70760_ar;
                el.field_70737_aN = 0;
                el.field_70760_ar = el.field_70761_aq - 360.0F;
                GL11.glRotatef(el.field_70761_aq - 360.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPushMatrix();

                try {
                    renderEntity(el);
                } catch (Throwable var11) {
                    var11.printStackTrace();
                }

                GL11.glPopMatrix();
                el.field_70760_ar = ex1;
                el.field_70737_aN = hurt;
            } else {
                int hurt = el.field_70737_aN;
                el.field_70737_aN = 0;
                GL11.glRotatef(180.0F - Minecraft.func_71410_x().field_71439_g.field_70177_z, 0.0F, -1.0F, 0.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPushMatrix();

                try {
                    renderEntity(el);
                } catch (Throwable var111) {
                }

                GL11.glPopMatrix();
                el.field_70737_aN = hurt;
            }
        } catch (Throwable var13) {
        }

        GL11.glPopMatrix();
    }

    public static void renderEntity(EntityLivingBase el) {
        GL11.glDisable(3042);
        GL11.glEnable(2929);

        try {
            float backup = RenderLiving.NAME_TAG_RANGE;
            RenderLiving.NAME_TAG_RANGE = 0.0F;
            Render render = Minecraft.func_71410_x().func_175598_ae().func_78713_a(el);
            if (render != null) {
                render.func_76986_a(el, (double)0.0F, (double)0.0F, (double)0.0F, 0.0F, 1.0F);
            }

            RenderLiving.NAME_TAG_RANGE = backup;
        } catch (Throwable var3) {
        }

        GL11.glEnable(3042);
        GL11.glClear(256);
        GL11.glDisable(2929);
        GL11.glColor4f(255.0F, 255.0F, 255.0F, 255.0F);
        GL11.glBlendFunc(770, 771);
        GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
    }

    public DIGuiTools(Minecraft par1Minecraft) {
        super(par1Minecraft);
    }
}
