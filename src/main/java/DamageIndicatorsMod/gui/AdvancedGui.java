package DamageIndicatorsMod.gui;

import DITextures.AbstractSkin;
import DITextures.EnumSkinPart;
import DamageIndicatorsMod.configuration.DIConfig;
import DamageIndicatorsMod.core.EntityConfigurationEntry;
import DamageIndicatorsMod.core.Tools;
import DamageIndicatorsMod.util.EntityConfigurationEntryComparator;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class AdvancedGui extends GuiScreen {
    private GuiEntityList guiEntityList;
    private List<GuiTextField> textboxes = new ArrayList();
    private boolean entrySelected = false;
    private int selectedEntry = 0;
    private ArrayList<Rectangle> controlLocations = new ArrayList();
    private ArrayList<String[]> controlTooltipText = new ArrayList();
    private GuiToolTip tooltip;
    private int tooltipWidth = 96;
    private int tooltipHeight = 64;
    private Rectangle LastHovered = null;
    private int timeHovered = 0;
    private GuiTextField search;
    private boolean popoffsetting;
    private boolean portraitsetting;
    private EntityConfigurationEntryComparator comparator = new EntityConfigurationEntryComparator();
    private EntityLivingBase tempMob;
    private EntityConfigurationEntry ece;
    private long lasttime = 0L;
    public boolean backwards = false;

    protected void func_73733_a(int par1, int par2, int par3, int par4, int par5, int par6) {
        super.func_73733_a(par1, par2, par3, par4, par5, par6);
    }

    public void func_146281_b() {
        super.func_146281_b();
        DIConfig.mainInstance().popOffsEnabled = this.popoffsetting;
        DIConfig.mainInstance().portraitEnabled = this.portraitsetting;
    }

    public boolean func_73868_f() {
        return true;
    }

    public void func_73866_w_() {
        this.popoffsetting = DIConfig.mainInstance().popOffsEnabled;
        this.portraitsetting = DIConfig.mainInstance().portraitEnabled;
        DIConfig.mainInstance().popOffsEnabled = false;
        DIConfig.mainInstance().portraitEnabled = false;
        GuiEntityList.entities = new ArrayList(Tools.getInstance().getEntityMap().values());
        Iterator<EntityConfigurationEntry> it = GuiEntityList.entities.iterator();

        while(it.hasNext()) {
            EntityConfigurationEntry ece = (EntityConfigurationEntry)it.next();
            if (EntityPlayer.class.isAssignableFrom(ece.Clazz)) {
                it.remove();
            }
        }

        Collections.sort(GuiEntityList.entities, this.comparator);
        this.guiEntityList = new GuiEntityList(this.field_146297_k, 120, this.field_146295_m, 16, this.field_146295_m - 16, 10, 25, this);
        this.field_146289_q.func_175063_a(((EntityConfigurationEntry)GuiEntityList.entities.get(this.selectedEntry)).Clazz.getName(), 225.0F, 160.0F, 10066431);
        this.tooltip = new GuiToolTip(this, this.tooltipWidth, this.tooltipHeight);
        this.tooltip.setCenterVertically(true);
        this.tooltip.setCentered(true);
        this.search = new GuiTextField(5, this.field_146289_q, 11, 5, 115, 10);
        this.search.func_146180_a("Search...");
        this.createTooltips();
        super.func_73866_w_();
    }

    protected void func_73864_a(int par1, int par2, int par3) throws IOException {
        for(GuiTextField gtf : this.textboxes) {
            gtf.func_146192_a(par1, par2, par3);
        }

        this.search.func_146192_a(par1, par2, par3);
        if (this.search.func_146206_l()) {
            if ("Search...".equals(this.search.func_146179_b())) {
                this.search.func_146180_a("");
            }
        } else if ("".equals(this.search.func_146179_b())) {
            this.search.func_146180_a("Search...");
        }

        super.func_73864_a(par1, par2, par3);
    }

    protected void func_73869_a(char par1, int par2) throws IOException {
        for(GuiTextField ec : this.textboxes) {
            ec.func_146201_a(par1, par2);
        }

        if (this.search.func_146206_l()) {
            this.guiEntityList.visibleEntities.clear();
            this.search.func_146201_a(par1, par2);
            if (!"".equals(this.search.func_146179_b())) {
                this.entrySelected = false;
                this.guiEntityList.selectedEntry = 0;

                for(EntityConfigurationEntry ec1 : GuiEntityList.entities) {
                    Map classToStringMapping = Tools.getEntityList();
                    if (ec1.Clazz.getName().toLowerCase().contains(this.search.func_146179_b().toLowerCase())) {
                        this.guiEntityList.visibleEntities.add(ec1);
                    } else if (classToStringMapping.containsKey(ec1.Clazz)) {
                        String temp = ((String)classToStringMapping.get(ec1.Clazz)).toLowerCase();
                        if (temp.contains(this.search.func_146179_b().toLowerCase())) {
                            this.guiEntityList.visibleEntities.add(ec1);
                        }
                    } else if (ec1.NameOverride.toLowerCase().contains(this.search.func_146179_b().toLowerCase())) {
                        this.guiEntityList.visibleEntities.add(ec1);
                    }
                }
            } else {
                this.guiEntityList.visibleEntities.addAll(GuiEntityList.entities);
            }
        }

        super.func_73869_a(par1, par2);
    }

    public void createTooltips() {
        this.controlLocations.clear();
        this.controlTooltipText.clear();
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("Scale Factor"), 32, this.field_146289_q.func_78256_a("Scale Factor"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"How big mob looks in portrait"});
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("X Offset"), 48, this.field_146289_q.func_78256_a("X Offset"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels right."});
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("Y Offset"), 64, this.field_146289_q.func_78256_a("Y Offset"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels down."});
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("Size Scaling"), 80, this.field_146289_q.func_78256_a("Size Scaling"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"For Slime Type Mobs. How much to scale based on size."});
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("Baby Scaling"), 112, this.field_146289_q.func_78256_a("Baby Scaling"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"To make babies bigger in portrait."});
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("Name Override"), 144, this.field_146289_q.func_78256_a("Name Override"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"Use this name instead."});
        this.controlLocations.add(new Rectangle(225, 142, 120, 10));
        this.controlTooltipText.add(new String[]{"Replace name with this text."});
        this.controlLocations.add(new Rectangle(220 - this.field_146289_q.func_78256_a("Full Class Name"), 160, this.field_146289_q.func_78256_a("Full Class Name"), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"Full Class Path For Debugging."});
        this.controlLocations.add(new Rectangle(225, 160, this.field_146289_q.func_78256_a(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(this.selectedEntry)).Clazz.getName()), this.field_146289_q.field_78288_b));
        this.controlTooltipText.add(new String[]{"Full Class Path For Debugging."});
        this.controlLocations.add(new Rectangle(220 - (this.field_146289_q.func_78256_a("Prefix Babies") + 12), 96, this.field_146289_q.func_78256_a("Prefix Babies") + 12, 12));
        this.controlTooltipText.add(new String[]{"Prefix names with baby if a baby."});
        this.controlLocations.add(new Rectangle(300, 186, 80, 20));
        this.controlTooltipText.add(new String[]{"Save all changes."});
        this.controlLocations.add(new Rectangle(225, 30, 120, 10));
        this.controlTooltipText.add(new String[]{"How big mob looks in portrait"});
        this.controlLocations.add(new Rectangle(225, 46, 120, 10));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels right."});
        this.controlLocations.add(new Rectangle(225, 62, 120, 10));
        this.controlTooltipText.add(new String[]{"Nudge this many pixels down."});
        this.controlLocations.add(new Rectangle(225, 78, 120, 10));
        this.controlTooltipText.add(new String[]{"For Slime Type Mobs. How much to scale based on size."});
        this.controlLocations.add(new Rectangle(225, 110, 120, 10));
        this.controlTooltipText.add(new String[]{"To make babies bigger in portrait."});
        this.controlLocations.add(new Rectangle(32, 32, 120, this.field_146295_m - 64));
        this.controlTooltipText.add(new String[]{"Detected Entities. Click to configure."});

        for(int i = 0; i < this.controlLocations.size(); ++i) {
            this.controlTooltipText.set(i, (String[])this.field_146289_q.func_78271_c(((String[])this.controlTooltipText.get(i))[0], this.tooltipWidth - 2).toArray());
        }

    }

    public void func_73863_a(int par1, int par2, float par3) {
        if (this.lasttime == 0L) {
            this.lasttime = System.nanoTime();
        }

        double diff = (double)(System.nanoTime() - this.lasttime) / (double)1000000.0F;
        this.lasttime = System.nanoTime();
        this.func_146278_c(2);
        this.guiEntityList.drawScreen(par1, par2, par3);
        boolean found = false;
        this.search.func_146194_f();
        if (this.entrySelected) {
            if ((this.guiEntityList.visibleEntities.size() > 0 || this.guiEntityList.visibleEntities.get(this.selectedEntry) != null) && (this.ece == null || this.ece != this.guiEntityList.visibleEntities.get(this.selectedEntry))) {
                this.ece = (EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(this.selectedEntry);

                try {
                    if (this.tempMob != null) {
                        this.tempMob.func_70106_y();
                    }

                    try {
                        this.tempMob = (EntityLivingBase)this.ece.Clazz.getConstructor(World.class).newInstance(this.field_146297_k.field_71441_e);
                    } catch (InstantiationException var18) {
                        this.tempMob = null;
                    }
                } catch (Throwable var11) {
                    Logger.getLogger(AdvancedGui.class.getName()).log(Level.SEVERE, (String)null, var11);
                }
            }

            this.field_146289_q.func_175063_a("Scale Factor:", (float)(220 - this.field_146289_q.func_78256_a("Scale Factor")), 32.0F, 16777215);
            this.field_146289_q.func_175063_a("X Offset:", (float)(220 - this.field_146289_q.func_78256_a("X Offset")), 48.0F, 16777215);
            this.field_146289_q.func_175063_a("Y Offset:", (float)(220 - this.field_146289_q.func_78256_a("Y Offset")), 64.0F, 16777215);
            this.field_146289_q.func_175063_a("Size Scaling:", (float)(220 - this.field_146289_q.func_78256_a("Size Scaling")), 80.0F, 16777215);
            this.field_146289_q.func_175063_a("Baby Scaling:", (float)(220 - this.field_146289_q.func_78256_a("Baby Scaling")), 112.0F, 16777215);
            this.field_146289_q.func_175063_a("Name Override:", (float)(220 - this.field_146289_q.func_78256_a("Name Override")), 144.0F, 16777215);
            this.field_146289_q.func_175063_a("Full Class Name:", (float)(220 - this.field_146289_q.func_78256_a("Full Class Name")), 160.0F, 16777215);
            this.field_146289_q.func_175063_a(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(this.selectedEntry)).Clazz.getName(), 225.0F, 160.0F, 10066431);

            for(GuiTextField transparency : this.textboxes) {
                transparency.func_146194_f();
            }

            GL11.glPushMatrix();
            if (this.ece == null) {
                this.ece = (EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(this.selectedEntry);
            }

            String var14 = this.ece.NameOverride;
            if (this.tempMob != null) {
                if (var14 == null || "".equals(var14)) {
                    var14 = this.tempMob.func_70005_c_();
                }
            } else if (var14 == null || "".equals(var14)) {
                Map classToStringMapping = Tools.getEntityList();
                if (classToStringMapping.containsKey(this.ece.Clazz)) {
                    var14 = classToStringMapping.get(this.ece.Clazz).toString();
                } else {
                    var14 = this.ece.Clazz.getName().substring(this.ece.Clazz.getName().lastIndexOf(".") + 1);
                }
            }

            this.field_73735_i += 0.1F;
            GL11.glPushMatrix();
            float var16 = DIConfig.mainInstance().guiScale;
            GL11.glPushAttrib(8192);

            try {
                float headPosX = 150.0F;
                headPosX += (float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX) + (float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) / 2.0F;
                float headPosY = 175.0F;
                headPosY += (float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY) + (float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT) / 2.0F;
                headPosX = (float)par1 - headPosX;
                headPosY = (float)par2 - headPosY;
                float f2 = this.tempMob.field_70761_aq;
                float f3 = this.tempMob.field_70177_z;
                float f4 = this.tempMob.field_70125_A;
                float f5 = this.tempMob.field_70758_at;
                float f6 = this.tempMob.field_70759_as;
                this.tempMob.field_70761_aq = (float)Math.atan((double)(headPosX / 40.0F)) * 20.0F + 35.0F;
                this.tempMob.field_70177_z = (float)Math.atan((double)(headPosX / 40.0F)) * 40.0F;
                this.tempMob.field_70125_A = (float)Math.atan((double)(headPosY / 40.0F)) * 20.0F;
                this.tempMob.field_70759_as = this.tempMob.field_70177_z;
                this.tempMob.field_70758_at = this.tempMob.field_70177_z;
                DIConfig.mainInstance().guiScale = 1.0F;
                DIGuiTools.DrawPortraitSkinned(150, 175, var14, (int)Math.ceil(this.tempMob == null ? (double)0.0F : (double)this.tempMob.func_110143_aJ()), (int)Math.ceil(this.tempMob == null ? (double)0.0F : (double)this.tempMob.func_110138_aP()), this.tempMob);
                this.tempMob.field_70761_aq = f2;
                this.tempMob.field_70177_z = f3;
                this.tempMob.field_70125_A = f4;
                this.tempMob.field_70758_at = f5;
                this.tempMob.field_70759_as = f6;
            } catch (Throwable var17) {
            }

            GL11.glPopAttrib();
            this.field_73735_i += 0.1F;
            DIConfig.mainInstance().guiScale = var16;
            GL11.glPopMatrix();
            super.func_73863_a(par1, par2, par3);
            GL11.glPopMatrix();

            try {
                if (this.controlLocations != null) {
                    for(int lines = 0; lines < this.controlLocations.size(); ++lines) {
                        try {
                            if (((Rectangle)this.controlLocations.get(lines)).contains(par1, par2)) {
                                found = true;
                                int ex = 0;
                                if (this.controlLocations.get(lines) != null && (this.LastHovered == null || this.LastHovered != this.controlLocations.get(lines))) {
                                    this.LastHovered = (Rectangle)this.controlLocations.get(lines);
                                    this.timeHovered = 1;
                                }

                                if (this.timeHovered != 0) {
                                    this.timeHovered += MathHelper.func_76128_c(diff);
                                    if (this.timeHovered > 255) {
                                        this.timeHovered = -2000;
                                        this.backwards = true;
                                    } else if (this.backwards && this.timeHovered > 0) {
                                        this.backwards = false;
                                        this.timeHovered = 0;
                                    }

                                    ex = Math.min(255, Math.abs(this.timeHovered));
                                }

                                ex = MathHelper.func_76141_d((float)ex * 0.75F);
                                this.tooltip.setGlobalAlpha(ex);
                                this.tooltip.HEIGHT = ((String[])this.controlTooltipText.get(lines)).length * (this.field_146289_q.field_78288_b + 2) + 6;
                                this.tooltip.setUpForDraw(par1, par2, (String[])this.controlTooltipText.get(lines));
                                this.tooltip.setDontUseTexture();
                                this.tooltip.drawStrings(this.field_146289_q);
                                break;
                            }
                        } catch (Throwable var20) {
                        }
                    }
                }
            } catch (Throwable var21) {
            }
        }

        if (!found) {
            this.LastHovered = null;
            this.timeHovered = 0;
        }

    }

    public void func_73876_c() {
        this.search.func_146178_a();

        for(GuiTextField gtf : this.textboxes) {
            gtf.func_146178_a();
        }

        super.func_73876_c();
    }

    protected void func_146284_a(GuiButton par1GuiButton) throws IOException {
        EntityConfigurationEntry current = (EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(this.selectedEntry);
        Iterator newEce = this.textboxes.iterator();
        if (par1GuiButton instanceof GuiCheckBox) {
            ((GuiCheckBox)par1GuiButton).toggle();
        } else if (par1GuiButton != null) {
            while(newEce.hasNext()) {
                GuiTextField textbox = (GuiTextField)newEce.next();
                if (textbox != this.textboxes.get(5)) {
                    try {
                        textbox.func_146180_a("" + Float.valueOf(textbox.func_146179_b()));
                    } catch (Throwable var6) {
                        textbox.func_146180_a("0.0");
                    }
                }
            }
        }

        EntityConfigurationEntry newEce1 = new EntityConfigurationEntry(current.Clazz, Float.valueOf(((GuiTextField)this.textboxes.get(0)).func_146179_b()), Float.valueOf(((GuiTextField)this.textboxes.get(1)).func_146179_b()), Float.valueOf(((GuiTextField)this.textboxes.get(2)).func_146179_b()), Float.valueOf(((GuiTextField)this.textboxes.get(3)).func_146179_b()), Float.valueOf(((GuiTextField)this.textboxes.get(4)).func_146179_b()), ((GuiCheckBox)this.field_146292_n.get(1)).isChecked(), ((GuiTextField)this.textboxes.get(5)).func_146179_b(), ((GuiCheckBox)this.field_146292_n.get(0)).isChecked(), current.maxHP, current.eyeHeight, ((GuiCheckBox)this.field_146292_n.get(3)).isChecked());
        if (!current.equals(newEce1)) {
            Tools.getInstance().getEntityMap().put(newEce1.Clazz, newEce1);
            EntityConfigurationEntry.saveEntityConfig(newEce1);
            GuiEntityList.entities = new ArrayList(Tools.getInstance().getEntityMap().values());
            Collections.sort(GuiEntityList.entities, this.comparator);
            this.guiEntityList.visibleEntities.set(this.selectedEntry, newEce1);
        }

        super.func_146284_a(par1GuiButton);
    }

    public FontRenderer getFontRenderer() {
        return this.field_146289_q;
    }

    public void listClickedCallback(int index) {
        this.field_146292_n.clear();
        this.textboxes = new ArrayList();
        this.entrySelected = true;
        this.selectedEntry = index;
        this.field_146292_n.add(0, new GuiCheckBox(0, 220 - (this.field_146289_q.func_78256_a("Ignore Mob") + 12), 14, this.field_146289_q.func_78256_a("Ignore Mob") + 12, 12, "Ignore Mob"));
        ((GuiCheckBox)this.field_146292_n.get(0)).setChecked(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(index)).IgnoreThisMob);
        this.field_146292_n.add(1, new GuiCheckBox(1, 220 - (this.field_146289_q.func_78256_a("Prefix Babies") + 12), 96, this.field_146289_q.func_78256_a("Prefix Babies") + 12, 12, "Prefix Babies"));
        ((GuiCheckBox)this.field_146292_n.get(1)).setChecked(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(index)).AppendBaby);
        this.field_146292_n.add(2, new GuiButton(2, 315, 186, 80, 20, "Save"));
        this.field_146292_n.add(3, new GuiCheckBox(0, 220 - (this.field_146289_q.func_78256_a("Disable Mob") + 12) + 100, 14, this.field_146289_q.func_78256_a("Disable Mob") + 12, 12, "Disable Mob"));
        ((GuiCheckBox)this.field_146292_n.get(3)).setChecked(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(index)).DisableMob);
        this.addTextBoxes(index);
        String Name = "";
        if (((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(index)).NameOverride != null) {
            Name = ((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(index)).NameOverride;
        } else {
            Name = "";
        }

        ((GuiTextField)this.textboxes.get(5)).func_146180_a(Name);
    }

    public void addTextBoxes(int listIndex) {
        this.textboxes.add(0, new GuiTextField(0, this.field_146289_q, 225, 30, 120, 10));
        ((GuiTextField)this.textboxes.get(0)).func_146180_a(String.valueOf(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(listIndex)).ScaleFactor));
        this.textboxes.add(1, new GuiTextField(1, this.field_146289_q, 225, 46, 120, 10));
        ((GuiTextField)this.textboxes.get(1)).func_146180_a(String.valueOf(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(listIndex)).XOffset));
        this.textboxes.add(2, new GuiTextField(2, this.field_146289_q, 225, 62, 120, 10));
        ((GuiTextField)this.textboxes.get(2)).func_146180_a(String.valueOf(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(listIndex)).YOffset));
        this.textboxes.add(3, new GuiTextField(3, this.field_146289_q, 225, 78, 120, 10));
        ((GuiTextField)this.textboxes.get(3)).func_146180_a(String.valueOf(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(listIndex)).EntitySizeScaling));
        this.textboxes.add(4, new GuiTextField(4, this.field_146289_q, 225, 110, 120, 10));
        ((GuiTextField)this.textboxes.get(4)).func_146180_a(String.valueOf(((EntityConfigurationEntry)this.guiEntityList.visibleEntities.get(listIndex)).BabyScaleFactor));
        this.textboxes.add(5, new GuiTextField(5, this.field_146289_q, 225, 142, 120, 10));
    }
}
