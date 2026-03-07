package DamageIndicatorsMod.util;

import DamageIndicatorsMod.core.EntityConfigurationEntry;
import DamageIndicatorsMod.core.Tools;
import java.util.Comparator;
import java.util.Map;

public class EntityConfigurationEntryComparator implements Comparator<EntityConfigurationEntry> {
    public int compare(EntityConfigurationEntry o1, EntityConfigurationEntry o2) {
        Map classToStringMapping = Tools.getEntityList();
        String str1;
        if (classToStringMapping.containsKey(o1.Clazz)) {
            str1 = (String)classToStringMapping.get(o1.Clazz);
        } else {
            str1 = o1.Clazz.getName();
        }

        String str2;
        if (classToStringMapping.containsKey(o2.Clazz)) {
            str2 = (String)classToStringMapping.get(o2.Clazz);
        } else {
            str2 = o2.Clazz.getName();
        }

        return str1.compareTo(str2);
    }
}
