package DamageIndicatorsMod.util;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RaytraceUtil {
    public static double getDistanceToClosestSolidWall(EntityLivingBase viewEntity, double traceDistance) {
        return getClosestSolidWall(viewEntity, new Vec3d(viewEntity.posX, viewEntity.posY + (double)1.5F, viewEntity.posZ), traceDistance, 0, (double)0.0F);
    }

    public static double getClosestSolidWall(EntityLivingBase viewEntity, Vec3d startPosition, double traceDistance, int count, double offset) {
        if (count++ <= 20 && !(traceDistance - offset <= (double)0.0F)) {
            Vec3d vec31 = viewEntity.getLookVec();
            Vec3d vec32 = startPosition.add(vec31.x * (traceDistance - offset), vec31.y * (traceDistance - offset), vec31.z * (traceDistance - offset));
            RayTraceResult objectMouseOver = viewEntity.world.rayTraceBlocks(startPosition, vec32, false, false, true);
            if (objectMouseOver != null) {
                IBlockState bs = viewEntity.world.getBlockState(objectMouseOver.getBlockPos());
                Block block = bs.getBlock();
                if (block != null) {
                    if (block.getClass().getName().contains("BlockFrame")) {
                        return objectMouseOver.hitVec.distanceTo(new Vec3d(viewEntity.posX, viewEntity.posY + (double)1.5F, viewEntity.posZ));
                    }

                    if (bs.isOpaqueCube() && !block.isAir(bs, viewEntity.world, objectMouseOver.getBlockPos())) {
                        return objectMouseOver.hitVec.distanceTo(new Vec3d(viewEntity.posX, viewEntity.posY + (double)1.5F, viewEntity.posZ));
                    }

                    return getClosestSolidWall(viewEntity, objectMouseOver.hitVec.add(vec31.x, vec31.y, vec31.z), traceDistance, count, objectMouseOver.hitVec.distanceTo(startPosition));
                }
            }

            return traceDistance;
        } else {
            return traceDistance;
        }
    }

    public static RayTraceResult rayTrace(EntityLivingBase viewEntity, double p_70614_1_) {
        Vec3d vec3 = new Vec3d(viewEntity.posX, viewEntity.posY + (double)1.5F, viewEntity.posZ);
        Vec3d vec31 = viewEntity.getLookVec();
        Vec3d vec32 = vec3.add(vec31.x * p_70614_1_, vec31.y * p_70614_1_, vec31.z * p_70614_1_);
        return viewEntity.world.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static boolean isLookingAt(EntityLivingBase viewEntity, double parDistance, float tick, Entity entity) {
        parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
        if (viewEntity != null) {
            World worldObj = viewEntity.world;
            RayTraceResult objectMouseOver = rayTrace(viewEntity, parDistance);
            if (objectMouseOver != null) {
                parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            }

            Vec3d dirVec = viewEntity.getLookVec();
            List<Entity> targettedEntities = worldObj.getEntitiesWithinAABB(Entity.class, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));
            return targettedEntities.contains(entity);
        } else {
            return false;
        }
    }

    public static Entity getClosestEntity(EntityLivingBase viewEntity, double parDistance) {
        try {
            parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            Entity Return = null;
            double closest = parDistance;
            if (viewEntity != null) {
                World worldObj = viewEntity.world;
                RayTraceResult objectMouseOver = rayTrace(viewEntity, parDistance);
                Vec3d playerPosition = new Vec3d(viewEntity.posX, viewEntity.posY + (double)1.5F, viewEntity.posZ);
                if (objectMouseOver != null) {
                    parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
                }

                Vec3d dirVec = viewEntity.getLookVec();
                Vec3d lookFarCoord = playerPosition.add(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);

                for(Entity targettedEntity : worldObj.getEntitiesWithinAABBExcludingEntity(viewEntity, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance))) {
                    if (targettedEntity != null && !targettedEntity.isInvisible()) {
                        double precheck = (double)viewEntity.getDistance(targettedEntity);
                        RayTraceResult mopElIntercept = targettedEntity.getEntityBoundingBox().calculateIntercept(playerPosition, lookFarCoord);
                        if (mopElIntercept != null && precheck < closest) {
                            Return = targettedEntity;
                            closest = precheck;
                        }
                    }
                }
            }

            return Return;
        } catch (Throwable var17) {
            return null;
        }
    }

    public static EntityLivingBase getClosestLivingEntity(EntityLivingBase viewEntity, double parDistance) {
        try {
            parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            EntityLivingBase Return = null;
            double closest = parDistance;
            if (viewEntity != null) {
                World worldObj = viewEntity.world;
                RayTraceResult objectMouseOver = rayTrace(viewEntity, parDistance);
                Vec3d playerPosition = new Vec3d(viewEntity.posX, viewEntity.posY + (double)1.5F, viewEntity.posZ);
                if (objectMouseOver != null) {
                    parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
                }

                Vec3d dirVec = viewEntity.getLookVec();
                Vec3d lookFarCoord = playerPosition.add(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);
                List<EntityLivingBase> targettedEntities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));
                targettedEntities.remove(viewEntity);

                for(EntityLivingBase targettedEntity : targettedEntities) {
                    if (targettedEntity != null && !targettedEntity.isInvisible()) {
                        double precheck = (double)viewEntity.getDistance(targettedEntity);
                        RayTraceResult mopElIntercept = targettedEntity.getEntityBoundingBox().calculateIntercept(playerPosition, lookFarCoord);
                        if (mopElIntercept != null && precheck < closest) {
                            Return = targettedEntity;
                            closest = precheck;
                        }
                    }
                }
            }

            return Return;
        } catch (Throwable var17) {
            return null;
        }
    }
}
