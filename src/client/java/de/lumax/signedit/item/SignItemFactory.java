package de.lumax.signedit.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
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
        CompoundTag blockEntityData = new CompoundTag();
        blockEntityData.putString(
                "id",
                hanging ? "minecraft:hanging_sign" : "minecraft:sign"
        );
        blockEntityData.putBoolean("is_waxed", true);
        blockEntityData.put(
                "front_text",
                encode(frontText)
        );
        blockEntityData.put(
                "back_text",
                encode(backText)
        );

        if (hanging) {
            stack.set(
                    DataComponents.BLOCK_ENTITY_DATA,
                    TypedEntityData.of(
                            BlockEntityTypes.HANGING_SIGN,
                            blockEntityData
                    )
            );
        } else {
            stack.set(
                    DataComponents.BLOCK_ENTITY_DATA,
                    TypedEntityData.of(
                            BlockEntityTypes.SIGN,
                            blockEntityData
                    )
            );
        }
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

        private static CompoundTag encode(SignText text) {
                return (CompoundTag) SignText.DIRECT_CODEC.encodeStart(
                                NbtOps.INSTANCE,
                                text
                ).getOrThrow();
        }

}