package littleservantmod.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Servantの持ち物処理
 *
 * @author shift02
 */
public class InventoryServant implements IInventory {
	/** An array of 36 item stacks indicating the main player inventory (including the visible bar). */
	public final NonNullList<ItemStack> mainInventory = NonNullList.<ItemStack>withSize(17, ItemStack.EMPTY);
	/** An array of 4 item stacks containing the currently worn armor pieces. */
	public final NonNullList<ItemStack> armorInventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
	private final List<NonNullList<ItemStack>> allInventories;
	/** The index of the currently held item (0-8). */
	public int currentItem;
	/** The player whose inventory this is. */
	public EntityLittleServant servant;
	public EntityPlayer player;
	/** The stack currently held by the mouse cursor */
	private ItemStack itemStack;
	private int timesChanged;

	public InventoryServant(EntityLittleServant servantIn, EntityPlayer player) {
		this.allInventories = Arrays.<NonNullList<ItemStack>>asList(this.mainInventory, this.armorInventory);
		this.itemStack = ItemStack.EMPTY;
		this.servant = servantIn;
		this.player = player;
	}

	/**
	 * Returns the item stack currently held by the player.
	 */
	public ItemStack getCurrentItem() {
		return isHotbar(this.currentItem) ? (ItemStack) this.mainInventory.get(this.currentItem) : ItemStack.EMPTY;
	}

	/**
	 * Get the size of the player hotbar inventory
	 */
	public static int getHotbarSize() {
		return 18;
	}

	private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < this.getInventoryStackLimit();
	}

	/**
	 * Checks item, NBT, and meta if the item is not damageable
	 */
	private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	/**
	 * Returns the first item stack that is empty.
	 */
	public int getFirstEmptyStack() {
		for (int i = 0; i < this.mainInventory.size(); ++i) {
			if (this.mainInventory.get(i).isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	@SideOnly(Side.CLIENT)
	public void setPickedItemStack(ItemStack stack) {
		int i = this.getSlotFor(stack);

		if (isHotbar(i)) {
			this.currentItem = i;
		} else {
			if (i == -1) {
				this.currentItem = this.getBestHotbarSlot();

				if (!this.mainInventory.get(this.currentItem).isEmpty()) {
					int j = this.getFirstEmptyStack();

					if (j != -1) {
						this.mainInventory.set(j, this.mainInventory.get(this.currentItem));
					}
				}

				this.mainInventory.set(this.currentItem, stack);
			} else {
				this.pickItem(i);
			}
		}
	}

	public void pickItem(int index) {
		this.currentItem = this.getBestHotbarSlot();
		ItemStack itemstack = this.mainInventory.get(this.currentItem);
		this.mainInventory.set(this.currentItem, this.mainInventory.get(index));
		this.mainInventory.set(index, itemstack);
	}

	public static boolean isHotbar(int index) {
		//サーヴァントは強いので持ち物全てを道具として使える
		return index >= 0 && index < 18;//9;
	}

	/**
	 * Finds the stack or an equivalent one in the main inventory
	 */
	public int getSlotFor(ItemStack stack) {
		for (int i = 0; i < this.mainInventory.size(); ++i) {
			if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(stack, this.mainInventory.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
		for (int i = 0; i < this.mainInventory.size(); ++i) {
			ItemStack itemstack = this.mainInventory.get(i);

			if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(p_194014_1_, this.mainInventory.get(i)) && !this.mainInventory.get(i).isItemDamaged()
					&& !itemstack.isItemEnchanted() && !itemstack.hasDisplayName()) {
				return i;
			}
		}

		return -1;
	}

	public int getBestHotbarSlot() {
		for (int i = 0; i < 9; ++i) {
			int j = (this.currentItem + i) % 9;

			if (this.mainInventory.get(j).isEmpty()) {
				return j;
			}
		}

		for (int k = 0; k < 9; ++k) {
			int l = (this.currentItem + k) % 9;

			if (!this.mainInventory.get(l).isItemEnchanted()) {
				return l;
			}
		}

		return this.currentItem;
	}

	/**
	 * Switch the current item to the next one or the previous one
	 */
	@SideOnly(Side.CLIENT)
	public void changeCurrentItem(int direction) {
		if (direction > 0) {
			direction = 1;
		}

		if (direction < 0) {
			direction = -1;
		}

		for (this.currentItem -= direction; this.currentItem < 0; this.currentItem += 9) {
			;
		}

		while (this.currentItem >= 9) {
			this.currentItem -= 9;
		}
	}

	/**
	 * Removes matching items from the inventory.
	 * @param itemIn The item to match, null ignores.
	 * @param metadataIn The metadata to match, -1 ignores.
	 * @param removeCount The number of items to remove. If less than 1, removes all matching items.
	 * @param itemNBT The NBT data to match, null ignores.
	 * @return The number of items removed from the inventory.
	 */
	public int clearMatchingItems(@Nullable Item itemIn, int metadataIn, int removeCount, @Nullable NBTTagCompound itemNBT) {
		int i = 0;

		for (int j = 0; j < this.getSizeInventory(); ++j) {
			ItemStack itemstack = this.getStackInSlot(j);

			if (!itemstack.isEmpty() && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn)
					&& (itemNBT == null || NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true))) {
				int k = removeCount <= 0 ? itemstack.getCount() : Math.min(removeCount - i, itemstack.getCount());
				i += k;

				if (removeCount != 0) {
					itemstack.shrink(k);

					if (itemstack.isEmpty()) {
						this.setInventorySlotContents(j, ItemStack.EMPTY);
					}

					if (removeCount > 0 && i >= removeCount) {
						return i;
					}
				}
			}
		}

		if (!this.itemStack.isEmpty()) {
			if (itemIn != null && this.itemStack.getItem() != itemIn) {
				return i;
			}

			if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn) {
				return i;
			}

			if (itemNBT != null && !NBTUtil.areNBTEquals(itemNBT, this.itemStack.getTagCompound(), true)) {
				return i;
			}

			int l = removeCount <= 0 ? this.itemStack.getCount() : Math.min(removeCount - i, this.itemStack.getCount());
			i += l;

			if (removeCount != 0) {
				this.itemStack.shrink(l);

				if (this.itemStack.isEmpty()) {
					this.itemStack = ItemStack.EMPTY;
				}

				if (removeCount > 0 && i >= removeCount) {
					return i;
				}
			}
		}

		return i;
	}

	/**
	 * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of
	 * left over items.
	 */
	private int storePartialItemStack(ItemStack itemStackIn) {
		int i = this.storeItemStack(itemStackIn);

		if (i == -1) {
			i = this.getFirstEmptyStack();
		}

		return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
	}

	private int addResource(int p_191973_1_, ItemStack p_191973_2_) {
		Item item = p_191973_2_.getItem();
		int i = p_191973_2_.getCount();
		ItemStack itemstack = this.getStackInSlot(p_191973_1_);

		if (itemstack.isEmpty()) {
			itemstack = p_191973_2_.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
			itemstack.setCount(0);

			if (p_191973_2_.hasTagCompound()) {
				itemstack.setTagCompound(p_191973_2_.getTagCompound().copy());
			}

			this.setInventorySlotContents(p_191973_1_, itemstack);
		}

		int j = i;

		if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
			j = itemstack.getMaxStackSize() - itemstack.getCount();
		}

		if (j > this.getInventoryStackLimit() - itemstack.getCount()) {
			j = this.getInventoryStackLimit() - itemstack.getCount();
		}

		if (j == 0) {
			return i;
		} else {
			i = i - j;
			itemstack.grow(j);
			itemstack.setAnimationsToGo(5);
			return i;
		}
	}

	/**
	 * stores an itemstack in the users inventory
	 */
	public int storeItemStack(ItemStack itemStackIn) {
		if (this.canMergeStacks(this.getStackInSlot(this.currentItem), itemStackIn)) {
			return this.currentItem;
		} else if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn)) {
			return 40;
		} else {
			for (int i = 0; i < this.mainInventory.size(); ++i) {
				if (this.canMergeStacks(this.mainInventory.get(i), itemStackIn)) {
					return i;
				}
			}

			return -1;
		}
	}

	/**
	 * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
	 * receiving a block.
	 */
	public void decrementAnimations() {
		for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
			for (int i = 0; i < nonnulllist.size(); ++i) {
				if (!nonnulllist.get(i).isEmpty()) {
					nonnulllist.get(i).updateAnimation(this.servant.world, this.servant, i, this.currentItem == i);
				}
			}
		}
		for (ItemStack is : armorInventory) // FORGE: Tick armor on animation ticks
		{
			if (!is.isEmpty()) {
				is.getItem().onArmorTick(servant.world, this.player, is);
			}
		}
	}

	/**
	 * Adds the item stack to the inventory, returns false if it is impossible.
	 */
	public boolean addItemStackToInventory(ItemStack itemStackIn) {
		return this.add(-1, itemStackIn);
	}

	public boolean add(int p_191971_1_, final ItemStack p_191971_2_) {
		if (p_191971_2_.isEmpty()) {
			return false;
		} else {
			try {
				if (p_191971_2_.isItemDamaged()) {
					if (p_191971_1_ == -1) {
						p_191971_1_ = this.getFirstEmptyStack();
					}

					if (p_191971_1_ >= 0) {
						this.mainInventory.set(p_191971_1_, p_191971_2_.copy());
						this.mainInventory.get(p_191971_1_).setAnimationsToGo(5);
						p_191971_2_.setCount(0);
						return true;
						//} else if (this.servant.capabilities.isCreativeMode) {
						//	p_191971_2_.setCount(0);
						//	return true;
					} else {
						return false;
					}
				} else {
					int i;

					while (true) {
						i = p_191971_2_.getCount();

						if (p_191971_1_ == -1) {
							p_191971_2_.setCount(this.storePartialItemStack(p_191971_2_));
						} else {
							p_191971_2_.setCount(this.addResource(p_191971_1_, p_191971_2_));
						}

						if (p_191971_2_.isEmpty() || p_191971_2_.getCount() >= i) {
							break;
						}
					}

					//if (p_191971_2_.getCount() == i && this.servant.capabilities.isCreativeMode) {
					//	p_191971_2_.setCount(0);
					//	return true;
					//} else {
					return p_191971_2_.getCount() < i;
					//}
				}
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
				crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(p_191971_2_.getItem())));
				crashreportcategory.addCrashSection("Item data", Integer.valueOf(p_191971_2_.getMetadata()));
				crashreportcategory.addDetail("Item name", new ICrashReportDetail<String>() {
					@Override
					public String call() throws Exception {
						return p_191971_2_.getDisplayName();
					}
				});
				throw new ReportedException(crashreport);
			}
		}
	}

	public void placeItemBackInInventory(World p_191975_1_, ItemStack p_191975_2_) {
		if (!p_191975_1_.isRemote) {
			while (!p_191975_2_.isEmpty()) {
				int i = this.storeItemStack(p_191975_2_);

				if (i == -1) {
					i = this.getFirstEmptyStack();
				}

				if (i == -1) {
					this.servant.dropItem(p_191975_2_, false);
					break;
				}

				int j = p_191975_2_.getMaxStackSize() - this.getStackInSlot(i).getCount();

				if (this.add(i, p_191975_2_.splitStack(j))) {
					//TODO ((EntityPlayerMP) this.servant).connection.sendPacket(new SPacketSetSlot(-2, i, this.getStackInSlot(i)));
				}
			}
		}
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		List<ItemStack> list = null;

		for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
			if (index < nonnulllist.size()) {
				list = nonnulllist;
				break;
			}

			index -= nonnulllist.size();
		}

		return list != null && !list.get(index).isEmpty() ? ItemStackHelper.getAndSplit(list, index, count) : ItemStack.EMPTY;
	}

	public void deleteStack(ItemStack stack) {
		for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
			for (int i = 0; i < nonnulllist.size(); ++i) {
				if (nonnulllist.get(i) == stack) {
					nonnulllist.set(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
		NonNullList<ItemStack> nonnulllist = null;

		for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
			if (index < nonnulllist1.size()) {
				nonnulllist = nonnulllist1;
				break;
			}

			index -= nonnulllist1.size();
		}

		if (nonnulllist != null && !nonnulllist.get(index).isEmpty()) {
			ItemStack itemstack = nonnulllist.get(index);
			nonnulllist.set(index, ItemStack.EMPTY);
			return itemstack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		NonNullList<ItemStack> nonnulllist = null;

		for (NonNullList<ItemStack> nonnulllist1 : this.allInventories) {
			if (index < nonnulllist1.size()) {
				nonnulllist = nonnulllist1;
				break;
			}

			index -= nonnulllist1.size();
		}

		if (nonnulllist != null) {
			nonnulllist.set(index, stack);
		}
	}

	public float getDestroySpeed(IBlockState state) {
		float f = 1.0F;

		if (!this.mainInventory.get(this.currentItem).isEmpty()) {
			f *= this.mainInventory.get(this.currentItem).getDestroySpeed(state);
		}

		return f;
	}

	/**
	 * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
	 * for crafting).
	 */
	public NBTTagList writeToNBT(NBTTagList nbtTagListIn) {
		for (int i = 0; i < this.mainInventory.size(); ++i) {
			if (!this.mainInventory.get(i).isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				this.mainInventory.get(i).writeToNBT(nbttagcompound);
				nbtTagListIn.appendTag(nbttagcompound);
			}
		}

		for (int j = 0; j < this.armorInventory.size(); ++j) {
			if (!this.armorInventory.get(j).isEmpty()) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) (j + 100));
				this.armorInventory.get(j).writeToNBT(nbttagcompound1);
				nbtTagListIn.appendTag(nbttagcompound1);
			}
		}

		return nbtTagListIn;
	}

	/**
	 * Reads from the given tag list and fills the slots in the inventory with the correct items.
	 */
	public void readFromNBT(NBTTagList nbtTagListIn) {
		this.mainInventory.clear();
		this.armorInventory.clear();

		for (int i = 0; i < nbtTagListIn.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbtTagListIn.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;
			ItemStack itemstack = new ItemStack(nbttagcompound);

			if (!itemstack.isEmpty()) {
				if (j >= 0 && j < this.mainInventory.size()) {
					this.mainInventory.set(j, itemstack);
				} else if (j >= 100 && j < this.armorInventory.size() + 100) {
					this.armorInventory.set(j - 100, itemstack);
				} else if (j == 150) {
					this.mainInventory.set(this.mainInventory.size() - 1, itemstack);
				}
			}
		}
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		return this.mainInventory.size() + this.armorInventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.mainInventory) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemstack1 : this.armorInventory) {
			if (!itemstack1.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		List<ItemStack> list = null;

		for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
			if (index < nonnulllist.size()) {
				list = nonnulllist;
				break;
			}

			index -= nonnulllist.size();
		}

		return list == null ? ItemStack.EMPTY : (ItemStack) list.get(index);
	}

	/**
	 * Get the name of this object. For players this returns their username
	 */
	@Override
	public String getName() {
		return "container.inventory";
	}

	/**
	 * Returns true if this thing is named
	 */
	@Override
	public boolean hasCustomName() {
		return false;
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 */
	@Override
	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]);
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean canHarvestBlock(IBlockState state) {
		if (state.getMaterial().isToolNotRequired()) {
			return true;
		} else {
			ItemStack itemstack = this.getStackInSlot(this.currentItem);
			return !itemstack.isEmpty() ? itemstack.canHarvestBlock(state) : false;
		}
	}

	/**
	 * returns a player armor item (as itemstack) contained in specified armor slot.
	 */
	@SideOnly(Side.CLIENT)
	public ItemStack armorItemInSlot(int slotIn) {
		return this.armorInventory.get(slotIn);
	}

	/**
	 * Damages armor in each slot by the specified amount.
	 */
	public void damageArmor(float damage) {
		damage = damage / 4.0F;

		if (damage < 1.0F) {
			damage = 1.0F;
		}

		for (int i = 0; i < this.armorInventory.size(); ++i) {
			ItemStack itemstack = this.armorInventory.get(i);

			if (itemstack.getItem() instanceof ItemArmor) {
				itemstack.damageItem((int) damage, this.servant);
			}
		}
	}

	/**
	 * Drop all armor and main inventory items.
	 */
	public void dropAllItems() {
		for (List<ItemStack> list : this.allInventories) {
			for (int i = 0; i < list.size(); ++i) {
				ItemStack itemstack = list.get(i);

				if (!itemstack.isEmpty()) {
					this.servant.dropItem(itemstack, true, false);
					list.set(i, ItemStack.EMPTY);
				}
			}
		}
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
	 * hasn't changed and skip it.
	 */
	@Override
	public void markDirty() {
		++this.timesChanged;
	}

	@SideOnly(Side.CLIENT)
	public int getTimesChanged() {
		return this.timesChanged;
	}

	/**
	 * Set the stack helds by mouse, used in GUI/Container
	 */
	public void setItemStack(ItemStack itemStackIn) {
		this.itemStack = itemStackIn;
	}

	/**
	 * Stack helds by mouse, used in GUI and Containers
	 */
	public ItemStack getItemStack() {
		return this.itemStack;
	}

	/**
	 * Don't rename this method to canInteractWith due to conflicts with Container
	 */
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (this.servant.isDead) {
			return false;
		} else {
			return player.getDistanceSq(this.servant) <= 64.0D;
		}
	}

	/**
	 * Returns true if the specified ItemStack exists in the inventory.
	 */
	public boolean hasItemStack(ItemStack itemStackIn) {
		label:

		for (List<ItemStack> list : this.allInventories) {
			Iterator iterator = list.iterator();

			while (true) {
				if (!iterator.hasNext()) {
					continue label;
				}

				ItemStack itemstack = (ItemStack) iterator.next();

				if (!itemstack.isEmpty() && itemstack.isItemEqual(itemStackIn)) {
					break;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
	 * guis use Slot.isItemValid
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	/**
	 * Copy the ItemStack contents from another InventoryPlayer instance
	 */
	public void copyInventory(InventoryServant servantInventory) {
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			this.setInventorySlotContents(i, servantInventory.getStackInSlot(i));
		}

		this.currentItem = playerInventory.currentItem;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (List<ItemStack> list : this.allInventories) {
			list.clear();
		}
	}

	public void fillStackedContents(RecipeItemHelper helper, boolean p_194016_2_) {
		for (ItemStack itemstack : this.mainInventory) {
			helper.accountStack(itemstack);
		}
	}
}