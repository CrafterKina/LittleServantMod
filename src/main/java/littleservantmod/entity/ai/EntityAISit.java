package littleservantmod.entity.ai;

import littleservantmod.entity.EntityLittleServantBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISit extends EntityAIBase {
	private final EntityLittleServantBase tameable;
	/** If the EntityTameable is sitting. */
	private boolean isSitting;

	public EntityAISit(EntityLittleServantBase entityIn) {
		this.tameable = entityIn;
		this.setMutexBits(0b0101);//5
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (!this.tameable.isTamed()) {
			return false;
		} else if (this.tameable.isInWater()) {
			return false;
		} else if (!this.tameable.onGround) {
			return false;
		} else {
			EntityLivingBase entitylivingbase = this.tameable.getOwner();

			if (entitylivingbase == null) {
				return true;
			} else {
				return this.tameable.getDistanceSq(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : this.isSitting;
			}
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.tameable.getNavigator().clearPath();
		this.tameable.setSitting(true);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void resetTask() {
		this.tameable.setSitting(false);
	}

	/**
	 * Sets the sitting flag.
	 */
	public void setSitting(boolean sitting) {
		this.isSitting = sitting;
	}
}