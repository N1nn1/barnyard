package com.ninni.barnyard.entities.ai.tasks;

import static com.ninni.barnyard.Barnyard.MOD_ID;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.ninni.barnyard.entities.BarnyardPig;
import com.ninni.barnyard.entities.ai.BarnyardPigAi;
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
    protected boolean canStillUse(ServerLevel level, BarnyardPig mob, long l) {
        return true;
    }

    protected void createParticles(ServerLevel level, BarnyardPig mob) {
        Vec3 look = mob.getLookAngle().multiply(0.6, 0, 0.6);
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, mob.getBlockStateOn()), mob.getX() + look.x(), mob.getY() + 0.1, mob.getZ() + look.z(), 1, 0.2, 0.1, 0.2, 0);
    }

    protected void spawnItem(ServerLevel level, BarnyardPig mob) {
        Vec3 look = mob.getLookAngle().multiply(0.6, 0, 0.6);
        List<ItemStack> items = level.getServer().getLootTables().get(DIGGING_LOOT).getRandomItems(new LootContext.Builder(level).withRandom(level.getRandom()).create(LootContextParamSets.EMPTY));
        ItemEntity item = new ItemEntity(level, mob.getX() + look.x(), mob.getY() + 0.2, mob.getZ() + look.z(), items.get(0));
        item.setDefaultPickUpDelay();
        item.setDeltaMovement(look.multiply(0.3, 0, 0.3).add(0, 0.15, 0));
        level.addFreshEntity(item);
    }

    @Override
    protected void tick(ServerLevel level, BarnyardPig mob, long l) {
        mob.getBrain().getMemory(BarnyardMemoryModules.PIG_SNIFFING_TICKS).ifPresent((time) -> {
            if (time > 40 && time < 64) createParticles(level, mob);
            if (time == 38) spawnItem(level, mob);
        });
    }

    @Override
    protected void stop(ServerLevel level, BarnyardPig mob, long l) {
        mob.setPose(Pose.STANDING);
        mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        mob.getBrain().eraseMemory(MemoryModuleType.IS_SNIFFING);
        mob.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, BarnyardPigAi.SNIFFING_COOLDOWN.sample(mob.getRandom()));
    }
}