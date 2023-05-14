package me.gravityio.goodmc.mixin.lib.events;

import me.gravityio.goodmc.lib.events.ModEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "onCraft", at = @At("HEAD"))
    private void onCraftEvent(World world, PlayerEntity player, int amount, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        ModEvents.ON_CRAFT.invoker().onCraft(ModEvents.OnCraftEvent.CraftType.ANY, self, player);
    }

}
