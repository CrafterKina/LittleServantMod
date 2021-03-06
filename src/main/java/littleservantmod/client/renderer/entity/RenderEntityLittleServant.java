package littleservantmod.client.renderer.entity;

import littleservantmod.LittleServantMod;
import littleservantmod.client.model.ModelLittleServantBase;
import littleservantmod.client.renderer.entity.layers.LayerCustomHead;
import littleservantmod.client.renderer.entity.layers.LayerHeldItem;
import littleservantmod.entity.EntityLittleServant;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class RenderEntityLittleServant extends RenderLivingBase<EntityLittleServant> {

	private static final ResourceLocation MAID_TEXTURES = new ResourceLocation(LittleServantMod.MOD_ID, "textures/entitys/little_maid/mob_littlemaid.png");
	private static final ResourceLocation TAMED_MAID_TEXTURES = new ResourceLocation(LittleServantMod.MOD_ID, "textures/entitys/little_maid/mob_littlemaid_tamed.png");

	public RenderEntityLittleServant(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelLittleServantBase(0.0F, false), 0.5F);

		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));

	}

	@Override
	public void doRender(EntityLittleServant entity, double x, double y, double z, float entityYaw, float partialTicks) {

		setModelVisibilities(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	private void setModelVisibilities(EntityLittleServant clientPlayer) {

		//プレイヤーと同じ感じでモデルの変化をさせる

		ModelLittleServantBase modelplayer = this.getMainModel();

		/*if (clientPlayer.isSpectator()) {
			modelplayer.setVisible(false);
			modelplayer.bipedHead.showModel = true;
			modelplayer.bipedHeadwear.showModel = true;
		} else { */
		ItemStack itemstack = clientPlayer.getHeldItemMainhand();
		ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
		modelplayer.setVisible(true);

		/*modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
		modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
		modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
		modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
		modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
		modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
		*/
		modelplayer.isSneak = clientPlayer.isSneaking();
		ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
		ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

		if (!itemstack.isEmpty()) {
			modelbiped$armpose = ModelBiped.ArmPose.ITEM;

			if (clientPlayer.getItemInUseCount() > 0) {
				EnumAction enumaction = itemstack.getItemUseAction();

				if (enumaction == EnumAction.BLOCK) {
					modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
				} else if (enumaction == EnumAction.BOW) {
					modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
				}
			}
		}

		if (!itemstack1.isEmpty()) {
			modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

			if (clientPlayer.getItemInUseCount() > 0) {
				EnumAction enumaction1 = itemstack1.getItemUseAction();

				if (enumaction1 == EnumAction.BLOCK) {
					modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
				}
				// FORGE: fix MC-88356 allow offhand to use bow and arrow animation
				else if (enumaction1 == EnumAction.BOW) {
					modelbiped$armpose1 = ModelBiped.ArmPose.BOW_AND_ARROW;
				}
			}
		}

		if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT) {
			modelplayer.rightArmPose = modelbiped$armpose;
			modelplayer.leftArmPose = modelbiped$armpose1;
		} else {
			modelplayer.rightArmPose = modelbiped$armpose1;
			modelplayer.leftArmPose = modelbiped$armpose;
		}
		//}
	}

	@Override
	public ModelLittleServantBase getMainModel() {
		return (ModelLittleServantBase) super.getMainModel();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLittleServant entity) {

		if (entity.isTamed()) {
			return TAMED_MAID_TEXTURES;
		}

		return MAID_TEXTURES;
	}

	@Override
	public void renderName(EntityLittleServant entity, double x, double y, double z) {
		if (this.canRenderName(entity) && !entity.isGui) {
			this.renderLivingLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
		}
	}

}
