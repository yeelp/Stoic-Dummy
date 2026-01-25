package yeelp.stoicdummy.integration.ddd;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;
import yeelp.stoicdummy.ModConsts.DDDConsts;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.util.AbstractDamageInstance;
import yeelp.stoicdummy.util.Translations.Translator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class DDDDamageInstance extends AbstractDamageInstance {

    private static final Translator TRANSLATOR = AbstractDamageInstance.TRANSLATOR.deriveSubTranslator(DDDConsts.DDD_ID_SHORT);
    private static final ITextComponent INITIAL_DIST = TRANSLATOR.getComponent(DDDConsts.DDDTranslationKeys.INITIAL_DIST);
    private static final ITextComponent FINAL_DIST = TRANSLATOR.getComponent(DDDConsts.DDDTranslationKeys.FINAL_DIST);
    private final Map<DDDDamageType, Float> damageBefore, damageAfter;

    private DDDDamageInstance(DamageSource src, float amountBefore, float amountAfter, Map<DDDDamageType, Float> initialDist, Map<DDDDamageType, Float> finalDist) {
        super(src, amountBefore, amountAfter);
        this.damageBefore = initialDist;
        this.damageAfter = finalDist;
    }

    public DDDDamageInstance(NBTTagCompound nbt) {
        super(nbt);
        this.damageBefore = Maps.newHashMap();
        this.damageAfter = Maps.newHashMap();
        this.populateMap(nbt.getTagList(DDDConsts.INITIAL_DIST, DummyNBT.TAG_COMPOUND_ID), this.damageBefore);
        this.populateMap(nbt.getTagList(DDDConsts.FINAL_DIST, DummyNBT.TAG_COMPOUND_ID), this.damageAfter);
    }

    private void populateMap(NBTTagList lst, Map<DDDDamageType, Float> map) {
        lst.forEach((nbtBase) -> {
            NBTTagCompound compound = (NBTTagCompound) nbtBase;
            map.put(DDDRegistries.damageTypes.get(compound.getString(DDDConsts.DAMAGE_TYPE)), compound.getFloat(DDDConsts.AMOUNT));
        });
    }

    @Override
    protected void writeSpecificNBT(NBTTagCompound compound) {
        compound.setByte(DummyNBT.TYPE, DDDConsts.DDD_DAMAGE_INSTANCE_ID);
        compound.setTag(DDDConsts.INITIAL_DIST, this.writeMapToNBT(this.damageBefore));
        compound.setTag(DDDConsts.FINAL_DIST, this.writeMapToNBT(this.damageAfter));
    }

    private NBTTagList writeMapToNBT(Map<DDDDamageType, Float> map) {
        NBTTagList lst = new NBTTagList();
        map.forEach((type, amount) -> {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString(DDDConsts.DAMAGE_TYPE, type.getTypeName());
            compound.setFloat(DDDConsts.AMOUNT, amount);
            lst.appendTag(compound);
        });
        return lst;
    }

    @Override
    protected Collection<String> getSpecificInfoForIteration() {
        List<String> lst = Lists.newArrayList();
        lst.add(highlight(INITIAL_DIST.getFormattedText()) + SPLIT);
        lst.addAll(this.getMapAsStrings(this.damageBefore));
        lst.add(highlight(FINAL_DIST.getFormattedText()) + SPLIT);
        lst.addAll(this.getMapAsStrings(this.damageAfter));
        return lst;
    }

    private Collection<String> getMapAsStrings(Map<DDDDamageType, Float> map) {
        List<String> lst = Lists.newArrayList();
        map.forEach((type, amount) -> lst.add("  " + type.getFormattedDisplayName() + SPLIT + formatDamage(amount, this.damageBefore.get(type))));
        return lst;
    }

    private String formatDamage(float damage, float ref) {
        StringBuilder sb = new StringBuilder();
        if(damage > ref) {
            sb.append(TextFormatting.GREEN);
        }
        else if(damage < ref) {
            sb.append(TextFormatting.RED);
        }
        return sb.append(AbstractDamageInstance.formatDamage(damage)).append(TextFormatting.RESET).toString();
    }

    public static final class DDDDamageInstanceBuilder extends AbstractDamageInstanceBuilder {

        private Map<DDDDamageType, Float> initialMap, finalMap;
        public DDDDamageInstanceBuilder(DamageSource src, float amountBefore) {
            super(src, amountBefore);
        }

        public void setInitialDistribution(Map<DDDDamageType, Float> map) {
            this.initialMap = map;
        }

        public void setFinalDistribution(Map<DDDDamageType, Float> map) {
            this.finalMap = map;
        }

        @Override
        public AbstractDamageInstance build() {
            this.initialMap.entrySet().stream().filter((entry) -> entry.getValue() == 0 && this.finalMap.get(entry.getKey()) == 0).map(Entry::getKey).collect(Collectors.toList()).forEach((type) -> {
                this.initialMap.remove(type);
                this.finalMap.remove(type);
            });
            return new DDDDamageInstance(this.getSource(), this.getInitialDamage(), this.getFinalDamage(), this.initialMap, this.finalMap);
        }
    }
}
