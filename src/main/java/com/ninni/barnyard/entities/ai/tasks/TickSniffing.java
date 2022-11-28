package com.ninni.barnyard.entities.ai.tasks;

import static com.ninni.barnyard.Barnyard.MOD_ID;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.init.BarnyardMemoryModules;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;

public class TickSniffing extends Behavior<BarnyardPig> {
    private static final ResourceLocation DIGGING_LOOT = new ResourceLocation(MOD_ID, "entities/pig_digging");

    public TickSniffing(int duration) {
        super(ImmutableMap.of(MemoryModuleType.IS_SNIFFING, MemoryStatus.VALUE_PRESENT, MemoryModuleType.SNIFF_COOLDOWN, MemoryStatus.VALUE_ABSENT, BarnyardMemoryModules.PIG_SNIFFING_TICKS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), duration);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, BarnyardPig pig, long l) {
        return true;
    }

    protected void createParticles(ServerLevel level, BarnyardPig pig) {
        Vec3 look = pig.getLookAngle().multiply(0.6, 0, 0.6);
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, pig.getBlockStateOn()), pig.getX() + look.x(), pig.getY() + 0.1, pig.getZ() + look.z(), 1, 0.2, 0.1, 0.2, 0);
    }

    protected void spawnItem(ServerLevel level, BarnyardPig pig) {
        Vec3 look = pig.getLookAngle().multiply(0.6, 0, 0.6);
        List<ItemStack> items = level.getServer().getLootTables().get(DIGGING_LOOT).getRandomItems(new LootContext.Builder(level).withRandom(level.getRandom()).create(LootContextParamSets.EMPTY));
        ItemEntity item = new ItemEntity(level, pig.getX() + look.x(), pig.getY() + 0.2, pig.getZ() + look.z(), items.get(0));
        item.setDefaultPickUpDelay();
        item.setDeltaMovement(look.multiply(0.3, 0, 0.3).add(0, 0.15, 0));
        level.addFreshEntity(item);
    }

    @Override
    protected void tick(ServerLevel level, BarnyardPig pig, long l) {
        pig.getBrain().getMemory(BarnyardMemoryModules.PIG_SNIFFING_TICKS).ifPresent((time) -> {
            if (time > 40 && time < 64) createParticles(level, pig);
            if (time == 38) spawnItem(level, pig);
        });
    }

    @Override
    protected void stop(ServerLevel level, BarnyardPig pig, long l) {
        pig.setPose(Pose.STANDING);
        pig.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        pig.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        pig.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 6000L);
    }
}