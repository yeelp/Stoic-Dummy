package yeelp.stoicdummy.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import yeelp.stoicdummy.ModConsts;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.SDLogger;
import yeelp.stoicdummy.StoicDummy;
import yeelp.stoicdummy.config.ModConfig;
import yeelp.stoicdummy.proxy.Proxy;
import yeelp.stoicdummy.util.AbstractDamageInstance.AbstractDamageInstanceBuilder;
import yeelp.stoicdummy.util.AbstractDamageInstance;
import yeelp.stoicdummy.util.DamageHistory;
import yeelp.stoicdummy.util.InventoryUtils;
import yeelp.stoicdummy.util.SimpleDamageInstance.SimpleDamageInstanceBuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Mod.EventBusSubscriber(modid = ModConsts.MODID)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class EntityStoicDummy extends EntityLivingBase implements IEntityAdditionalSpawnData {
	public static final ResourceLocation LOC = new ResourceLocation(ModConsts.MODID, "stoicdummy");
	private static final int KILL_IN_VOID_THRESHOLD = -150;
	private EnumHandSide hand;
	private EnumCreatureAttribute currentCreatureAttribute = EnumCreatureAttribute.UNDEFINED;
	private final DummyInventory inventory;
	private final DamageHistory history;
	private int rotationTarget;
	private final Map<Potion, Integer> activePotions;
	private AbstractDamageInstanceBuilder damageBuilder;
	
	private static BiFunction<DamageSource, Float, AbstractDamageInstanceBuilder> builderSupplier = SimpleDamageInstanceBuilder::new;

	private EntityStoicDummy(World worldIn, EnumHandSide hand, int rotation) {
		super(worldIn);
		this.hand = hand;
		this.rotationYaw = rotation;
		this.prevRotationYaw = rotation;
		this.rotationTarget = rotation;
		this.activePotions = Maps.newHashMap();
		this.inventory = new DummyInventory();
		this.history = new DamageHistory();
	}

	public EntityStoicDummy(World worldIn, EntityPlayer placer, int rotation) {
		this(worldIn, placer.getPrimaryHand(), rotation);
	}

	public EntityStoicDummy(World worldIn) {
		this(worldIn, EnumHandSide.RIGHT, 0);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return this.inventory.getArmorInventory();
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return this.inventory.get(slotIn);
	}

	@Override
	public EnumHandSide getPrimaryHand() {
		return this.hand;
	}
	
	public void setEnumCreatureAttribute(EnumCreatureAttribute attribute) {
		this.currentCreatureAttribute = attribute;
	}
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return this.currentCreatureAttribute;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
		this.inventory.put(slotIn, stack);
	}
	
	private void setRotation(float rotation) {
		this.rotationTarget = (int) rotation;
		this.rotationYaw = rotation;
		this.rotationYawHead = this.rotationYaw;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationYawHead = this.rotationYaw;
		this.renderYawOffset = this.rotationYaw;
		this.prevRenderYawOffset = this.rotationYaw;
	}

	@Override
	protected void damageArmor(float damage) {
		if(!ModConfig.dummy.damageArmor) {
			return;
		}
		int damageDealt = (int) Math.max(1.0f, damage / 4.0f);
		this.getArmorInventoryList().forEach((s) -> s.damageItem(damageDealt, this));
	}
	
	@Override
	public void onEntityUpdate() {
		this.setRotation(this.rotationTarget);
		super.onEntityUpdate();
		this.setRotation(this.rotationTarget);
		this.activePotions.entrySet().stream().filter(Predicates.not(this::hasExactPotionActive)).forEach((entry) -> {
			this.removeActivePotionEffect(entry.getKey());
			this.addPotionEffect(new PotionEffect(entry.getKey(), Integer.MAX_VALUE, entry.getValue(), true, false));
		});
	}
	
	@Override
	public void onLivingUpdate() {
		this.setRotation(this.rotationTarget);
		super.onLivingUpdate();
		this.setRotation(this.rotationTarget);
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@Override
	public boolean attackable() {
		return false;
	}
	
	@Override
	public ItemStack getPickedResult(RayTraceResult target) {
		return new ItemStack(Proxy.dummyItem);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		this.damageBuilder = builderSupplier.apply(source, amount);
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		if(this.damageBuilder == null) {
			this.damageBuilder = builderSupplier.apply(damageSrc, damageAmount);
		}
		super.damageEntity(damageSrc, damageAmount);
		// heal after damage dealt to not kill
		this.setHealth(this.getMaxHealth());
		if(damageSrc == DamageSource.OUT_OF_WORLD && this.posY <= KILL_IN_VOID_THRESHOLD) {
			this.setDead();
		}
		else {
			this.history.add(this.damageBuilder.build());
			this.history.getLatestEntry().map(Functions.toStringFunction()).ifPresent(SDLogger::debug);
		}
	}

	public AbstractDamageInstanceBuilder getDamageBuilder() {
		return this.damageBuilder;
	}

	public Iterable<AbstractDamageInstance> getDamageHistory() {
		return this.history;
	}
	
	public Iterable<String> getDamageHistoryNumbered() {
		return this.history.getNumberedHistory();
	}
	
	public void clearDamageHistory() {
		this.history.clear();
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(!player.isSneaking() && player.getHeldItemMainhand().getItem() != Items.NAME_TAG && !this.isDead) {
			player.openGui(StoicDummy.instance, this.getEntityId(), this.world, 0, 0, 0);
			return true;
		}
		if(!player.world.isRemote && player.isSneaking() && player.getHeldItemMainhand().isEmpty() && !this.isDead) {
			this.dropEquipment(false, 0);
			this.entityDropItem(new ItemStack(Proxy.dummyItem), 0.5f);
			this.world.playSound(null, this.getPosition(), SoundEvents.ENTITY_ARMORSTAND_BREAK, SoundCategory.BLOCKS, 0.75f, 0.6f);
			if(this.world instanceof WorldServer) {
				((WorldServer) this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY + this.height/1.5, this.posZ, 12, this.width/4, this.height/4, this.width/4, 0.05, Block.getStateId(Blocks.STONE.getDefaultState()));
			}
			this.setDead();
			return true;
		}
		return false;
	}
	

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		this.inventory.values().forEach((item) -> this.entityDropItem(item, 0.0f));
		this.inventory.clear();
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
		//no knockback
	}
	
	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
		super.setLocationAndAngles(x, y, z, this.rotationTarget, pitch);
	}
	
	@Override
	protected float updateDistance(float p_110146_1_, float p_110146_2_) {
		float result = super.updateDistance(p_110146_1_, p_110146_2_);
		this.renderYawOffset = this.rotationYaw;
		return result;
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeBoolean(this.rotationTarget != 0);
		buffer.writeShortLE(this.rotationTarget);
	}
	
	@Override
	public void readSpawnData(ByteBuf additionalData) {
		if(additionalData.readBoolean()) {
			this.setRotation(additionalData.readShortLE());
		}
	}
	
	public boolean hasEmptyInventory() {
		return this.inventory.values().stream().allMatch(ItemStack::isEmpty);
	}
	
	@SuppressWarnings("DataFlowIssue")
    private boolean hasExactPotionActive(Map.Entry<Potion, Integer> entry) {
		return this.isPotionActive(entry.getKey()) && this.getActivePotionEffect(entry.getKey()).getAmplifier() == entry.getValue();
	}
	
	public boolean hasPermanentPotionActive(Potion potion, int amplifier) {
		return this.activePotions.containsKey(potion) && this.activePotions.get(potion) == amplifier;
	}
	
	public Iterable<Potion> getPermanentPotions() {
		return this.activePotions.keySet();
	}
	
	public void addPermanentPotionEffect(Potion potion, int amplifier) {
		//box to avoid NPE for null return of put
		if(!Integer.valueOf(amplifier).equals(this.activePotions.put(potion, amplifier))) {
			this.world.playSound(null, this.getPosition(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.75f, (float) (0.4f * Math.random() + 0.8f));
		}
	}
	
	public void removePermanentPotionEffect(Potion potion) {
		this.activePotions.remove(potion);
		this.removeActivePotionEffect(potion);
	}

    @Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean(DummyNBT.HAND, this.hand == EnumHandSide.RIGHT);
		compound.setInteger(DummyNBT.ROTATION, this.rotationTarget);
		compound.setByte(DummyNBT.CREATURE_ATTRIBUTE, (byte) this.currentCreatureAttribute.ordinal());
		if(!this.hasEmptyInventory()) {
			compound.setTag(DummyNBT.INVENTORY, this.inventory.writeToNBT());
		}
		if(!this.activePotions.isEmpty()) {
			compound.setTag(DummyNBT.POTIONS, this.writePermanentPotionsToNBT());
		}
		if(!this.history.isEmpty()) {
			compound.setTag(DummyNBT.DAMAGE_HISTORY, this.history.writeToNBT());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		this.activePotions.clear();
		this.history.clear();
		super.readEntityFromNBT(compound);
		this.hand = compound.getBoolean(DummyNBT.HAND) ? EnumHandSide.RIGHT : EnumHandSide.LEFT;
		this.setRotation(compound.getInteger(DummyNBT.ROTATION));
		this.setEnumCreatureAttribute(EnumCreatureAttribute.values()[compound.getByte(DummyNBT.CREATURE_ATTRIBUTE)]);
		if(compound.hasKey(DummyNBT.INVENTORY)) {
			this.inventory.readFromNBT(compound.getTagList(DummyNBT.INVENTORY, DummyNBT.TAG_COMPOUND_ID));
		}
		if(compound.hasKey(DummyNBT.POTIONS)) {
			this.readPermanentPotionsFromNBT(compound.getTagList(DummyNBT.POTIONS, DummyNBT.TAG_COMPOUND_ID));
		}
		if(compound.hasKey(DummyNBT.DAMAGE_HISTORY)) {
			this.history.readFromNBT(compound.getTagList(DummyNBT.DAMAGE_HISTORY, DummyNBT.TAG_COMPOUND_ID));
		}
	}

	@SuppressWarnings("DataFlowIssue")
	private NBTTagList writePermanentPotionsToNBT() {
		NBTTagList lst = new NBTTagList();
		this.activePotions.forEach((potion, amp) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString(DummyNBT.POTION_NAME, potion.getRegistryName().toString());
			tag.setInteger(DummyNBT.POTION_AMPLIFIER, amp);
			lst.appendTag(tag);
		});
		return lst;
	}
	
	private void readPermanentPotionsFromNBT(NBTTagList lst) {
		lst.forEach((nbt) -> {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			String[] name = compound.getString(DummyNBT.POTION_NAME).split(":");
			Potion potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(name[0], name[1]));
			if(potion == null) {
				SDLogger.warn("Potion: {}:{} doesn't exist! Perhaps a mod that added it was removed?", name[0], name[1]);
			}
			else {
				this.activePotions.put(potion, compound.getInteger(DummyNBT.POTION_AMPLIFIER));
			}
		});
	}

	private static final class DummyInventory extends EnumMap<EntityEquipmentSlot, ItemStack> {
		DummyInventory() {
			super(EntityEquipmentSlot.class);
			for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				this.put(slot, ItemStack.EMPTY);
			}
		}

		Iterable<ItemStack> getArmorInventory() {
			return this.entrySet().stream().filter((e) -> e.getKey().getSlotType() == EntityEquipmentSlot.Type.ARMOR).map(Entry::getValue).collect(Collectors.toList());
		}

		@Override
		@Nullable
		public ItemStack put(EntityEquipmentSlot key, ItemStack value) {
			return super.put(key, Objects.requireNonNull(value));
		}

		@Override
		public void clear() {
			this.replaceAll((slot, stack) -> ItemStack.EMPTY);
		}
		
		NBTTagList writeToNBT() {
			NBTTagList lst = new NBTTagList();
			this.forEach((slot, stack) -> {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte(DummyNBT.SLOT, (byte) slot.getSlotIndex());
				tag.setTag(DummyNBT.ITEM, stack.writeToNBT(new NBTTagCompound()));
				lst.appendTag(tag);
			});
			return lst;
		}
		
		void readFromNBT(NBTTagList lst) {
			lst.forEach((nbt) -> {
				NBTTagCompound tag = (NBTTagCompound) nbt;
				InventoryUtils.getSlotFromSlotIndex(tag.getByte(DummyNBT.SLOT)).ifPresent((slot) -> this.put(slot, new ItemStack(tag.getCompoundTag(DummyNBT.ITEM))));
			});
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onDamage(LivingDamageEvent evt) {
		EntityLivingBase entity = evt.getEntityLiving();
		if(!(entity instanceof EntityStoicDummy)) {
			return;
		}
		EntityStoicDummy dummy = (EntityStoicDummy) entity;
		dummy.damageBuilder.setFinalDamageTaken(evt.getAmount());
	}

	public static void setHistoryBuilder(BiFunction<DamageSource, Float, AbstractDamageInstanceBuilder> builder) {
		builderSupplier = builder;
	}
}
