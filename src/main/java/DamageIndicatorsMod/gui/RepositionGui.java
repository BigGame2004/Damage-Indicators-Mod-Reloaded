package DamageIndicatorsMod.gui;

import DITextures.AbstractSkin;
import DITextures.EnumSkinPart;
import DITextures.JarSkinRegistration;
import DamageIndicatorsMod.configuration.DIConfig;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class RepositionGui extends GuiScreen {
    private float animationTick = 0.0F;
    private DynamicTexture colorBarTex;
    private final BufferedImage colorBar = new BufferedImage(8, 1536, 1);
    public DIConfig diConfig;
    private DynamicTexture gradientTex;
    private final BufferedImage Gradient = new BufferedImage(256, 256, 1);
    private GuiTextField gtf;
    public boolean mouseDown = false;
    private boolean setDamageColor = false;
    private boolean setHealColor = false;
    private int textWidth;

    protected void func_146284_a(GuiButton par1GuiButton) throws IOException {
        if (par1GuiButton instanceof GuiCheckBox) {
            switch (par1GuiButton.field_146127_k) {
                case 0:
                    ((GuiCheckBox)par1GuiButton).setChecked(!((GuiCheckBox)par1GuiButton).isChecked());
                    this.diConfig.portraitEnabled = ((GuiCheckBox)par1GuiButton).checked;
                    break;
                case 1:
                    ((GuiCheckBox)par1GuiButton).setChecked(!((GuiCheckBox)par1GuiButton).isChecked());
                    this.diConfig.enablePotionEffects = ((GuiCheckBox)par1GuiButton).checked;
                    break;
                case 2:
                    ((GuiCheckBox)par1GuiButton).setChecked(!((GuiCheckBox)par1GuiButton).isChecked());
                    this.diConfig.popOffsEnabled = ((GuiCheckBox)par1GuiButton).checked;
                case 3:
                case 4:
                case 5:
                default:
                    break;
                case 6:
                    ((GuiCheckBox)par1GuiButton).setChecked(!((GuiCheckBox)par1GuiButton).isChecked());
                    this.diConfig.alternateRenderingMethod = ((GuiCheckBox)par1GuiButton).checked;
                    break;
                case 7:
                    ((GuiCheckBox)par1GuiButton).setChecked(!((GuiCheckBox)par1GuiButton).isChecked());
                    this.diConfig.highCompatibilityMod = ((GuiCheckBox)par1GuiButton).checked;
            }
        } else {
            switch (par1GuiButton.field_146127_k) {
                case 3:
                    this.field_146297_k.func_147108_a(new SkinGui((GuiScreen)null, this.field_146297_k.field_71474_y));
                    break;
                case 4:
                    this.field_146297_k.func_147108_a(new AdvancedGui());
                    break;
                case 5:
                    this.field_146297_k.field_71439_g.func_71053_j();
            }
        }

        super.func_146284_a(par1GuiButton);
    }

    public boolean func_73868_f() {
        return true;
    }

    private void drawColorbar() {
        if (this.colorBarTex == null) {
            int locx = 0;

            for(int color = 0; color < 6; ++color) {
                for(int saturation = 0; saturation < 256; ++saturation) {
                    int finalColor;
                    switch (color) {
                        case 0:
                            finalColor = 16711680 | saturation;
                            break;
                        case 1:
                            finalColor = (255 - saturation) * 65536 | 0 | 255;
                            break;
                        case 2:
                            finalColor = 0 | saturation * 256 | 255;
                            break;
                        case 3:
                            finalColor = '\uff00' | 255 - saturation;
                            break;
                        case 4:
                            finalColor = saturation * 65536 | '\uff00' | 0;
                            break;
                        default:
                            finalColor = 16711680 | (255 - saturation) * 256 | 0;
                    }

                    int pos = locx++;

                    for(int i = 0; i < 8; ++i) {
                        this.colorBar.setRGB(i, pos, finalColor);
                    }
                }
            }

            this.colorBarTex = new DynamicTexture(this.colorBar);
        }

    }

    private void drawColorSelector() {
        func_73734_a(-2, -2, 72, 66, -2236963);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.gradientTex.func_110564_a();
        GL11.glBegin(7);
        GL11.glTexCoord2d((double)0.0F, (double)0.0F);
        GL11.glVertex3d((double)0.0F, (double)0.0F, (double)this.field_73735_i);
        GL11.glTexCoord2d((double)0.0F, (double)1.0F);
        GL11.glVertex3d((double)0.0F, (double)64.0F, (double)this.field_73735_i);
        GL11.glTexCoord2d((double)1.0F, (double)1.0F);
        GL11.glVertex3d((double)64.0F, (double)64.0F, (double)this.field_73735_i);
        GL11.glTexCoord2d((double)1.0F, (double)0.0F);
        GL11.glVertex3d((double)64.0F, (double)0.0F, (double)this.field_73735_i);
        GL11.glEnd();
        this.colorBarTex.func_110564_a();
        GL11.glBegin(7);
        GL11.glTexCoord2d((double)0.0F, (double)0.0F);
        GL11.glVertex3d((double)66.0F, (double)0.0F, (double)this.field_73735_i);
        GL11.glTexCoord2d((double)0.0F, (double)1.0F);
        GL11.glVertex3d((double)66.0F, (double)64.0F, (double)this.field_73735_i);
        GL11.glTexCoord2d((double)1.0F, (double)1.0F);
        GL11.glVertex3d((double)70.0F, (double)64.0F, (double)this.field_73735_i);
        GL11.glTexCoord2d((double)1.0F, (double)0.0F);
        GL11.glVertex3d((double)70.0F, (double)0.0F, (double)this.field_73735_i);
        GL11.glEnd();
    }

    private void drawGradient(int startRed, int startGreen, int startBlue) {
        if (startRed >= startBlue && startRed >= startGreen) {
            startRed = 255;
        } else if (startGreen >= startBlue && startGreen >= startRed) {
            startGreen = 255;
        } else {
            startBlue = 255;
        }

        if (startRed <= startBlue && startRed <= startGreen) {
            startRed = 0;
        } else if (startGreen <= startBlue && startGreen <= startRed) {
            startGreen = 0;
        } else {
            startBlue = 0;
        }

        for(int y = 0; y < 256; ++y) {
            for(int x = 0; x < 256; ++x) {
                this.Gradient.setRGB(x, y, -16777216 | (startRed + (255 - startRed) * y / 255) * x / 255 * 65536 | (startGreen + (255 - startGreen) * y / 255) * x / 255 * 256 | (startBlue + (255 - startBlue) * y / 255) * x / 255);
            }
        }

        this.gradientTex = new DynamicTexture(this.Gradient);
    }

    public void func_73863_a(int par1, int par2, float par3) {
        this.func_146276_q_();
        GL11.glPushMatrix();
        ((GuiCheckBox)this.field_146292_n.get(6)).checked = this.diConfig.alternateRenderingMethod;
        ((GuiCheckBox)this.field_146292_n.get(7)).checked = this.diConfig.highCompatibilityMod;
        if (!this.diConfig.portraitEnabled) {
            ((GuiCheckBox)this.field_146292_n.get(1)).field_146124_l = false;
        } else {
            ((GuiCheckBox)this.field_146292_n.get(1)).field_146124_l = true;
            GL11.glPushMatrix();
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glTranslatef((1.0F - DIConfig.mainInstance().guiScale) * (float)DIConfig.mainInstance().locX, (1.0F - DIConfig.mainInstance().guiScale) * (float)DIConfig.mainInstance().locY, 0.0F);
            GL11.glScalef(DIConfig.mainInstance().guiScale, DIConfig.mainInstance().guiScale, 1.0F);
            GL11.glPushAttrib(8192);
            float headPosX = (float)DIConfig.mainInstance().locX;
            headPosX += ((float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWX) + (float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDWIDTH) / 2.0F) * DIConfig.mainInstance().guiScale;
            float headPosY = (float)DIConfig.mainInstance().locY;
            headPosY += ((float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGMOBPREVIEWY) + (float)(Integer)AbstractSkin.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGBACKGROUNDHEIGHT) / 2.0F) * DIConfig.mainInstance().guiScale;
            headPosX = (float)par1 - headPosX;
            headPosY = (float)par2 - headPosY;
            float f2 = this.field_146297_k.field_71439_g.field_70761_aq;
            float f3 = this.field_146297_k.field_71439_g.field_70177_z;
            float f4 = this.field_146297_k.field_71439_g.field_70125_A;
            float f5 = this.field_146297_k.field_71439_g.field_70758_at;
            float f6 = this.field_146297_k.field_71439_g.field_70759_as;
            this.field_146297_k.field_71439_g.field_70761_aq = (float)Math.atan((double)(headPosX / 40.0F)) * 20.0F + 35.0F;
            this.field_146297_k.field_71439_g.field_70177_z = (float)Math.atan((double)(headPosX / 40.0F)) * 40.0F;
            this.field_146297_k.field_71439_g.field_70125_A = (float)Math.atan((double)(headPosY / 40.0F)) * 20.0F;
            this.field_146297_k.field_71439_g.field_70759_as = this.field_146297_k.field_71439_g.field_70177_z;
            this.field_146297_k.field_71439_g.field_70758_at = this.field_146297_k.field_71439_g.field_70177_z;
            Minecraft.func_71410_x().func_175598_ae().field_78735_i = 180.0F;
            DIGuiTools.DrawPortraitSkinned(this.diConfig.locX, this.diConfig.locY, this.field_146297_k.field_71439_g.func_70005_c_(), (int)Math.ceil((double)this.field_146297_k.field_71439_g.func_110138_aP()), (int)Math.ceil((double)this.field_146297_k.field_71439_g.func_110143_aJ()), this.field_146297_k.field_71439_g);
            this.field_146297_k.field_71439_g.field_70761_aq = f2;
            this.field_146297_k.field_71439_g.field_70177_z = f3;
            this.field_146297_k.field_71439_g.field_70125_A = f4;
            this.field_146297_k.field_71439_g.field_70758_at = f5;
            this.field_146297_k.field_71439_g.field_70759_as = f6;
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        if (this.animationTick >= 1.0F) {
            this.animationTick = -5.0F;
        }

        this.animationTick += 0.01F;
        GL11.glTranslatef((float)(this.field_146294_l / 2 + 30 - this.textWidth / 2), (float)(this.field_146295_m / 2 - 30), 0.0F);
        func_73734_a(0, 0, 30, 20, 1996488704);
        func_73734_a(0, 2, 30, 0, -1441726384);
        func_73734_a(0, 22, 30, 20, -1441726384);
        func_73734_a(0, 0, 2, 22, -1441726384);
        func_73734_a(28, 0, 30, 22, -1441726384);
        GL11.glTranslatef(32.0F, 25.0F, 0.0F);
        func_73734_a(0, 0, 15, 13, -16777216 | this.diConfig.DIColor);
        if (this.setDamageColor) {
            func_73734_a(0, 2, 15, 0, -2236963);
            func_73734_a(0, 15, 15, 13, -2236963);
            func_73734_a(0, 0, 2, 15, -2236963);
            func_73734_a(13, 0, 15, 15, -2236963);
            GL11.glTranslatef(17.0F, -31.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawColorSelector();
            GL11.glTranslatef(-17.0F, 31.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, 20.0F, 0.0F);
        func_73734_a(0, 0, 15, 15, -16777216 | this.diConfig.healColor);
        if (this.setHealColor) {
            func_73734_a(0, 2, 15, 0, -2236963);
            func_73734_a(0, 15, 15, 13, -2236963);
            func_73734_a(0, 0, 2, 15, -2236963);
            func_73734_a(13, 0, 15, 15, -2236963);
            GL11.glTranslatef(17.0F, -51.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawColorSelector();
            GL11.glTranslatef(-17.0F, 51.0F, 0.0F);
        }

        GL11.glPopMatrix();
        boolean mouseOver = false;
        boolean mouseOver2 = false;
        if (par1 > this.diConfig.locX && par1 < this.diConfig.locX + (Integer)JarSkinRegistration.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEWIDTH) && par2 > this.diConfig.locY && par2 < this.diConfig.locY + (Integer)JarSkinRegistration.getActiveSkin().getSkinValue(EnumSkinPart.CONFIGFRAMEHEIGHT)) {
            mouseOver = true;
        }

        if (par1 > ((GuiCheckBox)this.field_146292_n.get(7)).field_146128_h - 20 && par2 > ((GuiCheckBox)this.field_146292_n.get(7)).field_146129_i - 20 && par1 <= ((GuiCheckBox)this.field_146292_n.get(7)).getWidth()) {
            mouseOver2 = true;
        }

        if (this.mouseDown) {
            mouseOver = false;
            this.diConfig.locX = par1;
            this.diConfig.locY = par2;
        }

        this.field_146289_q.func_175063_a("Gui Scale:", (float)(this.field_146294_l / 2 - (this.textWidth + this.gtf.func_146200_o() + 8) / 2), (float)(this.field_146295_m / 2 - 24), 16777215);
        this.field_146289_q.func_175063_a("Damage Color:", (float)(this.field_146294_l / 2 - this.field_146289_q.func_78256_a("Damage Color:") / 2), (float)(this.field_146295_m / 2 - 2), 16777215);
        this.field_146289_q.func_175063_a("Heal Color:", (float)(this.field_146294_l / 2 - this.field_146289_q.func_78256_a("Heal Color:") / 2), (float)(this.field_146295_m / 2 + 18), 16777215);
        this.gtf.func_146194_f();
        this.field_146289_q.func_175063_a("%", (float)(this.field_146294_l / 2 + 42 - this.textWidth / 2 - 6 + this.gtf.func_146200_o()), (float)(this.field_146295_m / 2 - 24), 16777215);
        ((GuiCheckBox)this.field_146292_n.get(0)).setChecked(this.diConfig.portraitEnabled);
        ((GuiCheckBox)this.field_146292_n.get(1)).setChecked(this.diConfig.enablePotionEffects);
        ((GuiCheckBox)this.field_146292_n.get(2)).setChecked(this.diConfig.popOffsEnabled);
        super.func_73863_a(par1, par2, par3);
        GL11.glDepthFunc(519);
        if (mouseOver) {
            GL11.glPushMatrix();
            this.field_146289_q.func_78256_a("<Drag Me>");
            GL11.glTranslatef((float)par1, (float)par2, 0.0F);
            func_73734_a(0, 0, 60, 20, 1996488704);
            func_73734_a(0, 2, 60, 0, -1441726384);
            func_73734_a(0, 22, 60, 20, -1441726384);
            func_73734_a(0, 0, 2, 22, -1441726384);
            func_73734_a(58, 0, 60, 22, -1441726384);
            this.field_146289_q.func_78276_b("<Drag Me>", 7, 7, -1429418804);
            GL11.glPopMatrix();
        }

        if (mouseOver2) {
            GL11.glPushMatrix();
            this.field_146289_q.func_78256_a("This option may decrease performance.");
            GL11.glTranslatef((float)par1, (float)(par2 - 22), 0.0F);
            func_73734_a(0, 0, 210, 20, 1996488704);
            func_73734_a(0, 2, 210, 0, -1441726384);
            func_73734_a(0, 22, 210, 20, -1441726384);
            func_73734_a(0, 0, 2, 22, -1441726384);
            func_73734_a(208, 0, 210, 22, -1441726384);
            this.field_146289_q.func_78276_b("This option may decrease performance.", 7, 7, -1429418804);
            GL11.glPopMatrix();
        }

        GL11.glDepthFunc(515);
        GL11.glEnable(2929);
    }

    public void func_73866_w_() {
        super.func_73866_w_();
        this.diConfig = DIConfig.mainInstance();
        int enablePortrait = this.field_146289_q.func_78256_a("Enable Portrait") + 12;
        this.field_146292_n.add(0, new GuiCheckBox(0, this.field_146294_l / 2 - enablePortrait / 2, this.field_146295_m / 2 - 66, enablePortrait, 16, "Enable Portrait"));
        ((GuiCheckBox)this.field_146292_n.get(0)).setChecked(this.diConfig.portraitEnabled);
        int enablePotionEffects = this.field_146289_q.func_78256_a("Enable PotionEffects") + 12;
        this.field_146292_n.add(1, new GuiCheckBox(1, this.field_146294_l / 2 - enablePotionEffects / 2, this.field_146295_m / 2 - 52, enablePotionEffects, 16, "Enable PotionEffects"));
        ((GuiCheckBox)this.field_146292_n.get(1)).setChecked(true);
        int enablePopOffsWidth = this.field_146289_q.func_78256_a("Enable PopOffs") + 12;
        this.field_146292_n.add(2, new GuiCheckBox(2, this.field_146294_l / 2 - enablePopOffsWidth / 2, this.field_146295_m / 2 - 38, enablePopOffsWidth, 16, "Enable PopOffs"));
        ((GuiCheckBox)this.field_146292_n.get(2)).setChecked(true);
        int enableSkinWidth = this.field_146289_q.func_78256_a("Select Skin...") + 8;
        this.field_146292_n.add(3, new GuiButton(3, this.field_146294_l / 2 - enableSkinWidth / 2, this.field_146295_m / 2 + 34, enableSkinWidth, 20, "Select Skin..."));
        ((GuiButton)this.field_146292_n.get(3)).field_146124_l = true;
        int AdvancedWidth = this.field_146289_q.func_78256_a("Advanced") + 8;
        this.field_146292_n.add(4, new GuiButton(4, this.field_146294_l - AdvancedWidth - 4, this.field_146295_m - 24, AdvancedWidth, 20, "Advanced"));
        this.field_146292_n.add(5, new GuiButton(5, this.field_146294_l - 24, 4, 20, 20, "X"));
        enablePortrait = this.field_146289_q.func_78256_a("Alternate Render Method") + 12;
        this.field_146292_n.add(6, new GuiCheckBox(6, this.field_146294_l / 2 - enablePortrait / 2, this.field_146295_m / 2 - 80, enablePortrait, 16, "Alternate Render Method"));
        this.field_146292_n.add(7, new GuiCheckBox(7, 5, this.field_146295_m - 12, this.field_146289_q.func_78256_a("High Compatibility Rendering") + 12, 16, "High Compatibility Rendering"));
        this.textWidth = this.field_146289_q.func_78256_a("Gui Scale") + 8;
        this.gtf = new GuiTextField(8, this.field_146289_q, this.field_146294_l / 2 + 40 - this.textWidth / 2, this.field_146295_m / 2 - 24, 30, 20);
        this.gtf.func_146180_a(String.valueOf(MathHelper.func_76141_d(this.diConfig.guiScale * 100.0F)));
        this.gtf.func_146203_f(3);
        this.gtf.func_146185_a(false);
        this.gtf.func_146189_e(true);
        this.drawColorbar();
        this.drawGradient(255, 0, 255);
        GL11.glClear(256);
    }

    protected void func_73869_a(char par1, int par2) throws IOException {
        if (par2 != 14 && par2 != 211) {
            if (Character.isDigit(par1)) {
                this.gtf.func_146201_a(par1, par2);
                int setVal = Integer.valueOf(this.gtf.func_146179_b());
                if (setVal > 200) {
                    int p = this.gtf.func_146198_h();
                    this.gtf.func_146180_a("200");
                    this.gtf.func_146190_e(p);
                }
            }
        } else {
            this.gtf.func_146201_a(par1, par2);
            super.func_73869_a(par1, par2);
            if (this.gtf.func_146179_b().length() == 0) {
                this.gtf.func_146180_a("0");
                this.gtf.func_146196_d();
                this.gtf.func_146199_i(1);
            }
        }

        this.diConfig.guiScale = Float.valueOf(this.gtf.func_146179_b()) / 100.0F;
        super.func_73869_a(par1, par2);
    }

    protected void func_73864_a(int par1, int par2, int par3) throws IOException {
        if (par3 == 0) {
            try {
                if (par2 >= this.field_146295_m / 2 - 36 && par2 <= this.field_146295_m / 2 + 28) {
                    if (this.setDamageColor) {
                        if (par1 >= this.field_146294_l / 2 + 53 && par1 <= this.field_146294_l / 2 + 116) {
                            int ex1 = par1 - (this.field_146294_l / 2 + 53);
                            int y = par2 - (this.field_146295_m / 2 - 36);
                            int pixelcolor = this.Gradient.getRGB(ex1 * 4, y * 4);
                            this.diConfig.DIColor = pixelcolor;
                            this.setDamageColor = false;
                            return;
                        }

                        if (par1 >= this.field_146294_l / 2 + 119 && par1 <= this.field_146294_l / 2 + 123) {
                            byte ex = 1;
                            int y = par2 - (this.field_146295_m / 2 - 36);
                            int pixelcolor = this.colorBar.getRGB(ex, y * (this.colorBar.getHeight() / 64));
                            this.drawGradient(pixelcolor >> 16 & 255, pixelcolor >> 8 & 255, pixelcolor & 255);
                        }
                    } else if (this.setHealColor) {
                        if (par1 >= this.field_146294_l / 2 + 53 && par1 <= this.field_146294_l / 2 + 116) {
                            int ex1 = par1 - (this.field_146294_l / 2 + 53);
                            int y = par2 - (this.field_146295_m / 2 - 36);
                            int pixelcolor = this.Gradient.getRGB(ex1 * 4, y * 4);
                            this.diConfig.healColor = pixelcolor;
                            this.setHealColor = false;
                            return;
                        }

                        if (par1 >= this.field_146294_l / 2 + 119 && par1 <= this.field_146294_l / 2 + 123) {
                            byte ex = 1;
                            int y = par2 - (this.field_146295_m / 2 - 36);
                            int pixelcolor = this.colorBar.getRGB(ex, y * (this.colorBar.getHeight() / 64));
                            this.drawGradient(pixelcolor >> 16 & 255, pixelcolor >> 8 & 255, pixelcolor & 255);
                            return;
                        }
                    }
                }

                if (par1 >= this.field_146294_l / 2 + 30 - this.textWidth / 2 + 30 && par1 <= this.field_146294_l / 2 + 30 - this.textWidth / 2 + 30 + 15) {
                    if (par2 >= this.field_146295_m / 2 - 5 && par2 <= this.field_146295_m / 2 + 10) {
                        this.setDamageColor = true;
                        this.setHealColor = false;
                        this.drawGradient(this.diConfig.DIColor >> 16 & 255, this.diConfig.DIColor >> 8 & 255, this.diConfig.DIColor & 255);
                    } else if (par2 >= this.field_146295_m / 2 - 25 && par2 <= this.field_146295_m / 2 + 30) {
                        this.setHealColor = true;
                        this.setDamageColor = false;
                        this.drawGradient(this.diConfig.healColor >> 16 & 255, this.diConfig.healColor >> 8 & 255, this.diConfig.healColor & 255);
                    }
                }
            } catch (Throwable var8) {
            }

            if (par1 >= this.diConfig.locX - 1 && par1 <= this.diConfig.locX + 137 && par2 >= this.diConfig.locY - 1 && par2 <= this.diConfig.locY + 52) {
                this.mouseDown = true;
            }
        }

        this.gtf.func_146192_a(par1, par2, par3);
        super.func_73864_a(par1, par2, par3);
    }

    protected void func_146286_b(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.mouseDown = false;
        }

        super.func_146286_b(mouseX, mouseY, state);
    }

    public void func_146281_b() {
        DIConfig.overrideConfigAndSave(this.diConfig);
        super.func_146281_b();
    }

    public void func_73876_c() {
        this.gtf.func_146178_a();
        super.func_73876_c();
    }
}
