package DamageIndicatorsMod.gui;

import DamageIndicatorsMod.core.EntityConfigurationEntry;
import DamageIndicatorsMod.core.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class GuiEntityList extends GuiScrollingList {
    private AdvancedGui parent;
    public static List<EntityConfigurationEntry> entities;
    public List<EntityConfigurationEntry> visibleEntities;
    public int selectedEntry = 0;

    public GuiEntityList(Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight, AdvancedGui parent) {
        super(client, width, height, top, bottom, left, entryHeight);
        this.parent = parent;
        this.visibleEntities = new ArrayList(entities);
    }

    protected int getSize() {
        return this.visibleEntities.size();
    }

    protected void elementClicked(int index, boolean doubleClick) {
        this.selectedEntry = index;
        this.parent.listClickedCallback(index);
    }

    protected boolean isSelected(int index) {
        return this.selectedEntry == index;
    }

    protected void drawBackground() {
        this.parent.drawBackground(2);
    }

    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
        try {
            Map classToStringMapping = Tools.getEntityList();
            String entryName = (String)classToStringMapping.get(((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz);
            if (((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).NameOverride != null && !"".equals(((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).NameOverride)) {
                entryName = ((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).NameOverride;
            } else if (entryName == null || "".equals(entryName)) {
                String[] ModName = ((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz.getName().split(".");
                if (ModName != null && ModName.length > 0) {
                    entryName = ModName[ModName.length - 1];
                } else {
                    entryName = ((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz.getName();
                }
            }

            if (((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz == EntityOtherPlayerMP.class) {
                entryName = "Other Player";
            } else if (((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz == EntityMob.class) {
                this.visibleEntities.remove(listIndex);
            } else if (((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz == EntityLivingBase.class) {
                this.visibleEntities.remove(listIndex);
            } else if (((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz == EntityLiving.class) {
                this.visibleEntities.remove(listIndex);
            }

            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(entryName, this.listWidth - 10), this.left + 3, var3 + 2, 16777215);
            String ModName1 = "Vanilla/Unknown Mod";
            EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(((EntityConfigurationEntry)this.visibleEntities.get(listIndex)).Clazz, true);
            if (er != null) {
                ModName1 = er.getContainer().getName();
            }

            this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(ModName1, this.listWidth - 10), this.left + 3, var3 + 12, 10066431);
        } catch (Throwable var10) {
        }

    }
}
