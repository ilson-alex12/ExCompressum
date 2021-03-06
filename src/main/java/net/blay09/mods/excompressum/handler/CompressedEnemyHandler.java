package net.blay09.mods.excompressum.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.excompressum.ExCompressum;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class CompressedEnemyHandler {

    @SubscribeEvent
    public void onSpawnEntity(EntityJoinWorldEvent event) {
        if(!event.world.isRemote && event.entity instanceof EntityCreature) {
            String entityName = EntityList.getEntityString(event.entity);
            if(ExCompressum.compressedMobs.contains(entityName)) {
                if (event.entity.worldObj.rand.nextFloat() <= ExCompressum.compressedMobChance && !event.entity.getEntityData().getCompoundTag("ExCompressum").hasKey("NoCompress") && !event.entity.getEntityData().getCompoundTag("ExCompressum").hasKey("Compressed")) {
                    ((EntityCreature) event.entity).setAlwaysRenderNameTag(true);
                    ((EntityCreature) event.entity).setCustomNameTag("Compressed " + event.entity.getCommandSenderName());
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    tagCompound.setBoolean("Compressed", true);
                    event.entity.getEntityData().setTag("ExCompressum", tagCompound);
                } else {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    tagCompound.setBoolean("NoCompress", true);
                    event.entity.getEntityData().setTag("ExCompressum", tagCompound);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if(!event.entity.worldObj.isRemote && event.entity.getEntityData().getCompoundTag("ExCompressum").hasKey("Compressed")) {
            if(event.entity instanceof EntityCreature) {
                if(event.source.getEntity() instanceof EntityPlayer && !(event.source.getEntity() instanceof FakePlayer)) {
                    if(EnchantmentHelper.getSilkTouchModifier((EntityLivingBase) event.source.getEntity())) {
                        return;
                    }
                    int entityId = EntityList.getEntityID(event.entity);
                    for(int i = 0; i < ExCompressum.compressedMobSize; i++) {
                        EntityLivingBase entity = (EntityLivingBase) EntityList.createEntityByID(entityId, event.entity.worldObj);
                        if(((EntityLivingBase) event.entity).isChild()) {
                            if(entity instanceof EntityZombie) {
                                ((EntityZombie) entity).setChild(true);
                            } else if(entity instanceof EntityAgeable) {
                                ((EntityAgeable) entity).setGrowingAge(((EntityAgeable) event.entity).getGrowingAge());
                            }
                        }
                        if(entity instanceof EntityPigZombie) {
                            entity.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
                        } else if(entity instanceof EntitySkeleton) {
                            if(((EntitySkeleton) entity).getSkeletonType() == 0) {
                                entity.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
                            } else {
                                entity.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
                            }
                        }
                        NBTTagCompound tagCompound = new NBTTagCompound();
                        tagCompound.setBoolean("NoCompress", true);
                        entity.getEntityData().setTag("ExCompressum", tagCompound);
                        entity.setPositionAndUpdate(event.entity.posX, event.entity.posY + 1, event.entity.posZ);
                        double motion = 0.01;
                        entity.motionX = (event.entity.worldObj.rand.nextGaussian() - 0.5) * motion;
                        entity.motionY = 0;
                        entity.motionZ = (event.entity.worldObj.rand.nextGaussian() - 0.5) * motion;
                        event.entity.worldObj.spawnEntityInWorld(entity);
                    }
                }
            }
        }
    }

}
