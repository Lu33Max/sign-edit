package de.lumax.signedit.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.List;

public final class SignItemFactory {

    private SignItemFactory() {
    }

    public static ItemStack create(
            WoodType woodType,
            boolean hanging,
            SignText frontText,
            SignText backText
    ) {
        Block signBlock = getSignBlock(woodType, hanging);
        ItemStack stack = new ItemStack(signBlock.asItem());
        stack.set(DataComponents.SIGN_TEXT_FRONT, frontText);
        stack.set(DataComponents.SIGN_TEXT_BACK, backText);
        stack.set(DataComponents.WAXED, Unit.INSTANCE);
        return stack;
    }

    public static List<WoodType> availableWoodTypes(boolean hanging) {
        return WoodType.values()
                .filter(woodType -> hasSignBlock(woodType, hanging))
                .toList();
    }

    private static boolean hasSignBlock(WoodType woodType, boolean hanging) {
        return getSignBlock(woodType, hanging) != null;
    }

    public static Block getSignBlock(WoodType woodType, boolean hanging) {
        Identifier id = Identifier.withDefaultNamespace(
                woodType.name() + (hanging ? "_hanging_sign" : "_sign")
        );
        Block block = BuiltInRegistries.BLOCK.getValue(id);

        return block != null && id.equals(BuiltInRegistries.BLOCK.getKey(block))
                ? block
                : null;
    }
}
