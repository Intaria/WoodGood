package net.mehvahdjukaar.every_compat.api;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.misc.ResourcesUtils;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.item.BlockTypeBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.BlockTypeResTransformer;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.leaves.LeavesType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;


//contrary to popular belief this class is indeed not simple. Its usage however is
public class SimpleEntrySet<T extends BlockType, B extends Block> extends AbstractSimpleEntrySet<T, B, Item> {

    protected final Supplier<@Nullable B> baseBlock;

    protected final Function<T, @Nullable B> blockFactory;
    @Nullable
    protected final TriFunction<T, B, Item.Properties, @Nullable Item> itemFactory;
    @Nullable
    protected final SimpleEntrySet.ITileHolder<?> tileHolder;

    protected final LootTableMode lootMode;
    @Nullable
    protected final Supplier<Supplier<RenderType>> renderType;


    public SimpleEntrySet(Class<T> type,
                          String name, @Nullable String prefix,
                          Function<T, B> blockSupplier,
                          Supplier<@Nullable B> baseBlock,
                          Supplier<T> baseType,
                          Supplier<ResourceKey<CreativeModeTab>> tab,
                          LootTableMode lootMode,
                          @Nullable TriFunction<T, B, Item.Properties, Item> itemFactory,
                          @Nullable SimpleEntrySet.ITileHolder<?> tileFactory,
                          @Nullable Supplier<Supplier<RenderType>> renderType,
                          @Nullable BiFunction<T, ResourceManager, Pair<List<Palette>, @Nullable AnimationMetadataSection>> paletteSupplier,
                          @Nullable Consumer<BlockTypeResTransformer<T>> extraTransform,
                          Predicate<T> condition) {
        super(type, name, prefix, baseType, tab, paletteSupplier, extraTransform, condition);
        this.blockFactory = blockSupplier;
        this.tileHolder = tileFactory;
        this.lootMode = lootMode;
        this.baseBlock = baseBlock;
        this.itemFactory = itemFactory;
        this.renderType = renderType;
    }


    public @Nullable ITileHolder<?> getTileHolder() {
        return tileHolder;
    }

    @Override
    public boolean isDisabled() {
        return this.getBaseBlock() == null;
    }

    public B getBaseBlock() {
        return baseBlock.get();
    }

    public void addTranslations(CompatModule module, AfterLanguageLoadEvent lang) {
        blocks.forEach((w, v) -> LangBuilder.addDynamicEntry(lang, "block_type." + module.getModId() + "." + typeName, w, v));
    }

    public void registerWoodBlocks(CompatModule module, Registrator<Block> registry, Collection<WoodType> woodTypes) {
        if (WoodType.class == getTypeClass()) {
            registerBlocks(module, registry, (Collection<T>) woodTypes);
        }
    }

    public void registerLeavesBlocks(CompatModule module, Registrator<Block> registry, Collection<LeavesType> leavesTypes) {
        if (LeavesType.class == getTypeClass()) {
            registerBlocks(module, registry, (Collection<T>) leavesTypes);
        }
    }

    @Override
    public void registerBlocks(CompatModule module, Registrator<Block> registry, Collection<T> woodTypes) {
        if (isDisabled()) return;
        Block base = getBaseBlock();
        if (base == null || base == Blocks.AIR)
            //?? wtf im using disabled to allow for null??
            throw new UnsupportedOperationException("Base block cant be null (" + this.typeName + " for " + module.modId + " module)");

        for (T w : woodTypes) {
            String name = getBlockName(w);
            String fullName = module.shortenedId() + "/" + w.getNamespace() + "/" + name;
            if (w.isVanilla() || module.isEntryAlreadyRegistered(name, w, BuiltInRegistries.BLOCK)) continue;

            if(condition.test(w)) {
                B block = blockFactory.apply(w);
                //for blocks that fail
                if (block != null) {
                    this.blocks.put(w, block);

                    registry.register(EveryCompat.res(fullName), block);
                    w.addChild(getChildKey(module),  block);

                    if (lootMode == LootTableMode.DROP_SELF && YEET_JSONS) {
                        SIMPLE_DROPS.add(block);
                    }
                }
            }
        }

        //attempts adding all other children

        String childKey = getChildKey(module);
        baseType.get().addChild(childKey,  base);

        Set<String> alreadySupportedMods = new HashSet<>(module.getAlreadySupportedMods());
        alreadySupportedMods.add(module.modId);
        var possibleNamespaces =  alreadySupportedMods.toArray(String[]::new);
        for(var w : BlockSetAPI.getTypeRegistry(this.getTypeClass()).getValues()){
            if(!items.containsKey(w)){
                String path = getBlockName(w);
                Block block = getOptionalBlock(path, w.getNamespace());
                if(block == null) block = getOptionalBlock(path, possibleNamespaces);
                if(block != null) w.addChild(childKey,  block);
            }
        }
    }

    @Nullable
    private static Block getOptionalBlock(String path, String ...namespaces) {
        ResourceLocation id;
        for (var n : namespaces) {
            id = new ResourceLocation(n, path);
            var i = BuiltInRegistries.BLOCK.getOptional(id);
            if(i.isPresent()){
                return i.get();
            }
        }
        return null;
    }

    @NotNull
    public String getBlockName(T w) {
        String name;
        if (prefix != null) {
            name = this.prefix + "_" + w.getTypeName();
            if (!this.postfix.isEmpty()) name += "_" + this.postfix;
        } else {
            name = w.getTypeName() + "_" + this.postfix;
        }
        return name;
    }

    @Override
    public void registerItems(CompatModule module, Registrator<Item> registry) {
        blocks.forEach((w, value) -> {
            Item i;

            if (itemFactory != null) {
                i = itemFactory.apply(w, value, new Item.Properties());
            } else {
                i = new BlockTypeBasedBlockItem<>(value, new Item.Properties(), w);
            }
            //for ones that don't have item
            if (i != null) {
                this.items.put(w, i);
                registry.register(Utils.getID(value), i);
            }
        });
    }

    @Override
    public void registerTiles(CompatModule module, Registrator<BlockEntityType<?>> registry) {
        if (isDisabled()) return;
        if (tileHolder instanceof NewTileHolder<?> nt) {
            var tile = nt.createInstance(blocks.values().toArray(Block[]::new));
            registry.register(EveryCompat.res(module.shortenedId() + "_" + this.getName()), tile);
        }
    }

    @Override
    public void registerEntityRenderers(CompatModule simpleModule, ClientHelper.BlockEntityRendererEvent event) {
        if (this.tileHolder != null) {
            //this.tileHolder.registerRenderer(event);
        }
    }

    @Override
    public void setupExistingTiles() {
        if (isDisabled()) return;
        if (tileHolder instanceof ExistingTileHolder<?> et) {
            SimpleModule.appendTileEntityBlocks(et.get(), blocks.values());
        }
    }

    @Override
    public void setRenderLayer() {
        if (isDisabled()) return;
        if (renderType != null) {
            blocks.values().forEach(t -> ClientHelper.registerRenderType(t, renderType.get().get()));
        }
    }

    @Override
    public void generateLootTables(CompatModule module, DynamicDataPack pack, ResourceManager manager) {
        if (isDisabled()) return;
        if (lootMode == LootTableMode.COPY_FROM_PARENT) {
            ResourceLocation reg = Utils.getID(getBaseBlock());
            ResourcesUtils.addBlockResources(module.getModId(), manager, pack, blocks, baseType.get().getTypeName(),
                    ResType.BLOCK_LOOT_TABLES.getPath(reg));

        } else if (lootMode == LootTableMode.DROP_SELF) {
            //drop self
            if (!YEET_JSONS) {
                blocks.forEach((wood, value) -> pack.addSimpleBlockLootTable(value));
            }
        }
    }

    @Override
    public void generateModels(CompatModule module, DynClientResourcesGenerator handler, ResourceManager manager) {
        if (isDisabled()) return;
        ResourcesUtils.addStandardResources(module.getModId(), manager, handler, blocks, baseType.get(), extraTransform);
    }

    //ok...
    public static <T extends BlockType, B extends Block> Builder<T, B> builder(Class<T> type,
                                                                               String name, Supplier<B> baseBlock, Supplier<T> baseType, Function<T, B> blockSupplier) {

        return new Builder<>(type, name, null, baseType, baseBlock, blockSupplier);
    }

    public static <T extends BlockType, B extends Block> Builder<T, B> builder(Class<T> type,
                                                                               String name, String prefix, Supplier<B> baseBlock, Supplier<T> baseType, Function<T, B> blockSupplier) {

        return new Builder<>(type, name, prefix, baseType, baseBlock, blockSupplier);
    }

    public static class Builder<T extends BlockType, B extends Block> extends AbstractSimpleEntrySet.Builder<Builder<T,B>, T,B,Item> {
        protected final Supplier<@Nullable B> baseBlock;
        protected LootTableMode lootMode = LootTableMode.DROP_SELF;
        protected final Function<T, B> blockFactory;
        @Nullable
        protected TriFunction<T, B, Item.Properties, Item> itemFactory;
        @Nullable
        protected SimpleEntrySet.ITileHolder<?> tileHolder;
        @Nullable
        protected Supplier<Supplier<RenderType>> renderType = null;

        protected Builder(Class<T> type, String name, @Nullable String prefix, Supplier<T> baseType, Supplier<B> baseBlock, Function<T, B> blockFactory) {
            super(type, name, prefix, baseType);
            this.baseBlock = baseBlock;
            this.blockFactory = blockFactory;
        }

        public SimpleEntrySet<T, B> build() {
            var e = new SimpleEntrySet<>(type, name, prefix, blockFactory, baseBlock, baseType, tab, lootMode,
                    itemFactory, tileHolder, renderType, palette, extraModelTransform, condition);
            e.recipeLocations.addAll(this.recipes);
            e.tags.putAll(this.tags);
            e.textures.addAll(textures);
            return e;
        }

        public <H extends BlockEntity> Builder<T, B> addTile(Supplier<BlockEntityType<H>> tile) {
            this.tileHolder = new ExistingTileHolder<>(tile);
            return this;
        }

        public <H extends BlockEntity> Builder<T, B> addTile(BiFunction<BlockPos, BlockState, H> tileFactory) {
            this.tileHolder = new NewTileHolder<>(tileFactory);
            return this;
        }

        public Builder<T, B> addCustomItem(TriFunction<T, B, Item.Properties, Item> itemFactory) {
            this.itemFactory = itemFactory;
            return this;
        }

        public Builder<T, B> noItem() {
            this.itemFactory = (a, b, c) -> null;
            return this;
        }

        /**
         * As opposed to just dropping itself
         */
        public Builder<T, B> copyParentDrop() {
            this.lootMode = LootTableMode.COPY_FROM_PARENT;
            return this;
        }

        //default
        public Builder<T, B> dropSelf() {
            this.lootMode = LootTableMode.DROP_SELF;
            return this;
        }

        public Builder<T, B> noDrops() {
            this.lootMode = LootTableMode.NO_LOOT;
            return this;
        }

        public Builder<T, B> setRenderType(Supplier<Supplier<RenderType>> renderType) {
            this.renderType = renderType;
            return this;
        }

        public Builder<T, B> defaultRecipe() {
            this.recipes.add(() -> Utils.getID(this.baseBlock.get()));
            return this;
        }
    }


    public interface ITileHolder<H extends BlockEntity> {

        BlockEntityType<? extends H> get();
    }

    public record ExistingTileHolder<H extends BlockEntity>(
            Supplier<BlockEntityType<H>> supplier) implements ITileHolder<H> {

        @Override
        public BlockEntityType<? extends H> get() {
            return supplier.get();
        }
    }

    public static class NewTileHolder<H extends BlockEntity> implements ITileHolder<H> {

        protected final BiFunction<BlockPos, BlockState, H> tileFactory;
        protected Supplier<BlockEntityRendererProvider<H>> renderer = null;
        public BlockEntityType<? extends H> tile = null;


        public NewTileHolder(BiFunction<BlockPos, BlockState, H> tileFactory) {
            this.tileFactory = tileFactory;
        }

        public BlockEntityType<? extends H> get() {
            return tile;
        }

        public BlockEntityType<? extends H> createInstance(Block... blocks) {
            if (tile != null) throw new UnsupportedOperationException("tile has already been created");
            this.tile = PlatHelper.newBlockEntityType(tileFactory::apply, blocks);
            return tile;
        }
    }


    protected static final boolean YEET_JSONS = true;
    protected static final Set<Block> SIMPLE_DROPS = new HashSet<>();

    public static boolean isSimpleDrop(Block block) {
        return SIMPLE_DROPS.contains(block);
    }

    public enum LootTableMode {
        DROP_SELF,
        COPY_FROM_PARENT,
        NO_LOOT
    }
}
