package DamageIndicatorsMod.core;

import DamageIndicatorsMod.DIMod;
import DamageIndicatorsMod.configuration.DIConfig;
import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityConfigurationEntry {
    public static HashMap<Integer, Integer> maxHealthOverride = new HashMap(200);
    private static boolean lasttimefailed = false;
    public final boolean AppendBaby;
    public final float BabyScaleFactor;
    public final Class Clazz;
    public final float EntitySizeScaling;
    public float eyeHeight;
    public final boolean IgnoreThisMob;
    public int maxHP;
    public final String NameOverride;
    public final float ScaleFactor;
    public final float XOffset;
    public final float YOffset;
    public final boolean DisableMob;

    public void save() {
        saveEntityConfig(this);
    }

    public static EntityConfigurationEntry generateDefaultConfiguration(Configuration config, Class entry) {
        boolean ignore = false;
        boolean appendBabyName = true;
        float scaleFactor = 22.0F;
        float xOffset = 0.0F;
        float yOffset = -5.0F;
        float SizeModifier = 0.0F;
        float BabyScaleFactor = 2.0F;
        boolean disableMob = false;
        if (entry == EntityIronGolem.class) {
            scaleFactor = 16.0F;
        } else if (entry != EntitySlime.class && entry != EntityMagmaCube.class) {
            if (entry == EntityEnderman.class) {
                scaleFactor = 15.0F;
            } else if (entry == EntityGhast.class) {
                scaleFactor = 7.0F;
                yOffset = -20.0F;
            } else if (entry == EntitySquid.class) {
                yOffset = -17.0F;
            } else if (entry == EntityOcelot.class) {
                scaleFactor = 25.0F;
                yOffset = -5.0F;
            } else if (entry == EntityWither.class) {
                scaleFactor = 15.0F;
                yOffset = 5.0F;
            } else if (EntityPlayer.class.isAssignableFrom(entry)) {
                yOffset = 20.0F;
            } else if (entry.getName().equalsIgnoreCase("thaumcraft.common.entities.EntityWisp")) {
                yOffset = -14.0F;
            } else if (entry.getName().equalsIgnoreCase("drzhark.mocreatures.MoCEntityWerewolf")) {
                scaleFactor = 20.0F;
                yOffset = -4.0F;
            } else if (entry.getName().equalsIgnoreCase("drzhark.mocreatures.MoCEntityOgre")) {
                scaleFactor = 12.0F;
            } else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityCyclops")) {
                scaleFactor = 10.0F;
            } else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityEnergyGolem")) {
                scaleFactor = 10.0F;
            } else if (entry.getName().equalsIgnoreCase("xolova.blued00r.divinerpg.mobs.EntityCaveclops")) {
                scaleFactor = 10.0F;
            } else if (Loader.isModLoaded("RDVehicleTools")) {
                try {
                    Class clazz = Class.forName("net.richdigitsmods.vehiclecore.vehicles.EntityVehicleCore");
                    if (clazz.isAssignableFrom(entry)) {
                        ignore = true;
                    }
                } catch (Throwable var11) {
                }
            }
        } else {
            scaleFactor = 5.0F;
            SizeModifier = 2.0F;
            yOffset = -5.0F;
        }

        return loadEntityConfig(config, new EntityConfigurationEntry(entry, scaleFactor, xOffset, yOffset, SizeModifier, BabyScaleFactor, appendBabyName, "", ignore, 20, 1.5F, disableMob));
    }

    public static EntityConfigurationEntry loadEntityConfig(Configuration config, EntityConfigurationEntry ece) {
        return loadEntityConfig(config, ece, (EntityLiving)null);
    }

    public static EntityConfigurationEntry loadEntityConfig(Configuration config, EntityConfigurationEntry ece, EntityLiving el) {
        Class entry = ece.Clazz;
        String mod = "Vanilla";
        EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(ece.Clazz, true);
        if (er != null) {
            try {
                mod = er.getContainer().getMetadata().name.replaceAll(Pattern.quote("."), "");
            } catch (Throwable var24) {
            }
        }

        String CatagoryName = entry.getName();
        if (CatagoryName.lastIndexOf(".") != -1) {
            CatagoryName = CatagoryName.substring(CatagoryName.lastIndexOf("."), CatagoryName.length());
            CatagoryName = CatagoryName.replaceAll(Pattern.quote("."), "");
        }

        CatagoryName = mod + "." + CatagoryName;
        CatagoryName = CatagoryName.replaceAll("[^a-zA-Z0-9\\s\\!\\:\\.\\&\\$]", "");
        config.addCustomCategoryComment(CatagoryName, "These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
        Property prop = config.get(CatagoryName, "Scale_Factor", String.valueOf(ece.ScaleFactor));

        float scaleFactor;
        try {
            scaleFactor = Float.valueOf(prop.getString());
        } catch (Throwable var23) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            scaleFactor = ece.ScaleFactor;
            prop.set(String.valueOf(22.0F));
        }

        prop = config.get(CatagoryName, "Name", "");
        String entityName = prop.getString();
        prop = config.get(CatagoryName, "Append_Baby_Name", ece.AppendBaby);
        boolean appendBabyName = prop.getBoolean(ece.AppendBaby);
        prop = config.get(CatagoryName, "Ignore_This_Mob", ece.IgnoreThisMob);
        boolean ignore = prop.getBoolean(ece.IgnoreThisMob);
        prop = config.get(CatagoryName, "X_Offset", String.valueOf(ece.XOffset));

        float xOffset;
        try {
            xOffset = Float.valueOf(prop.getString());
        } catch (Throwable var22) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.XOffset));
            xOffset = ece.XOffset;
        }

        prop = config.get(CatagoryName, "Y_Offset", String.valueOf(ece.YOffset));

        float yOffset;
        try {
            yOffset = Float.valueOf(prop.getString());
        } catch (Throwable var21) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.YOffset));
            yOffset = ece.YOffset;
        }

        prop = config.get(CatagoryName, "Size_Modifier", String.valueOf(ece.EntitySizeScaling));

        float SizeModifier;
        try {
            SizeModifier = Float.valueOf(prop.getString());
        } catch (Throwable var20) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.EntitySizeScaling));
            SizeModifier = ece.EntitySizeScaling;
        }

        prop = config.get(CatagoryName, "Baby_Scale_Modifier", (double)ece.BabyScaleFactor);

        float babyScaleFactor;
        try {
            babyScaleFactor = Float.valueOf(prop.getString());
        } catch (Throwable var19) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.BabyScaleFactor));
            babyScaleFactor = ece.BabyScaleFactor;
        }

        boolean disableMob = false;
        prop = config.get(CatagoryName, "Disable_Mob", ece.DisableMob);

        try {
            disableMob = Boolean.valueOf(prop.getString());
        } catch (Throwable var18) {
            System.err.println("Invalid or malformed configuration entry for " + prop.getName());
            prop.set(String.valueOf(ece.DisableMob));
            disableMob = ece.DisableMob;
        }

        EntityConfigurationEntry tmp = new EntityConfigurationEntry(entry, scaleFactor, xOffset, yOffset, SizeModifier, babyScaleFactor, appendBabyName, entityName, ignore, ece.maxHP, ece.eyeHeight, disableMob);
        return tmp;
    }

    public static Configuration getEntityConfiguration() {
        File configfile = new File(DIConfig.mainInstance().CONFIG_FILE.getParentFile(), "DIAdvancedCompatibility.cfg");

        try {
            configfile.createNewFile();
            return new Configuration(configfile);
        } catch (Exception e) {
            if (configfile.exists()) {
                if (!lasttimefailed) {
                    DIMod.log.warn("Per mob configuration file was corrupt! Attempting to purge and recreate...");
                    if (!configfile.delete()) {
                        configfile.deleteOnExit();
                    }

                    lasttimefailed = true;
                    return getEntityConfiguration();
                } else {
                    DIMod.log.warn("Failed to recreate configuration! Configuration should be deleted when minecraft closes.");
                    throw new RuntimeException("DIAdvancedCompatibility was currupt and was unable to recreate the file.");
                }
            } else {
                throw new RuntimeException("Exception while creating " + configfile.getAbsolutePath(), e);
            }
        }
    }

    public static void saveEntityConfig(EntityConfigurationEntry ece) {
        Class entry = ece.Clazz;
        String mod = "Vanilla";
        EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(ece.Clazz, true);
        if (er != null) {
            try {
                mod = er.getContainer().getMetadata().name.replaceAll(Pattern.quote("."), "_");
            } catch (Throwable var6) {
            }
        }

        String CatagoryName = entry.getName();
        if (CatagoryName.lastIndexOf(".") != -1) {
            CatagoryName = CatagoryName.substring(CatagoryName.lastIndexOf("."), CatagoryName.length());
            CatagoryName = CatagoryName.replaceAll(Pattern.quote("."), "");
        }

        CatagoryName = mod + "." + CatagoryName;
        CatagoryName = CatagoryName.replaceAll("[^a-zA-Z0-9\\s\\!\\:\\.\\&\\$]", "");
        Configuration config = getEntityConfiguration();
        config.addCustomCategoryComment(CatagoryName, "These settings are to help other modders and users to make custom mobs fit correctly in the preview window.");
        config.get(CatagoryName, "Scale_Factor", String.valueOf(ece.ScaleFactor)).set(String.valueOf(ece.ScaleFactor));
        if (ece.NameOverride != null && !"".equals(ece.NameOverride)) {
            config.get(CatagoryName, "Name", ece.NameOverride).set(ece.NameOverride);
        } else {
            config.get(CatagoryName, "Name", ece.NameOverride).set("");
        }

        config.get(CatagoryName, "Ignore_This_Mob", ece.IgnoreThisMob).set(ece.IgnoreThisMob);
        config.get(CatagoryName, "Append_Baby_Name", ece.AppendBaby).set(ece.AppendBaby);
        config.get(CatagoryName, "X_Offset", String.valueOf(ece.XOffset)).set((double)ece.XOffset);
        config.get(CatagoryName, "Y_Offset", String.valueOf(ece.YOffset)).set((double)ece.YOffset);
        config.get(CatagoryName, "Size_Modifier", String.valueOf(ece.EntitySizeScaling)).set((double)ece.EntitySizeScaling);
        config.get(CatagoryName, "Baby_Scale_Modifier", String.valueOf(ece.BabyScaleFactor)).set((double)ece.BabyScaleFactor);
        config.get(CatagoryName, "Disable_Mob", String.valueOf(ece.DisableMob)).set(ece.DisableMob);
        config.save();
    }

    public EntityConfigurationEntry(Class clazz, float scale, float xoffset, float yoffset, float sizeScaling, float babyscale, boolean appendBaby, boolean ignoreThisMob, int maxHP, float eyeHeight, boolean disableMob) {
        this(clazz, scale, xoffset, yoffset, sizeScaling, babyscale, appendBaby, "", ignoreThisMob, maxHP, eyeHeight, disableMob);
    }

    public EntityConfigurationEntry(Class clazz, float scale, float xoffset, float yoffset, float sizeScaling, float babyscale, boolean appendBaby, String nameOverride, boolean ignoreThisMob, int maxHP, float eyeHeight, boolean disableMob) {
        this.IgnoreThisMob = ignoreThisMob;
        this.Clazz = clazz;
        this.ScaleFactor = scale;
        this.XOffset = xoffset;
        this.YOffset = yoffset;
        this.EntitySizeScaling = sizeScaling;
        this.BabyScaleFactor = babyscale;
        this.AppendBaby = appendBaby;
        this.DisableMob = disableMob;
        if (nameOverride != null) {
            this.NameOverride = nameOverride;
        } else {
            this.NameOverride = "";
        }

        this.maxHP = maxHP;
        this.eyeHeight = eyeHeight;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return this.hashCode() != obj.hashCode() ? false : obj.toString().equals(this.toString());
        }
    }

    public void SetInfo(int maxh, float eyeh) {
        this.maxHP = maxh;
        this.eyeHeight = eyeh;
    }

    public int hashCode() {
        return (this.Clazz.getName() + "-" + this.NameOverride + "-" + this.ScaleFactor + "-" + this.BabyScaleFactor + "-" + this.EntitySizeScaling + "-" + this.eyeHeight + "-" + this.XOffset + "-" + this.YOffset + this.DisableMob).hashCode();
    }

    public String toString() {
        String eol = System.getProperty("line.separator");
        StringBuilder output = new StringBuilder();
        output.append(eol).append("---------------------------------").append(eol).append("Class Name: ").append(this.Clazz.getName()).append(eol).append("ScaleFactor: ").append(String.valueOf(this.ScaleFactor)).append(eol).append("Name Override: ").append(this.NameOverride).append(eol).append("AppendBabyName: ").append(String.valueOf(this.AppendBaby)).append(eol).append("X Offset: ").append(String.valueOf(this.XOffset)).append(eol).append("Y Offset: ").append(String.valueOf(this.YOffset)).append(eol).append("Size Modifier: ").append(String.valueOf(this.EntitySizeScaling)).append(eol).append("Baby Scale Modifier: ").append(String.valueOf(this.BabyScaleFactor)).append(eol).append("Ignored: ").append(String.valueOf(this.IgnoreThisMob)).append(eol).append("DisableMob: ").append(String.valueOf(this.DisableMob)).append(eol).append("---------------------------------").append(eol);
        return output.toString();
    }
}