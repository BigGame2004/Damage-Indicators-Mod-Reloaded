package DamageIndicatorsMod.core;

import DITextures.AbstractSkin;
import DITextures.EnumSkinPart;
import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.client.DIClientProxy;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.DIPermissions.Handler;
import DamageIndicatorsMod.gui.DIGuiTools;
import DamageIndicatorsMod.gui.RepositionGui;
import DamageIndicatorsMod.rendering.DIWordParticles;
import DamageIndicatorsMod.util.RaytraceUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

public class DIEventBus {
    public static Random rnd = new Random(1051414L);
    public static HashMap<Integer, Integer> healths = new HashMap();
    public static Map<Integer, Collection<PotionEffect>> potionEffects = new HashMap();
    public static List<Integer> enemies = new ArrayList();
    public static int playerDim = 0;
    public static String playerName = "";
    public static String lastServer = "";
    public static String currentTexturePack = "";
    public static int dim = -2;
    public static int LastTargeted = 0;
    public static boolean searched = false;
    public static double tick = (double)0.0F;
    public static int updateSkip = 4;
    private static long time = -1L;
    double count = (double)5.0F;
    boolean skip = false;
    float test;
    static Entity last;
    public static Map<String, Map<UUID, Long>> potionTimers = new HashMap();

    @SubscribeEvent
    public void arrowNook(LivingHurtEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && evt.getEntityLiving() != null && evt.getSource() instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect)evt.getSource()).getImmediateSource() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow)((EntityDamageSourceIndirect)evt.getSource()).getImmediateSource();
            if (arrow != null && arrow.getIsCritical()) {
                DIMod.proxy.doCritical(evt.getEntityLiving());
            }
        }

    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingDeathEvent evt) {
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent evt) {
        EntityConfigurationEntry configentry = (EntityConfigurationEntry)Tools.getInstance().getEntityMap().get(evt.getEntityLiving().getClass());
        if (configentry != null && configentry.DisableMob) {
            evt.getEntityLiving().setDead();
        }

    }

    private void updateHealth(EntityLivingBase el, int currentHealth) {
        if (healths.containsKey(el.getEntityId())) {
            int lastHealth = (Integer)healths.get(el.getEntityId());
            if (lastHealth != currentHealth) {
                int damage = lastHealth - currentHealth;
                double var10004 = el.posY + (double)el.height;
                DIWordParticles customParticle = new DIWordParticles(Minecraft.getMinecraft().world, el.posX, var10004, el.posZ, 0.001, (double)(0.05F * DIConfig.mainInstance().BounceStrength), 0.001, damage);
                if (Minecraft.getMinecraft().player.canEntityBeSeen(el)) {
                    customParticle.shouldOnTop = true;
                } else if (Minecraft.getMinecraft().isSingleplayer()) {
                    customParticle.shouldOnTop = DIConfig.mainInstance().alwaysRender;
                }

                if (el != Minecraft.getMinecraft().player || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
                    Minecraft.getMinecraft().effectRenderer.addEffect(customParticle);
                }
            }
        }

        healths.put(el.getEntityId(), currentHealth);
    }

    @SubscribeEvent
    public void entityDeath(LivingDeathEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && DIConfig.mainInstance().popOffsEnabled) {
            this.updateHealth(evt.getEntityLiving(), 0);
        }

        Object entityID = evt.getEntity().getEntityId();
        potionEffects.remove((Integer)entityID);
        healths.remove((Integer)entityID);
        enemies.remove((Integer)entityID);
    }

    @SubscribeEvent
    public void livingUpdate(RenderPlayerEvent.Pre evt) {
        EntityLivingBase el = evt.getEntityLiving();
        this.count -= (double)evt.getPartialRenderTick();
        EntityPlayer p = evt.getEntityPlayer();
        if (p != null && p.world != null && el instanceof EntityPlayer && DIMod.donators.contains(((EntityPlayer)el).getName().trim().toLowerCase()) && (el != p || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) {
            if (el.getName().equals(Minecraft.getMinecraft().player.getName())) {
                el = Minecraft.getMinecraft().player;
            }

            if (el instanceof AbstractClientPlayer && ((AbstractClientPlayer)el).getLocationCape() == null) {
            }

            if (this.count <= (double)0.0F) {
                this.count = rnd.nextDouble() * (double)10.0F;
                double darkness = rnd.nextDouble() * 0.333;
                double red = Math.min((double)0.75F + darkness, (double)1.0F);
                double green = red * (double)0.75F;
                if (Minecraft.getMinecraft().inGameHasFocus) {
                    el.world.spawnParticle(EnumParticleTypes.REDSTONE, el.posX + (rnd.nextDouble() * (double)1.25F - (double)1.0F), el.posY + (rnd.nextDouble() * (double)1.25F - (double)1.5F), el.posZ + (rnd.nextDouble() * (double)1.25F - (double)1.0F), red, green, (double)0.0F, new int[0]);
                }
            }
        }

        if (evt.getEntity().isDead) {
            try {
                potionEffects.remove(evt.getEntity().getEntityId());
            } catch (Throwable var12) {
            }

            try {
                healths.remove(evt.getEntity().getEntityId());
            } catch (Throwable var11) {
            }

            try {
                enemies.remove(evt.getEntity().getEntityId());
            } catch (Throwable var10) {
            }
        }

    }

    @SubscribeEvent
    public void attackEntity(AttackEntityEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && evt.getEntityLiving() != null && evt.getEntityPlayer() != null) {
            boolean flag = evt.getEntityPlayer().fallDistance > 0.0F && !evt.getEntityPlayer().onGround && !evt.getEntityPlayer().isOnLadder() && !evt.getEntityPlayer().isInWater() && evt.getEntityPlayer().getRidingEntity() == null;
            if (flag) {
                DIMod.proxy.doCritical(evt.getTarget());
            }
        }

    }

    @SubscribeEvent
    public void livingEvent(LivingEvent.LivingUpdateEvent evt) {
        if (!"".equals(DIMod.s_sUpdateMessage)) {
            if (FMLCommonHandler.instance().getSide().isServer()) {
                DIMod.log.info(DIMod.s_sUpdateMessage);
                DIMod.s_sUpdateMessage = "";
            } else if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(DIMod.s_sUpdateMessage));
                DIMod.s_sUpdateMessage = "";
            }
        }

        EntityLivingBase el = evt.getEntityLiving();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            EntityPlayer p = Minecraft.getMinecraft().player;
            if (p != null && p.world != null && el instanceof EntityPlayer && DIMod.donators.contains(((EntityPlayer)el).getName().trim().toLowerCase()) && (el != p || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0)) {
                if (el.getName().equals(Minecraft.getMinecraft().player.getName())) {
                    el = Minecraft.getMinecraft().player;
                }

                if (el instanceof AbstractClientPlayer) {
                }

                if (!this.skip) {
                    this.skip = !this.skip;
                    double darkness = rnd.nextDouble() * 0.333;
                    double red = Math.min((double)0.75F + darkness, (double)1.0F);
                    double green = red * (double)0.75F;
                    if (Minecraft.getMinecraft().inGameHasFocus) {
                        el.world.spawnParticle(EnumParticleTypes.REDSTONE, el.posX + (rnd.nextDouble() * (double)1.25F - (double)1.0F), el.posY + (rnd.nextDouble() * (double)1.25F - (double)1.5F), el.posZ + (rnd.nextDouble() * (double)1.25F - (double)1.0F), red, green, (double)0.0F, new int[0]);
                    }
                }
            }
        }

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            if (DIConfig.mainInstance().popOffsEnabled) {
                this.updateHealth(el, MathHelper.ceil(el.getHealth()));
            }

            if (evt.getEntity().isDead) {
                try {
                    potionEffects.remove(evt.getEntity().getEntityId());
                } catch (Throwable var12) {
                }

                try {
                    healths.remove(evt.getEntity().getEntityId());
                } catch (Throwable var11) {
                }

                try {
                    enemies.remove(evt.getEntity().getEntityId());
                } catch (Throwable var10) {
                }
            }
        }

    }

    @SubscribeEvent
    public void changeDimension(EntityJoinWorldEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            try {
                if (evt.getEntity() == Minecraft.getMinecraft().player) {
                    potionEffects.clear();
                    healths.clear();
                    enemies.clear();
                    playerDim = Minecraft.getMinecraft().player.dimension;
                    playerName = Minecraft.getMinecraft().player.getName();
                }
            } catch (Throwable var3) {
            }
        }

    }

    @SubscribeEvent
    public void mobHurtUs(LivingHurtEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && evt.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)evt.getEntity();
            if (player.getName().equals(playerName) && evt.getSource() != null && evt.getSource().getTrueSource() != null) {
                enemies.add(evt.getSource().getTrueSource().getEntityId());
            }
        }

    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void rendergui(RenderGameOverlayEvent.Pre event) {
        EntityPlayer p = Minecraft.getMinecraft().player;
        if (p != null && p.world != null && DIMod.donators.contains(p.getName().trim().toLowerCase()) && Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
            if (p instanceof AbstractClientPlayer) {
            }

            if (this.count <= (double)0.0F) {
                this.count = rnd.nextDouble() * (double)10.0F;
                double darkness = rnd.nextDouble() * 0.333;
                double red = Math.min((double)0.75F + darkness, (double)1.0F);
                double green = red * (double)0.75F;
                if (Minecraft.getMinecraft().inGameHasFocus) {
                    p.world.spawnParticle(EnumParticleTypes.REDSTONE, p.posX + (rnd.nextDouble() * (double)1.25F - (double)1.0F), p.posY + (rnd.nextDouble() * (double)1.25F - (double)1.5F), p.posZ + (rnd.nextDouble() * (double)1.25F - (double)1.0F), red, green, (double)0.0F, new int[0]);
                }
            }
        }

        if (Minecraft.isGuiEnabled() && DIClientProxy.kb == null) {
            DIClientProxy.kb = new KeyBinding("key.portaitreposition", 52, "key.categories.ui");
            ClientRegistry.registerKeyBinding(DIClientProxy.kb);
            KeyBinding.resetKeyBindingArrayAndHash();
        }

        if (DIClientProxy.kb.isPressed()) {
            RepositionGui gui = new RepositionGui();
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }

        boolean flag = DIConfig.mainInstance().alternateRenderingMethod && event.getType() == ElementType.CHAT;
        if (!flag) {
            flag = event.getType() == ElementType.PORTAL && !DIConfig.mainInstance().alternateRenderingMethod;
        }

        if (event.getType() == ElementType.BOSSHEALTH && DIConfig.mainInstance().supressBossUI) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        } else if (flag && Minecraft.getMinecraft().player != null) {
            if (Minecraft.getMinecraft().gameSettings.hideGUI) {
                LastTargeted = 0;
                return;
            }

            if (Minecraft.getMinecraft().gameSettings.showDebugInfo && DIConfig.mainInstance().DebugHidesWindow) {
                LastTargeted = 0;
                return;
            }

            if (Minecraft.getMinecraft().currentScreen != null && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
                LastTargeted = 0;
                return;
            }

            if (!searched) {
                Tools.getInstance().giveUpdateInformation();
                Tools.getInstance().scanforEntities();
                searched = true;
            }

            try {
                if (DIConfig.mainInstance().portraitEnabled && !Handler.allDisabled && !Handler.mouseOversDisabled) {
                    if (DIConfig.mainInstance().highCompatibilityMod) {
                        GL11.glPushAttrib(1048575);
                        GL11.glPushClientAttrib(-1);
                    }

                    try {
                        updateMouseOversSkinned(0.5F);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }

                    if (DIConfig.mainInstance().highCompatibilityMod) {
                        GL11.glPopClientAttrib();
                        GL11.glPopAttrib();
                    }
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void updateMouseOversSkinned(float ticks) {
        if (time == -1L) {
            time = System.nanoTime();
        }

        double elapsedTime = (double)(System.nanoTime() - time) / (double)1.0E7F;
        time = System.nanoTime();
        if (Minecraft.getMinecraft().player != null) {
            EntityLivingBase el = null;
            if (updateSkip-- <= 0) {
                updateSkip = 4;
                el = RaytraceUtil.getClosestLivingEntity(Minecraft.getMinecraft().player, (double)DIConfig.mainInstance().mouseoverRange);
                if (el != null && el.getHealth() <= 0.0F) {
                    el = null;
                }
            }

            if (Minecraft.getMinecraft().player.getName().contains("rich1051414") && Minecraft.getMinecraft().player.isSneaking()) {
                Entity tmp = RaytraceUtil.getClosestEntity(Minecraft.getMinecraft().player, (double)DIConfig.mainInstance().mouseoverRange);
                if (tmp != null && tmp != last) {
                    last = tmp;
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString(tmp.getClass().getName()));
                    TextTransfer textTransfer = new TextTransfer();
                    textTransfer.setClipboardContents(tmp.getClass().getName());
                }
            }

            if (el != null) {
                Class<? extends EntityLivingBase> entityclass = el.getClass();
                EntityConfigurationEntry configentry = (EntityConfigurationEntry)Tools.getInstance().getEntityMap().get(entityclass);
                if (configentry == null) {
                    Configuration configfile = EntityConfigurationEntry.getEntityConfiguration();
                    configentry = EntityConfigurationEntry.generateDefaultConfiguration(configfile, entityclass);
                    configentry.save();
                    Tools.getInstance().getEntityMap().put(entityclass, configentry);
                }

                if (configentry.IgnoreThisMob) {
                    el = null;
                } else {
                    LastTargeted = el.getEntityId();
                }
            }

            if (el != null || LastTargeted != 0 && (DIConfig.mainInstance().portraitLifetime == -1 || tick > (double)0.0F)) {
                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                if (DIConfig.mainInstance().locX > scaledresolution.getScaledWidth() - 135) {
                    DIConfig.mainInstance().locX = scaledresolution.getScaledWidth() - 135;
                }

                if (DIConfig.mainInstance().locY > scaledresolution.getScaledHeight() - 50) {
                    DIConfig.mainInstance().locY = scaledresolution.getScaledHeight() - 50;
                }

                if (DIConfig.mainInstance().locX < 0) {
                    DIConfig.mainInstance().locX = 0;
                }

                if (DIConfig.mainInstance().locY < 0) {
                    DIConfig.mainInstance().locY = 0;
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (el == null) {
                    tick -= elapsedTime;

                    try {
                        el = (EntityLivingBase)Minecraft.getMinecraft().world.getEntityByID(LastTargeted);
                    } catch (Throwable var11) {
                        el = null;
                    }

                    if (el == null) {
                        LastTargeted = 0;
                    }
                } else {
                    tick = (double)DIConfig.mainInstance().portraitLifetime;
                }

                if (el == null) {
                    return;
                }

                LastTargeted = el.getEntityId();
                Class<? extends EntityLivingBase> entityclass = el.getClass();
                EntityConfigurationEntry configentry = (EntityConfigurationEntry)Tools.getInstance().getEntityMap().get(entityclass);
                if (configentry.maxHP == -1 || configentry.eyeHeight == -1.0F) {
                    configentry.eyeHeight = el.getEyeHeight();
                    configentry.maxHP = MathHelper.floor(Math.ceil((double)el.getMaxHealth()));
                }

                if (configentry.maxHP != MathHelper.floor(Math.ceil((double)el.getMaxHealth()))) {
                    configentry.maxHP = MathHelper.floor(Math.ceil((double)el.getMaxHealth()));
                }

                String Name = configentry.NameOverride;
                if (el instanceof EntityPlayer) {
                    Name = el.getName();
                }

                if (Name != null && !"".equals(Name)) {
                    if (el.isChild() && configentry.AppendBaby) {
                        Name = "§oBaby " + Name;
                    } else {
                        Name = "§o" + Name;
                    }
                } else {
                    Name = el.getName();
                    if (Name.endsWith(".name")) {
                        Name = Name.replace(".name", "");
                        Name = Name.substring(Name.lastIndexOf(".") + 1, Name.length());
                        Name = Name.substring(0, 1).toUpperCase() + Name.substring(1, Name.length());
                    }

                    if (el.isChild() && configentry.AppendBaby) {
                        Name = "Baby " + Name;
                    }
                }

                GL11.glPushMatrix();
                GL11.glTranslatef((1.0F - DIConfig.mainInstance().guiScale) * (float)DIConfig.mainInstance().locX, (1.0F - DIConfig.mainInstance().guiScale) * (float)DIConfig.mainInstance().locY, 0.0F);
                GL11.glScalef(DIConfig.mainInstance().guiScale, DIConfig.mainInstance().guiScale, DIConfig.mainInstance().guiScale);

                try {
                    DIGuiTools.DrawPortraitSkinned(DIConfig.mainInstance().locX, DIConfig.mainInstance().locY, Name, MathHelper.ceil(el.getHealth()), MathHelper.ceil(el.getMaxHealth()), el);
                    if (Calendar.getInstance().getWeekYear() + 3 > Calendar.getInstance().getWeeksInWeekYear()) {
                        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                        int Y = (Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEY) + 75;
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                } catch (Throwable var10) {
                }

                GL11.glPopMatrix();
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                GL11.glDisableClientState(32888);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

    }

    public List<PotionEffect> getFormattedPotionEffects(EntityLivingBase el) {
        List<PotionEffect> effects = new ArrayList();
        if (el.getActivePotionEffects() != null && el.getActivePotionEffects().size() > 0) {
            effects.addAll(el.getActivePotionEffects());
        }

        return effects;
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            DIMod.proxy.trysendmessage();
        }

    }
}
