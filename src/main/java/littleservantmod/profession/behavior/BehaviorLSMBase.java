package littleservantmod.profession.behavior;

import littleservantmod.api.IServant;
import littleservantmod.api.profession.behavior.BehaviorBase;
import littleservantmod.profession.IconHolder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BehaviorLSMBase extends BehaviorBase {

	protected IconHolder iconHolder;

	public IconHolder getIconHolder() {
		return iconHolder;
	}

	public BehaviorLSMBase setIconHolder(IconHolder iconHolder) {
		this.iconHolder = iconHolder;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon(IServant servant) {

		if (this.iconHolder == null || this.iconHolder.getIcon() == null) {
			//Missing
			return net.minecraft.client.Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}

		return this.iconHolder.getIcon();
	}

	@Override
	public boolean isEnableBehavior(IServant servant) {
		return true;
	}

}
