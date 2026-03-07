package DITextures;

public enum EnumSkinPart {
    FRAMENAME((Object)null, "/DITextures/Default/DIFramSkin.png"),
    FRAMEID((Object)null, (Object)null),
    TYPEICONSNAME((Object)null, "/DITextures/Default/DITypeIcons.png"),
    TYPEICONSID((Object)null, (Object)null),
    DAMAGENAME((Object)null, "/DITextures/Default/damage.png"),
    DAMAGEID((Object)null, (Object)null),
    HEALTHNAME((Object)null, "/DITextures/Default/health.png"),
    HEALTHID((Object)null, (Object)null),
    BACKGROUNDNAME((Object)null, "/DITextures/Default/background.png"),
    BACKGROUNDID((Object)null, (Object)null),
    NAMEPLATENAME((Object)null, "/DITextures/Default/NamePlate.png"),
    NAMEPLATEID((Object)null, (Object)null),
    LEFTPOTIONNAME((Object)null, "/DITextures/Default/leftPotions.png"),
    LEFTPOTIONID((Object)null, (Object)null),
    RIGHTPOTIONNAME((Object)null, "/DITextures/Default/rightPotions.png"),
    RIGHTPOTIONID((Object)null, (Object)null),
    CENTERPOTIONNAME((Object)null, "/DITextures/Default/centerPotions.png"),
    CENTERPOTIONID((Object)null, (Object)null),
    ORDERING((Object)null, Ordering.values()),
    CONFIGHEALTHBARHEIGHT("HealthBarHeight", 17),
    CONFIGHEALTHBARWIDTH("HealthBarWidth", 112),
    CONFIGHEALTHBARX("HealthBarXOffset", 49),
    CONFIGHEALTHBARY("HealthBarYOffset", 13),
    CONFIGFRAMEHEIGHT("FrameHeight", 64),
    CONFIGFRAMEWIDTH("FrameWidth", 178),
    CONFIGFRAMEX("FrameXOffset", -15),
    CONFIGFRAMEY("FrameYOffset", -5),
    CONFIGBACKGROUNDHEIGHT("BackgroundHeight", 51),
    CONFIGBACKGROUNDWIDTH("BackgroundWidth", 49),
    CONFIGBACKGROUNDX("BackgroundXOffset", -4),
    CONFIGBACKGROUNDY("BackgroundYOffset", -4),
    CONFIGNAMEPLATEHEIGHT("NamePlateHeight", 12),
    CONFIGNAMEPLATEWIDTH("NamePlateWidth", 112),
    CONFIGNAMEPLATEX("NamePlateXOffset", 49),
    CONFIGNAMEPLATEY("NamePlateYOffset", 0),
    CONFIGMOBTYPEHEIGHT("MobTypeSizeHeight", 18),
    CONFIGMOBTYPEWIDTH("MobTypeSizeWidth", 18),
    CONFIGMOBTYPEX("MobTypeOffsetX", -13),
    CONFIGMOBTYPEY("MobTypeOffsetY", 39),
    CONFIGPOTIONBOXHEIGHT("PotionBoxHeight", 22),
    CONFIGPOTIONBOXWIDTH("PotionBoxSidesWidth", 4),
    CONFIGPOTIONBOXX("PotionBoxOffsetX", 48),
    CONFIGPOTIONBOXY("PotionBoxOffsetY", 31),
    CONFIGMOBPREVIEWX("MobPreviewOffsetX", -4),
    CONFIGMOBPREVIEWY("MobPreviewOffsetY", -3),
    CONFIGTEXTEXTNAMECOLOR("NameTextColor", "FFFFFF"),
    CONFIGTEXTEXTHEALTHCOLOR("HealthTextColor", "FFFFFF"),
    CONFIGDISPLAYNM("SkinName", "Clean"),
    CONFIGAUTHOR("Author", "rich1051414"),
    INTERNAL((Object)null, "/DITextures/Default/");

    private final Object ext;
    private final Object extDefault;

    private EnumSkinPart(Object extended, Object configDefault) {
        this.ext = extended;
        this.extDefault = configDefault;
    }

    public final Object getExtended() {
        return this.ext;
    }

    public final Object getConfigDefault() {
        return this.extDefault;
    }
}
