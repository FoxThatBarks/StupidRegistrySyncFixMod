package red.rain.stupidregistrysyncfixmod.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {

    @Shadow
    @Final
    private Map<Identifier, Holder.Reference<T>> byLocation;

    @Shadow
    @Final
    private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow
    public abstract ResourceKey<? extends Registry<T>> key();

    @Unique
    private static final Logger STIPIDREGISTRYSYNCFIXMOD$LOGGER = LogUtils.getLogger();

    @Inject(method = "freeze", at = @At("HEAD"))
    private void freeze(final CallbackInfoReturnable<Registry<T>> cir) {
        final List<ResourceKey<T>> danglingKeys = new ArrayList<>();

        for (final Map.Entry<ResourceKey<T>, Holder.Reference<T>> entry : this.byKey.entrySet()) {
            if (!entry.getValue().isBound()) {
                danglingKeys.add(entry.getKey());
            }
        }

        if (danglingKeys.isEmpty()) {
            return;
        }

        for (final ResourceKey<T> danglingKey : danglingKeys) {
            this.byKey.remove(danglingKey);
            this.byLocation.remove(danglingKey.identifier());
        }

        STIPIDREGISTRYSYNCFIXMOD$LOGGER.warn("Removed {} unbound synced registry holders before freezing {}", danglingKeys.size(), this.key().identifier());
    }

}
