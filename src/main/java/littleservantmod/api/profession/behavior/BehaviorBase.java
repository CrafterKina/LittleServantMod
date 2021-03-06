package littleservantmod.api.profession.behavior;

import littleservantmod.api.IServant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BehaviorBase implements IBehavior {

	@SideOnly(Side.CLIENT)
	protected TextureAtlasSprite icon;

	protected ResourceLocation resourceLocation;
	protected String unlocalizedName;

	@Override
	public void initAI(IServant servant) {

	}

	@SideOnly(Side.CLIENT)
	public BehaviorBase setIcon(TextureAtlasSprite icon) {
		this.icon = icon;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon(IServant servant) {

		if (icon == null) {
			//Missing
			return net.minecraft.client.Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}

		return icon;
	}

	public BehaviorBase setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	public String getUnlocalizedName() {
		return "behavior." + this.unlocalizedName;
	}

	public String getUnlocalizedName(IServant servant) {
		return "behavior." + this.unlocalizedName;
	}

	@Override
	public String getBehaviorDisplayName(IServant servant) {
		return I18n.translateToLocal(this.getUnlocalizedName(servant) + ".name").trim();
	}

	@Override
	public ResourceLocation getRegistryName() {
		return this.resourceLocation;
	}

	public BehaviorBase setRegistryName(ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
		return this;
	}

	@Override
	public void startBehavior(IServant servant) {
	};

}
