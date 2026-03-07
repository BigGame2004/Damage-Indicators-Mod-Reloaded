package DamageIndicatorsMod.server;

import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.DIPermissions;
import DamageIndicatorsMod.core.DIPotionEffects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ServerEventHandler {
    public static Map<String, Map<UUID, Long>> potionTimers = new HashMap();

    public static void sendServerSettings(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            byte toggles = 0;
            toggles = (byte)(toggles + (!DIConfig.mainInstance().portraitEnabled ? 2 : 0));
            toggles = (byte)(toggles + (!DIConfig.mainInstance().enablePotionEffects ? 4 : 0));
            toggles = (byte)(toggles + (!DIConfig.mainInstance().popOffsEnabled ? 8 : 0));
            DIMod.network.sendTo(new DIPermissions(toggles), (EntityPlayerMP)player);
        }

    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            sendServerSettings(event.player);
        }

    }

    @SubscribeEvent
    public void livingEvent(LivingEvent.LivingUpdateEvent evt) {
        EntityLivingBase el = evt.getEntityLiving();
        if (DIConfig.mainInstance().enablePotionEffects && evt.getEntityLiving() != null) {
            Collection<?> potionEffects = el.getActivePotionEffects();
            if (potionEffects != null && !potionEffects.isEmpty()) {
                int offset = MathHelper.floor((float)DIConfig.mainInstance().packetrange / 2.0F);
                AxisAlignedBB aabb = new AxisAlignedBB(el.posX - (double)offset, el.posY - (double)offset, el.posZ - (double)offset, el.posX + (double)offset, el.posY + (double)offset, el.posZ + (double)offset);
                List<EntityPlayer> players = el.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
                if (players != null && !players.isEmpty()) {
                    for(EntityPlayer player : players) {
                        if (potionTimers.get(player.getName()) == null) {
                            potionTimers.put(player.getName(), new WeakHashMap());
                        }

                        Map<UUID, Long> potioneffectstimer = (Map)potionTimers.get(player.getName());
                        if ((!potioneffectstimer.containsKey(el.getPersistentID()) || System.currentTimeMillis() - (Long)potioneffectstimer.get(el.getPersistentID()) > 1000L) && player instanceof EntityPlayerMP) {
                            DIMod.network.sendTo(new DIPotionEffects(el, this.getFormattedPotionEffects(el)), (EntityPlayerMP)player);
                            potioneffectstimer.put(el.getPersistentID(), System.currentTimeMillis());
                        }
                    }
                }
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
}
