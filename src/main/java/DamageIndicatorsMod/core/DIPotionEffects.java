package DamageIndicatorsMod.core;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DIPotionEffects implements IMessage {
    int entityID = -1;
    List<PotionEffect> potionEffects = new ArrayList();

    public DIPotionEffects() {
    }

    public DIPotionEffects(EntityLivingBase elb, List<PotionEffect> potionEffects) {
        this.entityID = elb.func_145782_y();
        this.potionEffects = potionEffects;
    }

    public void fromBytes(ByteBuf buf) {
        try {
            this.entityID = buf.readInt();
            int count = buf.readInt();

            for(int i = 0; i < count; ++i) {
                this.potionEffects.add(new PotionEffect(Potion.func_188412_a(buf.readInt()), buf.readInt()));
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    public void toBytes(ByteBuf buf) {
        try {
            buf.writeInt(this.entityID);
            buf.writeInt(this.potionEffects.size());

            for(int i = 0; i < this.potionEffects.size(); ++i) {
                buf.writeInt(Potion.func_188409_a(((PotionEffect)this.potionEffects.get(i)).func_188419_a()));
                buf.writeInt(((PotionEffect)this.potionEffects.get(i)).func_76459_b());
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    public static class Handler implements IMessageHandler<DIPotionEffects, IMessage> {
        public DIPotionEffects onMessage(DIPotionEffects message, MessageContext ctx) {
            if (message.entityID != -1 && !message.potionEffects.isEmpty()) {
                DIEventBus.potionEffects.put(message.entityID, message.potionEffects);
            }

            return null;
        }
    }
}
