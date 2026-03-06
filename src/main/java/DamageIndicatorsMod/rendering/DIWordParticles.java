package DamageIndicatorsMod.rendering;

import DamageIndicatorsMod.configuration.DIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DIWordParticles extends Particle {
    private String critical;
    public boolean criticalhit;
    public static DynamicTexture texID;
    public static DIConfig diConfig = DIConfig.mainInstance();
    public int Damage;
    public int curTexID;
    boolean heal;
    boolean grow;
    float ul;
    float ur;
    float vl;
    float vr;
    float locX;
    float locY;
    float locZ;
    float lastPar2;
    float red;
    float green;
    float blue;
    float alpha;
    float yOffset;
    public boolean shouldOnTop;
    public static boolean isOptifinePresent = false;
    FontRenderer fontRenderer;

    public DIWordParticles(World par1World, double par2, double par4, double par6, double par8, double par10, double par12) {
        this(par1World, par2, par4, par6, par8, par10, par12, 0);
        this.criticalhit = true;
        this.field_70545_g = -0.05F;
    }

    public DIWordParticles(World par1World, double par2, double par4, double par6, double par8, double par10, double par12, int damage) {
        super(par1World, par2, par4, par6, par8, par10, par12);
        this.critical = "Critical!";
        this.criticalhit = false;
        this.heal = false;
        this.grow = true;
        this.shouldOnTop = false;
        this.Damage = damage;
        this.func_187115_a(0.2F, 0.2F);
        this.yOffset = this.field_187135_o * 1.1F;
        this.func_187109_b(par2, par4, par6);
        this.field_187129_i = par8;
        this.field_187130_j = par10;
        this.field_187131_k = par12;
        float var15 = MathHelper.func_76133_a(this.field_187129_i * this.field_187129_i + this.field_187130_j * this.field_187130_j + this.field_187131_k * this.field_187131_k);
        this.field_187129_i = this.field_187129_i / (double)var15 * 0.12;
        this.field_187130_j = this.field_187130_j / (double)var15 * 0.12;
        this.field_187131_k = this.field_187131_k / (double)var15 * 0.12;
        this.field_70548_b = 1.5F;
        this.field_70549_c = 1.5F;
        this.field_70545_g = diConfig.Gravity;
        this.field_70544_f = diConfig.Size;
        this.field_70547_e = diConfig.Lifespan;
        this.field_70546_d = 0;
        if (this.Damage < 0) {
            this.heal = true;
            this.Damage = Math.abs(this.Damage);
        }

        try {
            int baseColor = this.heal ? diConfig.healColor : diConfig.DIColor;
            this.red = (float)(baseColor >> 16 & 255) / 255.0F;
            this.green = (float)(baseColor >> 8 & 255) / 255.0F;
            this.blue = (float)(baseColor & 255) / 255.0F;
            this.alpha = diConfig.transparency * 0.9947F;
            this.ul = ((float)this.Damage - (float)MathHelper.func_76141_d((float)this.Damage / 16.0F) * 16.0F) % 16.0F / 16.0F;
            this.ur = this.ul + 0.0624375F;
            this.vl = (float)MathHelper.func_76141_d((float)this.Damage / 16.0F) * 16.0F / 16.0F / 16.0F;
            this.vr = this.vl + 0.0624375F;
        } catch (Throwable var17) {
        }

    }

    public void func_187110_a(double x, double y, double z) {
        super.func_187110_a(x, y, z);
    }

    public void func_180434_a(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        this.shouldOnTop = Minecraft.func_71410_x().field_71439_g.func_70685_l(entityIn);
        double rotationYaw = (double)(-Minecraft.func_71410_x().field_71439_g.field_70177_z);
        double rotationPitch = (double)Minecraft.func_71410_x().field_71439_g.field_70125_A;
        float size = 0.1F * this.field_70544_f;

        try {
            this.locX = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)partialTicks - field_70556_an);
            this.locY = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)partialTicks - field_70554_ao);
            this.locZ = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)partialTicks - field_70555_ap);
            float var10000 = rotationX * size;
            var10000 = rotationZ * size;
            var10000 = rotationYZ * size;
            var10000 = rotationXY * size;
            var10000 = rotationXZ * size;
        } catch (Throwable var15) {
        }

        GL11.glPushMatrix();
        if (this.shouldOnTop) {
            GL11.glDepthFunc(519);
        } else {
            GL11.glDepthFunc(515);
        }

        GL11.glTranslatef(this.locX, this.locY, this.locZ);
        GL11.glRotated(rotationYaw, (double)0.0F, (double)1.0F, (double)0.0F);
        GL11.glRotated(rotationPitch, (double)1.0F, (double)0.0F, (double)0.0F);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScaled((double)this.field_70544_f * 0.008, (double)this.field_70544_f * 0.008, (double)this.field_70544_f * 0.008);
        if (this.criticalhit) {
            GL11.glScaled((double)0.5F, (double)0.5F, (double)0.5F);
        }

        this.fontRenderer = Minecraft.func_71410_x().field_71466_p;
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.criticalhit && DIConfig.mainInstance().showCriticalStrikes) {
            this.renderText(this.critical, (float)this.fontRenderer.func_78256_a(this.critical) / -2.0F, (float)this.fontRenderer.field_78288_b / -2.0F, 204, 0, 0);
        } else if (!this.criticalhit) {
            int color = this.heal ? DIConfig.mainInstance().healColor : DIConfig.mainInstance().DIColor;
            this.renderText(String.valueOf(this.Damage), (float)this.fontRenderer.func_78256_a(this.Damage + "") / -2.0F, (float)this.fontRenderer.field_78288_b / -2.0F, color >> 16 & 255, color >> 8 & 255, color >> 0 & 255);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthFunc(515);
        GL11.glPopMatrix();
        if (this.grow) {
            this.field_70544_f *= 1.08F;
            if ((double)this.field_70544_f > (double)diConfig.Size * (double)3.0F) {
                this.grow = false;
            }
        } else {
            this.field_70544_f *= 0.96F;
        }

    }

    public void renderText(String str, float posX, float posY, int red, int green, int blue) {
        if (DIConfig.mainInstance().useDropShadows) {
            int r = red;
            int g = green;
            int b = blue;
            if (red > green && red > blue) {
                r = 255;
                g = 0;
                b = 0;
            } else if (green > red && green > blue) {
                r = 0;
                g = 255;
                b = 0;
            } else if (blue > red && blue > green) {
                r = 0;
                g = 0;
                b = 255;
            }

            this.fontRenderer.func_78276_b(str, 1, 1, ((int)((double)this.alpha * (double)200.0F) & 255) << 24);
            GL11.glPushMatrix();
            GL11.glTranslated(-0.2, -0.2, (double)0.0F);
            GL11.glScaled(1.075, 1.075, (double)1.0F);
            this.fontRenderer.func_78276_b(str, 0, 0, ((int)((double)this.alpha * (double)64.0F) & 255) << 24 | ((red + r) / 2 & 255) << 16 | ((green + g) / 2 & 255) << 8 | ((blue + b) / 2 & 255) << 0);
            GL11.glPopMatrix();
            this.fontRenderer.func_78276_b(str, 0, 0, ((int)((double)this.alpha * (double)128.0F) & 255) << 24 | ((red + red + r) / 3 & 255) << 16 | ((green + green + g) / 3 & 255) << 8 | ((blue + blue + b) / 3 & 255) << 0);
            GL11.glPushMatrix();
            GL11.glTranslated(0.15, 0.15, (double)0.0F);
            GL11.glScaled(0.95, 0.95, (double)1.0F);
            this.fontRenderer.func_78276_b(str, 0, 0, ((int)((double)this.alpha * (double)255.0F) & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255) << 0);
            GL11.glPopMatrix();
        } else {
            this.fontRenderer.func_78276_b(str, 0, 0, ((int)((double)this.alpha * (double)255.0F) & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255) << 0);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public int func_70537_b() {
        return 3;
    }
}
