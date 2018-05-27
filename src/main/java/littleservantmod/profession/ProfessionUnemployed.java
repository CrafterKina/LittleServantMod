package littleservantmod.profession;

import littleservantmod.api.IServant;
import littleservantmod.api.profession.ProfessionBase;
import littleservantmod.entity.ai.EntityAIFollow;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityRabbit;

/** 無職 */
public class ProfessionUnemployed extends ProfessionBase {

	@Override
	public void initAI(IServant servant) {

		super.initAI(servant);

		//うさぎについていく
		servant.addAI(500, new EntityAIFollow(servant.getEntityInstance(), EntityRabbit.class, 0.5D));

		//うさぎを見る
		servant.addAI(900, new EntityAIWatchClosest(servant.getEntityInstance(), EntityRabbit.class, 4.0F));

	}

}