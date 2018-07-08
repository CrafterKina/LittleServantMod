package littleservantmod.profession;

import littleservantmod.api.IServant;
import littleservantmod.entity.EntityLittleServant;
import littleservantmod.entity.ai.EntityAIAttackRangedBow2;
import littleservantmod.entity.ai.EntityAIEquipTool;
import littleservantmod.entity.ai.target.EntityAINearestAttackableTarget2;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class ProfessionArcher extends ProfessionLSMBase implements IRangedAttackMob {

	protected IServant servant;

	@Override
	public void initAI(IServant servant) {

		this.servant = servant;

		super.initAI(servant);

		//弓を持ち帰る
		servant.addAI(200, new EntityAIEquipTool((EntityLittleServant) servant.getEntityInstance(), ProfessionToolManager.archer));

		//攻撃
		servant.addAI(400, new EntityAIAttackRangedBow2(servant.getEntityInstance(), this, ProfessionToolManager.archer, 1.0D, 20, 15.0F));

		//Target
		servant.addTargetAI(200, new EntityAINearestAttackableTarget2((EntityLittleServant) servant.getEntityInstance(), EntitySpider.class, true));

	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		EntityArrow entityarrow = this.getArrow(distanceFactor);
		double d0 = target.posX - this.servant.getEntityInstance().posX;
		double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - entityarrow.posY;
		double d2 = target.posZ - this.servant.getEntityInstance().posZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 14 - this.servant.getEntityInstance().world.getDifficulty().getDifficultyId() * 4);
		this.servant.getEntityInstance().playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.servant.getEntityInstance().getRNG().nextFloat() * 0.4F + 0.8F));
		this.servant.getEntityInstance().world.spawnEntity(entityarrow);
	}

	protected EntityArrow getArrow(float p_190726_1_) {
		EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.servant.getEntityInstance().world, this.servant.getEntityInstance());
		entitytippedarrow.setEnchantmentEffectsFromEntity(this.servant.getEntityInstance(), p_190726_1_);
		return entitytippedarrow;
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
