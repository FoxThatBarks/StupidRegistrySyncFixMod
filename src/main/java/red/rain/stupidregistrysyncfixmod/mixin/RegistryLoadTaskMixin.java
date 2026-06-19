package red.rain.stupidregistrysyncfixmod.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryLoadTask;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(RegistryLoadTask.class)
public abstract class RegistryLoadTaskMixin<T> {

    @Shadow
    @Final
    private Object registryWriteLock;

    @Shadow
    @Final
    private WritableRegistry<T> registry;

    @Shadow
    private volatile boolean elementsRegistered;

    @Unique
    private static final Logger STUPIDREGISTRYSYNCFIXMOD$LOGGER = LogUtils.getLogger();

    @Inject(method = "registerElements", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings({"unchecked", "SynchronizeOnNonFinalField"})
    private void registerElements(final Stream<?> elements, final CallbackInfo ci) {
        synchronized (this.registryWriteLock) {
            elements.map(element -> (RegistryLoadTaskPendingRegistrationAccessor<T>) element).forEach(element -> element.stupidRegistrySyncFixMod$value().ifLeft(value -> this.registry.register(element.stupidRegistrySyncFixMod$key(), value, element.stupidRegistrySyncFixMod$registrationInfo())).ifRight(error -> {
                ResourceKey<T> key = element.stupidRegistrySyncFixMod$key();
                STUPIDREGISTRYSYNCFIXMOD$LOGGER.error("Skipping invalid registry entry {}: {}", key.identifier(), error.getMessage());
            }));
            this.elementsRegistered = true;
        }

        ci.cancel();
    }
}
