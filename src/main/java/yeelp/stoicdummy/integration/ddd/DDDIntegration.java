package yeelp.stoicdummy.integration.ddd;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.event.calculation.UpdateAdaptiveResistanceEvent;
import yeelp.distinctdamagedescriptions.event.classification.DetermineDamageEvent;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;
import yeelp.stoicdummy.ModConsts;
import yeelp.stoicdummy.ModConsts.DDDConsts;
import yeelp.stoicdummy.SDLogger;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.integration.ddd.DDDDamageInstance.DDDDamageInstanceBuilder;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class DDDIntegration {

    public static void init() {
        SDLogger.info("Stoic Dummy found {} ({})!", DDDConsts.DDD_TITLE, DDDConsts.DDD_ID);
        EntityStoicDummy.setHistoryBuilder(DDDDamageInstanceBuilder::new);
        MinecraftForge.EVENT_BUS.register(new DDDEventHandler());
    }

    public static final class DDDEventHandler {

        @SuppressWarnings("static-method")
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onDetermineDamage(DetermineDamageEvent evt) {
            handle(evt.getDefender(), evt::getDamage, DDDDamageInstanceBuilder::setInitialDistribution);
        }

        @SuppressWarnings("static-method")
        @SubscribeEvent
        public void onUpdateAdaptiveResistance(UpdateAdaptiveResistanceEvent evt) {
            handle(evt.getDefender(), evt::getDamage, DDDDamageInstanceBuilder::setFinalDistribution);
        }

        private static void handle(EntityLivingBase defender, Function<DDDDamageType, Float> extractor, BiConsumer<DDDDamageInstanceBuilder, Map<DDDDamageType, Float>> setter) {
            if(!(defender instanceof EntityStoicDummy)) {
                return;
            }
            setter.accept((DDDDamageInstanceBuilder) ((EntityStoicDummy) defender).getDamageBuilder(), extractDamage(extractor));
        }

        private static Map<DDDDamageType, Float> extractDamage(Function<DDDDamageType, Float> f) {
            Map<DDDDamageType, Float> map = Maps.newHashMap();
            DDDRegistries.damageTypes.forEach((type) -> map.put(type, f.apply(type)));
            return map;
        }
    }
}
