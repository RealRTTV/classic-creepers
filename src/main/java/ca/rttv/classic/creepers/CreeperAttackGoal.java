package ca.rttv.classic.creepers;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.CreeperEntity;

public class CreeperAttackGoal extends MeleeAttackGoal {
    private final CreeperEntity zombie;
    private int ticks;

    public CreeperAttackGoal(CreeperEntity zombie, double speed, boolean pauseWhenMobIdle) {
        super(zombie, speed, pauseWhenMobIdle);
        this.zombie = zombie;
    }

    public void start() {
        super.start();
        this.ticks = 0;
    }

    public void stop() {
        super.stop();
        this.zombie.setAttacking(false);
    }

    public void tick() {
        super.tick();
        ++this.ticks;
        if (this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2) {
            this.zombie.setAttacking(true);
        } else {
            this.zombie.setAttacking(false);
        }

    }
}
