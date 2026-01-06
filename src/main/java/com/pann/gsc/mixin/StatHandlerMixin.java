package com.pann.gsc.mixin;

import com.pann.gsc.GSC;
import com.pann.gsc.client.FakeStatManager;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to intercept stat value retrieval and return fake values when enabled.
 * This only affects what the client displays - no data is modified on the server.
 */
@Mixin(StatHandler.class)
public abstract class StatHandlerMixin {

    private static boolean debugLogged = false;

    /**
     * FIXED: Inject into getStat(Stat<T>) - the actual method in Minecraft
     * Old code was trying to inject into getStat(StatType<T>, T) which doesn't exist!
     */
    @Inject(method = "getStat(Lnet/minecraft/stat/Stat;)I", at = @At("HEAD"), cancellable = true)
    private <T> void onGetStat(Stat<T> stat, CallbackInfoReturnable<Integer> cir) {
        // Log once when mixin is first called
        if (!debugLogged) {
            GSC.LOGGER.info("StatHandlerMixin is working! Mixin successfully injected.");
            debugLogged = true;
        }

        if (!FakeStatManager.isFakeStatsEnabled()) {
            return;
        }

        try {
            // Extract StatType and value from the Stat object
            StatType<T> type = stat.getType();
            T value = stat.getValue();

            // Get the identifier from the appropriate registry
            Identifier statValueId = getStatValueId(type, value);
            if (statValueId == null) {
                return;
            }

            // Determine the stat type name
            String statTypeName = getStatTypeName(type);
            if (statTypeName == null) {
                return;
            }

            // Check if we have a fake value for this stat
            Integer fakeValue = FakeStatManager.getFakeStat(statTypeName, statValueId);
            if (fakeValue != null) {
                GSC.LOGGER.debug("Returning fake stat: {} {} = {}", statTypeName, statValueId, fakeValue);
                cir.setReturnValue(fakeValue);
            }
        } catch (Exception e) {
            GSC.LOGGER.error("Error in StatHandlerMixin: {}", e.getMessage(), e);
        }
    }

    /**
     * Get the identifier for a stat value from the appropriate registry.
     */
    private <T> Identifier getStatValueId(StatType<T> statType, T value) {
        try {
            return statType.getRegistry().getId(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Determine the stat type name from a StatType object.
     * Maps stat types to our simplified type names.
     */
    private <T> String getStatTypeName(StatType<T> statType) {
        try {
            // Get the stat type identifier
            Identifier statTypeId = Registries.STAT_TYPE.getId(statType);
            if (statTypeId == null) {
                return null;
            }

            String statTypePath = statTypeId.getPath();

            // Match against known stat type paths
            return switch (statTypePath) {
                case "mined" -> "mined";
                case "crafted" -> "crafted";
                case "used" -> "used";
                case "broken" -> "broken";
                case "picked_up" -> "picked_up";
                case "dropped" -> "dropped";
                case "killed" -> "killed";
                case "killed_by" -> "killed_by";
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }
}