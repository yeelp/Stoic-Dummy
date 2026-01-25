package yeelp.stoicdummy.item;

import java.util.List;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import yeelp.stoicdummy.ModConsts;
import yeelp.stoicdummy.entity.EntityStoicDummy;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class StoicDummyItem extends Item {
	
	private static final String ID = "stoicdummyitem";
	private static final ITextComponent INFO = new TextComponentTranslation("tooltips.stoicdummy.info");	
	private static final int ROTATION_INCREMENT = 45;
	
	public StoicDummyItem() {
		this.setRegistryName(ID);
		this.setTranslationKey(String.format("%s.%s", ModConsts.MODID, ID));
		this.setCreativeTab(CreativeTabs.MISC);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(INFO.getFormattedText());
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		//borrow from ItemArmorStand#onItemUse
		if(facing == EnumFacing.DOWN) {
			return EnumActionResult.FAIL;
		}
		BlockPos spawnPos = isReplaceable(worldIn, pos) ? pos : pos.offset(facing);
		ItemStack stack = player.getHeldItem(hand);
		if(!player.canPlayerEdit(spawnPos, facing, stack)) {
			return EnumActionResult.FAIL;
		}
		BlockPos above = spawnPos.up();
		if(isObstructed(worldIn, spawnPos) || isObstructed(worldIn, spawnPos)) {
			return EnumActionResult.FAIL;
		}
		double x = spawnPos.getX();
		double y = spawnPos.getY();
		double z = spawnPos.getZ();
		
		if(!worldIn.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(x, y, z, x + 1, y + 2, z + 1)).isEmpty()) {
			return EnumActionResult.FAIL;
		}
		if(!worldIn.isRemote) {
			worldIn.setBlockToAir(spawnPos);
			worldIn.setBlockToAir(above);
			int rotation = MathHelper.floor((MathHelper.wrapDegrees(player.rotationYaw - 180.0f) + ROTATION_INCREMENT/2.0f) / ROTATION_INCREMENT) * ROTATION_INCREMENT;
			EntityStoicDummy dummy = new EntityStoicDummy(worldIn, player, rotation);
			dummy.setPosition(x + 0.5, y, z + 0.5);
			ItemMonsterPlacer.applyItemEntityDataToEntity(worldIn, player, stack, dummy);
			worldIn.spawnEntity(dummy);
			worldIn.playSound(null, dummy.posX, dummy.posY, dummy.posZ, SoundEvents.ENTITY_ARMORSTAND_PLACE, SoundCategory.BLOCKS, 0.75f, 0.8f);
		}
		stack.shrink(1);
		return EnumActionResult.SUCCESS;
	}
	
	private static boolean isObstructed(World world, BlockPos pos) {
		return !world.isAirBlock(pos) && !isReplaceable(world, pos);
	}
	
	private static boolean isReplaceable(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
	}
}
