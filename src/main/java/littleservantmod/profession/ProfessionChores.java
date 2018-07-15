package littleservantmod.profession;

import java.util.Map;

import com.google.common.collect.Maps;

import littleservantmod.LSMProxy;
import littleservantmod.LittleServantMod;
import littleservantmod.api.IServant;
import littleservantmod.api.LittleServantModAPI;
import littleservantmod.api.profession.behavior.IBehavior;
import littleservantmod.api.profession.mode.IMode;
import littleservantmod.profession.behavior.BehaviorEscort;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 雑用
 * @author shift02
 */
public class ProfessionChores extends ProfessionLSMBase {

	/** エスコート */
	public static ResourceLocation kyeEscort = new ResourceLocation(LittleServantMod.MOD_ID, "chores_escort");
	public static IconHolder iconEscort = new IconHolder() {

		@Override
		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite getIcon() {
			return LSMProxy.getProxy().escort;
		}

	};

	/** 自由行動 */
	public static ResourceLocation kyeFree = new ResourceLocation(LittleServantMod.MOD_ID, "chores_free");
	public static IconHolder iconFree = new IconHolder() {

		@Override
		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite getIcon() {
			return LSMProxy.getProxy().free;
		}

	};

	public IMode modeBasic;

	public IBehavior behaviorEscort;
	public IBehavior behaviorFree;

	public ProfessionChores() {

		//Mode
		this.modeBasic = LittleServantModAPI.professionManager.getBasicMode();

		//Behavior
		this.behaviorEscort = new BehaviorEscort().setIconHolder(iconEscort).setUnlocalizedName("chores_escort").setRegistryName(kyeEscort);

		this.behaviorFree = new BehaviorEscort().setIconHolder(iconFree).setUnlocalizedName("chores_free").setRegistryName(kyeFree);

	}

	@Override
	public Map<ResourceLocation, IMode> initModes(IServant servant) {

		Map<ResourceLocation, IMode> map = Maps.newLinkedHashMap();

		map.put(LittleServantModAPI.professionManager.getBasicModeKey(), this.modeBasic);

		return map;

	}

	@Override
	public Map<ResourceLocation, IBehavior> initBehavior(IServant servant) {

		Map<ResourceLocation, IBehavior> map = Maps.newLinkedHashMap();

		map.put(kyeEscort, behaviorEscort);
		map.put(kyeFree, behaviorFree);

		return map;

	}

	@Override
	public IMode getDefaultMode(IServant servant) {
		return this.modeBasic;
	}

	@Override
	public IBehavior getDefaultBehavior(IServant servant) {
		return this.behaviorEscort;
	}

}
