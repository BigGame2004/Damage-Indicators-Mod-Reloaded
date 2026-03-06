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
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && evt.getEntityLiving() != null && evt.getSource() instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect)evt.getSource()).func_76364_f() instanceof EntityArrow) {
            EntityArrow arrow = (EntityArrow)((EntityDamageSourceIndirect)evt.getSource()).func_76364_f();
            if (arrow != null && arrow.func_70241_g()) {
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
            evt.getEntityLiving().func_70106_y();
        }

    }

    private void updateHealth(EntityLivingBase el, int currentHealth) {
        if (healths.containsKey(el.func_145782_y())) {
            int lastHealth = (Integer)healths.get(el.func_145782_y());
            if (lastHealth != currentHealth) {
                int damage = lastHealth - currentHealth;
                double var10004 = el.field_70163_u + (double)el.field_70131_O;
                DIWordParticles customParticle = new DIWordParticles(Minecraft.func_71410_x().field_71441_e, el.field_70165_t, var10004, el.field_70161_v, 0.001, (double)(0.05F * DIConfig.mainInstance().BounceStrength), 0.001, damage);
                if (Minecraft.func_71410_x().field_71439_g.func_70685_l(el)) {
                    customParticle.shouldOnTop = true;
                } else if (Minecraft.func_71410_x().func_71356_B()) {
                    customParticle.shouldOnTop = DIConfig.mainInstance().alwaysRender;
                }

                if (el != Minecraft.func_71410_x().field_71439_g || Minecraft.func_71410_x().field_71474_y.field_74320_O != 0) {
                    Minecraft.func_71410_x().field_71452_i.func_78873_a(customParticle);
                }
            }
        }

        healths.put(el.func_145782_y(), currentHealth);
    }

    @SubscribeEvent
    public void entityDeath(LivingDeathEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && DIConfig.mainInstance().popOffsEnabled) {
            this.updateHealth(evt.getEntityLiving(), 0);
        }

        Object entityID = evt.getEntity().func_145782_y();
        potionEffects.remove((Integer)entityID);
        healths.remove((Integer)entityID);
        enemies.remove((Integer)entityID);
    }

    @SubscribeEvent
    public void livingUpdate(RenderPlayerEvent.Pre evt) {
        EntityLivingBase el = evt.getEntityLiving();
        this.count -= (double)evt.getPartialRenderTick();
        EntityPlayer p = evt.getEntityPlayer();
        if (p != null && p.field_70170_p != null && el instanceof EntityPlayer && DIMod.donators.contains(((EntityPlayer)el).func_70005_c_().trim().toLowerCase()) && (el != p || Minecraft.func_71410_x().field_71474_y.field_74320_O != 0)) {
            if (el.func_70005_c_().equals(Minecraft.func_71410_x().field_71439_g.func_70005_c_())) {
                el = Minecraft.func_71410_x().field_71439_g;
            }

            if (el instanceof AbstractClientPlayer && ((AbstractClientPlayer)el).func_110303_q() == null) {
            }

            if (this.count <= (double)0.0F) {
                this.count = rnd.nextDouble() * (double)10.0F;
                double darkness = rnd.nextDouble() * 0.333;
                double red = Math.min((double)0.75F + darkness, (double)1.0F);
                double green = red * (double)0.75F;
                if (Minecraft.func_71410_x().field_71415_G) {
                    el.field_70170_p.func_175688_a(EnumParticleTypes.REDSTONE, el.field_70165_t + (rnd.nextDouble() * (double)1.25F - (double)1.0F), el.field_70163_u + (rnd.nextDouble() * (double)1.25F - (double)1.5F), el.field_70161_v + (rnd.nextDouble() * (double)1.25F - (double)1.0F), red, green, (double)0.0F, new int[0]);
                }
            }
        }

        if (evt.getEntity().field_70128_L) {
            try {
                potionEffects.remove(evt.getEntity().func_145782_y());
            } catch (Throwable var12) {
            }

            try {
                healths.remove(evt.getEntity().func_145782_y());
            } catch (Throwable var11) {
            }

            try {
                enemies.remove(evt.getEntity().func_145782_y());
            } catch (Throwable var10) {
            }
        }

    }

    @SubscribeEvent
    public void attackEntity(AttackEntityEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && evt.getEntityLiving() != null && evt.getEntityPlayer() != null) {
            boolean flag = evt.getEntityPlayer().field_70143_R > 0.0F && !evt.getEntityPlayer().field_70122_E && !evt.getEntityPlayer().func_70617_f_() && !evt.getEntityPlayer().func_70090_H() && evt.getEntityPlayer().func_184187_bx() == null;
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
                Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString(DIMod.s_sUpdateMessage));
                DIMod.s_sUpdateMessage = "";
            }
        }

        EntityLivingBase el = evt.getEntityLiving();
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            EntityPlayer p = Minecraft.func_71410_x().field_71439_g;
            if (p != null && p.field_70170_p != null && el instanceof EntityPlayer && DIMod.donators.contains(((EntityPlayer)el).func_70005_c_().trim().toLowerCase()) && (el != p || Minecraft.func_71410_x().field_71474_y.field_74320_O != 0)) {
                if (el.func_70005_c_().equals(Minecraft.func_71410_x().field_71439_g.func_70005_c_())) {
                    el = Minecraft.func_71410_x().field_71439_g;
                }

                if (el instanceof AbstractClientPlayer) {
                }

                if (!this.skip) {
                    this.skip = !this.skip;
                    double darkness = rnd.nextDouble() * 0.333;
                    double red = Math.min((double)0.75F + darkness, (double)1.0F);
                    double green = red * (double)0.75F;
                    if (Minecraft.func_71410_x().field_71415_G) {
                        el.field_70170_p.func_175688_a(EnumParticleTypes.REDSTONE, el.field_70165_t + (rnd.nextDouble() * (double)1.25F - (double)1.0F), el.field_70163_u + (rnd.nextDouble() * (double)1.25F - (double)1.5F), el.field_70161_v + (rnd.nextDouble() * (double)1.25F - (double)1.0F), red, green, (double)0.0F, new int[0]);
                    }
                }
            }
        }

        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            if (DIConfig.mainInstance().popOffsEnabled) {
                this.updateHealth(el, MathHelper.func_76123_f(el.func_110143_aJ()));
            }

            if (evt.getEntity().field_70128_L) {
                try {
                    potionEffects.remove(evt.getEntity().func_145782_y());
                } catch (Throwable var12) {
                }

                try {
                    healths.remove(evt.getEntity().func_145782_y());
                } catch (Throwable var11) {
                }

                try {
                    enemies.remove(evt.getEntity().func_145782_y());
                } catch (Throwable var10) {
                }
            }
        }

    }

    @SubscribeEvent
    public void changeDimension(EntityJoinWorldEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            try {
                if (evt.getEntity() == Minecraft.func_71410_x().field_71439_g) {
                    potionEffects.clear();
                    healths.clear();
                    enemies.clear();
                    playerDim = Minecraft.func_71410_x().field_71439_g.field_71093_bK;
                    playerName = Minecraft.func_71410_x().field_71439_g.func_70005_c_();
                }
            } catch (Throwable var3) {
            }
        }

    }

    @SubscribeEvent
    public void mobHurtUs(LivingHurtEvent evt) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && evt.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)evt.getEntity();
            if (player.func_70005_c_().equals(playerName) && evt.getSource() != null && evt.getSource().func_76346_g() != null) {
                enemies.add(evt.getSource().func_76346_g().func_145782_y());
            }
        }

    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void rendergui(RenderGameOverlayEvent.Pre event) {
        EntityPlayer p = Minecraft.func_71410_x().field_71439_g;
        if (p != null && p.field_70170_p != null && DIMod.donators.contains(p.func_70005_c_().trim().toLowerCase()) && Minecraft.func_71410_x().field_71474_y.field_74320_O != 0) {
            if (p instanceof AbstractClientPlayer) {
            }

            if (this.count <= (double)0.0F) {
                this.count = rnd.nextDouble() * (double)10.0F;
                double darkness = rnd.nextDouble() * 0.333;
                double red = Math.min((double)0.75F + darkness, (double)1.0F);
                double green = red * (double)0.75F;
                if (Minecraft.func_71410_x().field_71415_G) {
                    p.field_70170_p.func_175688_a(EnumParticleTypes.REDSTONE, p.field_70165_t + (rnd.nextDouble() * (double)1.25F - (double)1.0F), p.field_70163_u + (rnd.nextDouble() * (double)1.25F - (double)1.5F), p.field_70161_v + (rnd.nextDouble() * (double)1.25F - (double)1.0F), red, green, (double)0.0F, new int[0]);
                }
            }
        }

        if (Minecraft.func_71382_s() && DIClientProxy.kb == null) {
            DIClientProxy.kb = new KeyBinding("key.portaitreposition", 52, "key.categories.ui");
            ClientRegistry.registerKeyBinding(DIClientProxy.kb);
            KeyBinding.func_74508_b();
        }

        if (DIClientProxy.kb.func_151468_f()) {
            RepositionGui gui = new RepositionGui();
            Minecraft.func_71410_x().func_147108_a(gui);
        }

        boolean flag = DIConfig.mainInstance().alternateRenderingMethod && event.getType() == ElementType.CHAT;
        if (!flag) {
            flag = event.getType() == ElementType.PORTAL && !DIConfig.mainInstance().alternateRenderingMethod;
        }

        if (event.getType() == ElementType.BOSSHEALTH && DIConfig.mainInstance().supressBossUI) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
        } else if (flag && Minecraft.func_71410_x().field_71439_g != null) {
            if (Minecraft.func_71410_x().field_71474_y.field_74319_N) {
                LastTargeted = 0;
                return;
            }

            if (Minecraft.func_71410_x().field_71474_y.field_74330_P && DIConfig.mainInstance().DebugHidesWindow) {
                LastTargeted = 0;
                return;
            }

            if (Minecraft.func_71410_x().field_71462_r != null && !(Minecraft.func_71410_x().field_71462_r instanceof GuiChat)) {
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
        if (Minecraft.func_71410_x().field_71439_g != null) {
            EntityLivingBase el = null;
            if (updateSkip-- <= 0) {
                updateSkip = 4;
                el = RaytraceUtil.getClosestLivingEntity(Minecraft.func_71410_x().field_71439_g, (double)DIConfig.mainInstance().mouseoverRange);
                if (el != null && el.func_110143_aJ() <= 0.0F) {
                    el = null;
                }
            }

            if (Minecraft.func_71410_x().field_71439_g.func_70005_c_().contains("rich1051414") && Minecraft.func_71410_x().field_71439_g.func_70093_af()) {
                Entity tmp = RaytraceUtil.getClosestEntity(Minecraft.func_71410_x().field_71439_g, (double)DIConfig.mainInstance().mouseoverRange);
                if (tmp != null && tmp != last) {
                    last = tmp;
                    Minecraft.func_71410_x().field_71439_g.func_145747_a(new TextComponentString(tmp.getClass().getName()));
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
                    LastTargeted = el.func_145782_y();
                }
            }

            if (el != null || LastTargeted != 0 && (DIConfig.mainInstance().portraitLifetime == -1 || tick > (double)0.0F)) {
                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.func_71410_x());
                if (DIConfig.mainInstance().locX > scaledresolution.func_78326_a() - 135) {
                    DIConfig.mainInstance().locX = scaledresolution.func_78326_a() - 135;
                }

                if (DIConfig.mainInstance().locY > scaledresolution.func_78328_b() - 50) {
                    DIConfig.mainInstance().locY = scaledresolution.func_78328_b() - 50;
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
                        el = (EntityLivingBase)Minecraft.func_71410_x().field_71441_e.func_73045_a(LastTargeted);
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

                LastTargeted = el.func_145782_y();
                Class<? extends EntityLivingBase> entityclass = el.getClass();
                EntityConfigurationEntry configentry = (EntityConfigurationEntry)Tools.getInstance().getEntityMap().get(entityclass);
                if (configentry.maxHP == -1 || configentry.eyeHeight == -1.0F) {
                    configentry.eyeHeight = el.func_70047_e();
                    configentry.maxHP = MathHelper.func_76128_c(Math.ceil((double)el.func_110138_aP()));
                }

                if (configentry.maxHP != MathHelper.func_76128_c(Math.ceil((double)el.func_110138_aP()))) {
                    configentry.maxHP = MathHelper.func_76128_c(Math.ceil((double)el.func_110138_aP()));
                }

                String Name = configentry.NameOverride;
                if (el instanceof EntityPlayer) {
                    Name = el.func_70005_c_();
                }

                if (Name != null && !"".equals(Name)) {
                    if (el.func_70631_g_() && configentry.AppendBaby) {
                        Name = "§oBaby " + Name;
                    } else {
                        Name = "§o" + Name;
                    }
                } else {
                    Name = el.func_70005_c_();
                    if (Name.endsWith(".name")) {
                        Name = Name.replace(".name", "");
                        Name = Name.substring(Name.lastIndexOf(".") + 1, Name.length());
                        Name = Name.substring(0, 1).toUpperCase() + Name.substring(1, Name.length());
                    }

                    if (el.func_70631_g_() && configentry.AppendBaby) {
                        Name = "Baby " + Name;
                    }
                }

                GL11.glPushMatrix();
                GL11.glTranslatef((1.0F - DIConfig.mainInstance().guiScale) * (float)DIConfig.mainInstance().locX, (1.0F - DIConfig.mainInstance().guiScale) * (float)DIConfig.mainInstance().locY, 0.0F);
                GL11.glScalef(DIConfig.mainInstance().guiScale, DIConfig.mainInstance().guiScale, DIConfig.mainInstance().guiScale);

                try {
                    DIGuiTools.DrawPortraitSkinned(DIConfig.mainInstance().locX, DIConfig.mainInstance().locY, Name, MathHelper.func_76123_f(el.func_110143_aJ()), MathHelper.func_76123_f(el.func_110138_aP()), el);
                    if (Calendar.getInstance().getWeekYear() + 3 > Calendar.getInstance().getWeeksInWeekYear()) {
                        FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
                        int Y = (Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEY) + 75;
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    }
                } catch (Throwable var10) {
                }

                GL11.glPopMatrix();
                OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
                GL11.glDisableClientState(32888);
                OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

    }

    public List<PotionEffect> getFormattedPotionEffects(EntityLivingBase el) {
        List<PotionEffect> effects = new ArrayList();
        if (el.func_70651_bq() != null && el.func_70651_bq().size() > 0) {
            effects.addAll(el.func_70651_bq());
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
