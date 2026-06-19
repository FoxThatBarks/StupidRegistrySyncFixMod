package red.rain.stupidregistrysyncfixmod.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.resources.RegistryLoadTask$PendingRegistration")
public interface RegistryLoadTaskPendingRegistrationAccessor<T> {

    @Accessor("key")
    ResourceKey<T> stupidRegistrySyncFixMod$key();

    @Accessor("value")
    Either<T, Exception> stupidRegistrySyncFixMod$value();

    @Accessor("registrationInfo")
    RegistrationInfo stupidRegistrySyncFixMod$registrationInfo();

}
