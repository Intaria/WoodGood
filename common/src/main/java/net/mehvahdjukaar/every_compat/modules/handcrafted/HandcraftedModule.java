package net.mehvahdjukaar.every_compat.modules.handcrafted;

import earth.terrarium.handcrafted.Handcrafted;
import earth.terrarium.handcrafted.common.block.ItemHoldingBlockEntity;
import earth.terrarium.handcrafted.common.block.chair.chair.ChairBlock;
import earth.terrarium.handcrafted.common.block.chair.chair.ChairBlockEntity;
import earth.terrarium.handcrafted.common.block.chair.couch.CouchBlock;
import earth.terrarium.handcrafted.common.block.chair.couch.CouchBlockEntity;
import earth.terrarium.handcrafted.common.block.chair.diningbench.DiningBenchBlock;
import earth.terrarium.handcrafted.common.block.chair.diningbench.DiningBenchBlockEntity;
import earth.terrarium.handcrafted.common.block.chair.woodenbench.WoodenBenchBlock;
import earth.terrarium.handcrafted.common.block.chair.woodenbench.WoodenBenchBlockEntity;
import earth.terrarium.handcrafted.common.block.counter.CounterBlock;
import earth.terrarium.handcrafted.common.block.counter.CounterBlockEntity;
import earth.terrarium.handcrafted.common.block.fancybed.FancyBedBlock;
import earth.terrarium.handcrafted.common.block.fancybed.FancyBedBlockEntity;
import earth.terrarium.handcrafted.common.block.table.desk.DeskBlock;
import earth.terrarium.handcrafted.common.block.table.desk.DeskBlockEntity;
import earth.terrarium.handcrafted.common.block.table.nightstand.NightstandBlock;
import earth.terrarium.handcrafted.common.block.table.nightstand.NightstandBlockEntity;
import earth.terrarium.handcrafted.common.block.table.sidetable.SideTableBlock;
import earth.terrarium.handcrafted.common.block.table.sidetable.SideTableBlockEntity;
import earth.terrarium.handcrafted.common.block.table.table.TableBlock;
import earth.terrarium.handcrafted.common.block.table.table.TableBlockEntity;
import earth.terrarium.handcrafted.common.item.CounterBlockItem;
import earth.terrarium.handcrafted.common.registry.ModBlockEntityTypes;
import earth.terrarium.handcrafted.common.registry.ModBlocks;
import earth.terrarium.handcrafted.common.registry.ModItems;
import earth.terrarium.handcrafted.common.registry.ModTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.api.ItemOnlyEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleEntrySet;
import net.mehvahdjukaar.every_compat.api.SimpleModule;
import net.mehvahdjukaar.every_compat.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.every_compat.modules.handcrafted.client.*;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.util.*;
import java.util.stream.Collectors;


public class HandcraftedModule extends SimpleModule {

    public final SimpleEntrySet<WoodType, Block> CHAIR; // CUSHION
    public final SimpleEntrySet<WoodType, Block> TABLE; // CLOTHINGS
    public final SimpleEntrySet<WoodType, Block> BENCH; // CUSHION  DONE
    public final SimpleEntrySet<WoodType, Block> COUCH; // CUSHION, Multiple-Model DONE
    public final SimpleEntrySet<WoodType, Block> FANCY_BED; // CUSHION & CLOTHINGS DONE
    public final SimpleEntrySet<WoodType, Block> DINING_BENCH; // Multiple-Model
    public final SimpleEntrySet<WoodType, Block> NIGHTSTAND; // CLOTHINGS  DONE
    public final SimpleEntrySet<WoodType, Block> DESK; // CLOTHINGS  DONE
    public final SimpleEntrySet<WoodType, Block> SIDE_TABLE; // CLOTHINGS  DONE

    public final ItemOnlyEntrySet<WoodType, Item> COUNTER;
    public final SimpleEntrySet<WoodType, Block> COUNTER_1/*, COUNTER_2, COUNTER_3*/;
/*
    public final SimpleEntrySet<WoodType, Block> CUPBOARD_1, CUPBOARD_2;

    public final SimpleEntrySet<WoodType, Block> DRAWER_1, DRAWER_2, DRAWER_3, DRAWER_4;

    public final SimpleEntrySet<WoodType, Block> SHELF_1; // DONE
    public final SimpleEntrySet<WoodType, Block> PILLAR_TRIM; // DONE
    public final SimpleEntrySet<WoodType, Block> CORNER_TRIM; // DONE

    public final ItemOnlyEntrySet<WoodType, Item> BOARD;
*/
    public BlockEntityType<? extends compatCounterEntity> counterTile;


    public HandcraftedModule(String modId) {
        super(modId, "hc");
        CreativeModeTab tab = ModItems.ITEM_GROUP;
        {
            CHAIR = SimpleEntrySet.builder(WoodType.class, "chair",
                            ModBlocks.OAK_CHAIR, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatChairBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTile(compatChairEntity::new)
                    .setRenderType(() -> RenderType::cutout)
                    .addTexture(modRes("block/chair/chair/oak_chair"))
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .setTab(() -> tab)
                    .addCustomItem((w, b, p) -> new compatModItems.ChairItem(b, p))
//                .defaultRecipe()
                    .build();
            this.addEntry(CHAIR);

            TABLE = SimpleEntrySet.builder(WoodType.class, "table",
                            ModBlocks.OAK_TABLE, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatTableBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTile(compatTableEntity::new)
                    .setRenderType(() -> RenderType::cutout)
                    .addTexture(modRes("block/table/table/oak_table"))
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .addTag(ModTags.TABLE_ATTACHMENTS, Registry.BLOCK_REGISTRY)
                    .setTab(() -> tab)
                    .addCustomItem((w, b, p) -> new compatModItems.TableItem(b, p))
//                .defaultRecipe()
                    .build();
            this.addEntry(TABLE);

            BENCH = SimpleEntrySet.builder(WoodType.class, "bench",
                            ModBlocks.OAK_BENCH, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatBenchBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTile(compatBenchEntity::new)
                    .setRenderType(() -> RenderType::cutout)
                    .addTexture(modRes("block/chair/bench/oak_bench"))
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .setTab(() -> tab)
                    .addCustomItem((w, b, p) -> new compatModItems.BenchItem(b, p))
//                .defaultRecipe()
                    .build();
            this.addEntry(BENCH);

            COUCH = SimpleEntrySet.builder(WoodType.class, "couch",
                            ModBlocks.OAK_COUCH, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatFancyBedBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTile(compatCouchEntity::new)
                    .setRenderType(() -> RenderType::cutout)
                    .addTexture(modRes("block/chair/couch/oak_couch"))
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .setTab(() -> tab)
                    .addCustomItem((w, b, p) -> new compatModItems.CouchItem(b, p))
//                .defaultRecipe()
                    .build();
            this.addEntry(COUCH);

            FANCY_BED = SimpleEntrySet.builder(WoodType.class, "fancy_bed",
                            ModBlocks.OAK_FANCY_BED, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatFancyBedBlock(Utils.copyPropertySafe(Blocks.WHITE_BED)))
                    .addTile(compatFancyBedEntity::new)
                    .setRenderType(() -> RenderType::cutout)
                    .addTexture(modRes("block/bed/single/oak_fancy_bed"))
                    .addTexture(modRes("block/bed/double/oak_fancy_bed"))
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .setTab(() -> tab)
                    .addCustomItem((w, b, p) -> new compatModItems.FancyBedItem(b, p))
//                .defaultRecipe()
                    .build();
            this.addEntry(FANCY_BED);

            DINING_BENCH = SimpleEntrySet.builder(WoodType.class, "dining_bench",
                            ModBlocks.OAK_DINING_BENCH, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatDiningBenchBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTile(compatDiningBenchEntity::new)
                    .setRenderType(() -> RenderType::cutout)
                    .addTexture(modRes("block/chair/dining_bench/oak_dining_bench"))
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .setTab(() -> tab)
                    .addCustomItem((w, b, p) -> new compatModItems.DiningBenchItem(b, p))
//                .defaultRecipe()
                    .build();
            this.addEntry(DINING_BENCH);

            NIGHTSTAND = SimpleEntrySet.builder(WoodType.class, "nightstand",
                            ModBlocks.OAK_NIGHTSTAND, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatNightstandBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .addTextureM(modRes("block/table/nightstand/oak_nightstand"), EveryCompat.res("block/hc/table/oak_nightstand_m"))
                    .setRenderType(() -> RenderType::cutout)
                    .setTab(() -> tab)
//                .defaultRecipe()
                    .addCustomItem((w, b, p) -> new compatModItems.NightstandItem(b, p))
                    .addTile(compatNightstandEntity::new)
                    .build();

            this.addEntry(NIGHTSTAND);

            DESK = SimpleEntrySet.builder(WoodType.class, "desk",
                            ModBlocks.OAK_DESK, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatDeskBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .addTextureM(modRes("block/table/desk/oak_desk"), EveryCompat.res("block/hc/table/oak_desk_m"))
                    .setRenderType(() -> RenderType::cutout)
                    .setTab(() -> tab)
//                .defaultRecipe()
                    .addCustomItem((w, b, p) -> new compatModItems.DeskItem(b, p))
                    .addTile(compatDeskEntity::new)
                    .build();
            this.addEntry(DESK);

            SIDE_TABLE = SimpleEntrySet.builder(WoodType.class, "side_table",
                            ModBlocks.OAK_SIDE_TABLE, () -> WoodTypeRegistry.OAK_TYPE,
                            w -> new compatSideTableBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                    )
                    .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                    .addTextureM(modRes("block/table/side_table/oak_side_table"), EveryCompat.res("block/hc/table/oak_side_table_m"))
                    .setRenderType(() -> RenderType::cutout)
                    .setTab(() -> tab)
//                .defaultRecipe()
                    .addCustomItem((w, b, p) -> new compatModItems.SideTableItem(b, p))
                    .addTile(compatSideTableEntity::new)
                    .build();
            this.addEntry(SIDE_TABLE);

        }
        COUNTER_1 = SimpleEntrySet.builder(WoodType.class, "counter_1",
                        ModBlocks.OAK_COUNTER_1, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new compatCounterBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                )
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/counter/oak_counter_1"))
                .addTexture(modRes("block/counter/counter/overlay/oak_planks"))
                .setRenderType(() -> RenderType::cutout)
                .addTile(compatCounterEntity::new)
                .noItem()
                .build();
        this.addEntry(COUNTER_1);

/*        COUNTER_2 = SimpleEntrySet.builder(WoodType.class, "counter_2",
                        ModBlocks.OAK_COUNTER_2, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new compatCounterBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                        )
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/counter/oak_counter_2"))
                .setRenderType(() -> RenderType::cutout)
                .addTile(compatCounterEntity::new)
                .build();
        this.addEntry(COUNTER_2);

        COUNTER_3 = SimpleEntrySet.builder(WoodType.class, "counter_3",
                        ModBlocks.OAK_COUNTER_3, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new compatCounterBlock(Utils.copyPropertySafe(w.planks).noOcclusion())
                        )
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/counter/oak_counter_3"))
                .setRenderType(() -> RenderType::cutout)
                .addTile(compatCounterEntity::new)
                .build();
        this.addEntry(COUNTER_3);*/

        COUNTER = ItemOnlyEntrySet.builder(WoodType.class, "counter",
                        () -> getModItem("oak_counter"), () -> WoodTypeRegistry.OAK_TYPE,
                w -> new compatModItems.CounterItem(COUNTER_1.blocks.get(w), new Item.Properties().tab(tab))
                )
                .addCondition(COUNTER_1.blocks::containsKey)
                .build();
        this.addEntry(COUNTER);
/*
*/

/*
        CUPBOARD_1 = SimpleEntrySet.builder(WoodType.class, "cupboard_1",
                        ModBlocks.OAK_CUPBOARD_1, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new CupboardBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_1"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_2"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_back"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_side"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .addCustomItem((w, b, p) -> new HammerableBlockItem(b, p))
                .build();
        this.addEntry(CUPBOARD_1);

        CUPBOARD_2 = SimpleEntrySet.builder(WoodType.class, "cupboard_2",
                        ModBlocks.OAK_CUPBOARD_2, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new CupboardBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_2"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_back"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_side"))
                .addTexture(modRes("block/counter/cupboard/oak/cupboard_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .build();
        this.addEntry(CUPBOARD_2);


        DRAWER_1 = SimpleEntrySet.builder(WoodType.class, "drawer_1",
                        ModBlocks.OAK_DRAWER_1, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new DrawerBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/drawer/oak/front_1/drawer_left"))
                .addTexture(modRes("block/counter/drawer/oak/front_1/drawer_middle"))
                .addTexture(modRes("block/counter/drawer/oak/front_1/drawer_right"))
                .addTexture(modRes("block/counter/drawer/oak/front_1/drawer_single"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_left"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_middle"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_right"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_single"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_left"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_middle"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_right"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_single"))
                .addTexture(modRes("block/counter/drawer/oak/front_4/drawer"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_back"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_bottom"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_left"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_right"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .addCustomItem((w, b, p) -> new HammerableBlockItem(b, p))
                .build();
        this.addEntry(DRAWER_1);

        DRAWER_2 = SimpleEntrySet.builder(WoodType.class, "drawer_2",
                        ModBlocks.OAK_DRAWER_2, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new DrawerBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_left"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_middle"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_right"))
                .addTexture(modRes("block/counter/drawer/oak/front_2/drawer_single"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_back"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_bottom"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_left"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_right"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .build();
        this.addEntry(DRAWER_2);

        DRAWER_3 = SimpleEntrySet.builder(WoodType.class, "drawer_3",
                        ModBlocks.OAK_DRAWER_3, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new DrawerBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_left"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_middle"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_right"))
                .addTexture(modRes("block/counter/drawer/oak/front_3/drawer_single"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_back"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_bottom"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_left"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_right"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .build();
        this.addEntry(DRAWER_3);

        DRAWER_4 = SimpleEntrySet.builder(WoodType.class, "drawer_4",
                        ModBlocks.OAK_DRAWER_4, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new DrawerBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/drawer/oak/front_4/drawer"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_back"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_bottom"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_left"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_side_right"))
                .addTexture(modRes("block/counter/drawer/oak/drawer_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .build();
        this.addEntry(DRAWER_4);


        SHELF_1 = SimpleEntrySet.builder(WoodType.class, "shelf",
                        ModBlocks.OAK_SHELF_1, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new DiningBenchBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/counter/shelf/oak/shelf_back"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_left"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_middle"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_right"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_side_left"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_side_right"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_single"))
                .addTexture(modRes("block/counter/shelf/oak/shelf_top"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .addCustomItem((w, b, p) -> new ShelfBlockItem(b, p))
                .build();
        this.addEntry(SHELF_1);


        PILLAR_TRIM = SimpleEntrySet.builder(WoodType.class, "pillar_trim",
                        ModBlocks.OAK_PILLAR_TRIM, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new TrimBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/trim/pillar/oak_pillar_trim"))
                .addTexture(modRes("block/trim/pillar/oak_pillar_trim_2"))
                .addTexture(modRes("block/trim/pillar/oak_thicc_pillar_trim"))
                .addTexture(modRes("block/trim/pillar/oak_thicc_pillar_trim_2"))
                .addTexture(modRes("block/trim/pillar/oak_thin_pillar_trim"))
                .addTexture(modRes("block/trim/pillar/oak_thin_pillar_trim_2"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .addCustomItem((w, b, p) -> new HammerableBlockItem(b, p))
                .build();
        this.addEntry(PILLAR_TRIM);

        CORNER_TRIM = SimpleEntrySet.builder(WoodType.class, "corner_trim",
                        ModBlocks.OAK_CORNER_TRIM, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new CornerTrimBlock(Utils.copyPropertySafe(w.planks).noOcclusion()))
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("block/trim/corner/oak_corner_trim"))
                .addTexture(modRes("block/trim/corner/oak_thicc_corner_trim"))
                .addTexture(modRes("block/trim/corner/oak_thin_corner_trim"))
                .setRenderType(() -> RenderType::cutout)
                .setTab(() -> tab)
//                .defaultRecipe()
                .addCustomItem((w, b, p) -> new HammerableBlockItem(b, p))
                .build();
        this.addEntry(CORNER_TRIM);

        BOARD = ItemOnlyEntrySet.builder(WoodType.class, "board",
                        ModItems.OAK_BOARD, () -> WoodTypeRegistry.OAK_TYPE,
                        w -> new BoardItem(new Item.Properties())
                )
                .addTag(BlockTags.MINEABLE_WITH_AXE, Registry.BLOCK_REGISTRY)
                .addTexture(modRes("item/board/oak_board"))
//                .defaultRecipe()
                .build();
        this.addEntry(BOARD);


*/
    }

    @Override
    public void registerTiles(Registrator<BlockEntityType<?>> registry) {
        super.registerTiles(registry);
        counterTile = (BlockEntityType<? extends compatCounterEntity>) Objects.requireNonNull(COUNTER_1.getTileHolder().get());
//        counterTile = (BlockEntityType<? extends compatCounterEntity>) Objects.requireNonNull(COUNTER_2.getTileHolder().get());
//        counterTile = (BlockEntityType<? extends compatCounterEntity>) Objects.requireNonNull(COUNTER_3.getTileHolder().get());

    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerBlockEntityRenderers(ClientPlatformHelper.BlockEntityRendererEvent event) {
        event.register(((BlockEntityType) CHAIR.getTileHolder().get()), OptimizedChairRenderer::new);
        event.register(((BlockEntityType) TABLE.getTileHolder().get()), OptimizedTableRenderer::new);
        event.register(((BlockEntityType) BENCH.getTileHolder().get()), OptimizedBenchRenderer::new);
        event.register(((BlockEntityType) COUCH.getTileHolder().get()), OptimizedCouchRenderer::new);
        event.register(((BlockEntityType) FANCY_BED.getTileHolder().get()), OptimizedFancyBedRenderer::new);
        event.register(((BlockEntityType) DINING_BENCH.getTileHolder().get()), OptimizedDiningBenchRenderer::new);
        event.register((BlockEntityType) NIGHTSTAND.getTileHolder().get(), OptimizedNightstandRenderer::new);
        event.register((BlockEntityType) DESK.getTileHolder().get(), OptimizedDeskRenderer::new);
        event.register((BlockEntityType) SIDE_TABLE.getTileHolder().get(), OptimizedSideTableRenderer::new);

        event.register((BlockEntityType) counterTile, OptimizedCounterRenderer::new);
//        event.register(, OptimizedCounterRenderer::new);

//        event.register(((BlockEntityType) COUNTER_1.getTileHolder().get()), OptimizedCounterRenderer::new);
//        event.register(((BlockEntityType) COUNTER_2.getTileHolder().get()), CounterRenderer::new);
//        event.register(((BlockEntityType) COUNTER_3.getTileHolder().get()), CounterRenderer::new);
    }

    //TYPE: ================ stitchAtlasTextures
    @Override
    public void stitchAtlasTextures(ClientPlatformHelper.AtlasTextureEvent event) {
        String hcFolder = "block/" + shortenedId() + "/";

        if (OptimizedTableRenderer.OBJECT_TO_TEXTURE.isEmpty()) {

            //==================================== CLOTHING | CUSHION ==================================================
                // CLOTHING - used by TABLE, NIGHTSTAND, DESK, SIDE_TABLE
            for (var dye : DyeColor.values()) {
                Item sheetItem = Registry.ITEM.get(this.modRes(dye.getName() + "_sheet"));
                String dyeName = dye.getName() + "_sheet";
                if (sheetItem != Items.AIR) {
                    var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(sheetItem, ignored ->
                            new Material(TextureAtlas.LOCATION_BLOCKS,
                                    modRes("block/table/table_cloth/" + dyeName)));
                    event.addSprite(texture.texture());

                // SHEET - used by FANCY_BED
                    var bed_sheet = OptimizedFancyBedRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(sheetItem, ignored ->
                            new Material(TextureAtlas.LOCATION_BLOCKS,
                                    modRes("block/bed/sheet/" + dyeName)));
                    event.addSprite(bed_sheet.texture());
                }
            }

                // CUSHION - used by CHAIR, BENCH, COUCH
            for (var dye : DyeColor.values()) {
                String dyeName = dye.getName() + "_cushion";
                Item cushionItem = Registry.ITEM.get(this.modRes(dyeName));

                if (cushionItem != Items.AIR) {
                    // CHAIR
                    var chairTexture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(cushionItem, ignored ->
                            new Material(TextureAtlas.LOCATION_BLOCKS,
                                    modRes("block/chair/chair/cushion/" + dyeName))
                    );
                    event.addSprite(chairTexture.texture());

                    // BENCH
                    var benchTexture = OptimizedBenchRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(cushionItem, ignored ->
                            new Material(TextureAtlas.LOCATION_BLOCKS,
                                    modRes("block/chair/bench/cushion/" + dyeName))
                    );
                    event.addSprite(benchTexture.texture());

                    // COUCH
                    var couchTexture = OptimizedCouchRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(cushionItem, ignored ->
                            new Material(TextureAtlas.LOCATION_BLOCKS,
                                    modRes("block/chair/couch/cushion/" + dyeName))
                    );
                    event.addSprite(couchTexture.texture());

                // CUSHION - used by FANCY_BED
                    var bed_cushion = OptimizedFancyBedRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(cushionItem, ignored ->
                            new Material(TextureAtlas.LOCATION_BLOCKS,
                                    modRes("block/bed/cushion/" + dyeName))
                    );
                    event.addSprite(bed_cushion.texture());
                }
            }
            // ======================================== TEXTURE FOR BLOCKS =============================================
                // TABLE
            for (var t : TABLE.items.values()) {
                var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/table/table/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // CHAIR
            for (var t : CHAIR.items.values()) {
                var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/chair/chair/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // BENCH
            for (var t : BENCH.items.values()) {
                var texture = OptimizedBenchRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/chair/bench/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // COUCH
            for (var t : COUCH.items.values()) {
                var texture = OptimizedCouchRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/chair/couch/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // FANCY BED
            for (var t : FANCY_BED.items.values()) {
                var singleBed = OptimizedFancyBedRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ignored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/bed/single/" + s[2]));
                });
                event.addSprite(singleBed.texture());

                var doubleBed = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ignored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/bed/double/" + s[2]));
                });
                event.addSprite(doubleBed.texture());
            }

                // DINING_BENCH
            for (var t : DINING_BENCH.items.values()) {
                var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/chair/dining_bench/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // NIGHTSTAND
            for (var t : NIGHTSTAND.items.values()) {
                var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/table/nightstand/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // DESK
            for (var t : DESK.items.values()) {
                var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/table/desk/" + s[2]));
                });
                event.addSprite(texture.texture());
            }

                // SIDE_TABLE
            for (var t : SIDE_TABLE.items.values()) {
                var texture = OptimizedTableRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(t, ingored -> {
                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/table/side_table/" + s[2]));
                });
                event.addSprite(texture.texture());
            }


                // COUNTER
            for (var entry : COUNTER.items.entrySet()) {
                ResourceLocation itemId = Registry.ITEM.getKey(entry.getValue());
                var itemTexture = OptimizedCounterRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(itemId, ignored -> {
                    var s = itemId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/counter/counter/" + s[2] + "_1"));
                });
                event.addSprite(itemTexture.texture());
            }

            for (var entry : COUNTER_1.blocks.entrySet()) {
                ResourceLocation blockId = Registry.BLOCK.getKey(entry.getValue());
                var blockTexture = OptimizedCounterRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(blockId, ignored -> {
//                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/counter/counter/" + s[2]));
                });
                event.addSprite(blockTexture.texture());

                WoodType woodType = entry.getKey();
                ResourceLocation planksFromMods = Utils.getID(woodType.planks);
                var oTexture = OptimizedCounterRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(planksFromMods, ignored -> {
                        return new Material(TextureAtlas.LOCATION_BLOCKS,
                                EveryCompat.res(hcFolder + woodType.getNamespace() + "/counter/counter/overlay/" + woodType.getTypeName() + "_planks"));
                        });
                event.addSprite(oTexture.texture());

            }

/*            for (var entry : COUNTER_2.blocks.entrySet()) {
                ResourceLocation blockId = Registry.BLOCK.getKey(entry.getValue());
                var blockTexture = OptimizedCounterRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(blockId, ignored -> {
//                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/counter/counter/" + s[2]));
                });
                event.addSprite(blockTexture.texture());
            }

            for (var entry : COUNTER_3.blocks.entrySet()) {
                ResourceLocation blockId = Registry.BLOCK.getKey(entry.getValue());
                var blockTexture = OptimizedCounterRenderer.OBJECT_TO_TEXTURE.computeIfAbsent(blockId, ignored -> {
//                    var blockId = Registry.ITEM.getKey(t);
                    var s = blockId.getPath().split("/");
                    return new Material(TextureAtlas.LOCATION_BLOCKS,
                            EveryCompat.res(hcFolder + s[1] + "/counter/counter/" + s[2]));
                });
                event.addSprite(blockTexture.texture());
            }*/
        }
//        String vanillaOverlay[] = {
//                "acacia_planks", "andesite", "birch_planks", "blackstone", "bricks", "cacite", "crimson_planks",
//                "dark_oak_planks", "deepslate", "diorite", "dripsone_block", "granite", "jungle_planks", "magrove_planks",
//                "oak_planks", "quartz_block", "smooth_stone", "spruce_planks", "warped_planks"
//        };


    }


    //TYPE: ================ Block
    public class compatChairBlock extends ChairBlock {
        public compatChairBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatChairEntity(pos, state);
        }
    }

    public class compatTableBlock extends TableBlock {
        public compatTableBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatTableEntity(pos, state);
        }
    }

    public class compatBenchBlock extends WoodenBenchBlock {
        public compatBenchBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatBenchEntity(pos, state);
        }
    }

    public class compatCouchBlock extends CouchBlock {
        public compatCouchBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatCouchEntity(pos, state);
        }
    }

    public class compatFancyBedBlock extends FancyBedBlock {
        public compatFancyBedBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatFancyBedEntity(pos, state);
        }
    }

    public class compatDiningBenchBlock extends DiningBenchBlock {
        public compatDiningBenchBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatDiningBenchEntity(pos, state);
        }
    }

    public class compatNightstandBlock extends NightstandBlock {
        public compatNightstandBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatNightstandEntity(pos, state);
        }
    }

    public class compatDeskBlock extends DeskBlock {
        public compatDeskBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatDeskEntity(pos, state);
        }
    }

    public class compatSideTableBlock extends SideTableBlock {
        public compatSideTableBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatSideTableEntity(pos, state);
        }
    }

    public class compatCounterBlock extends CounterBlock {
        public compatCounterBlock(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new compatCounterEntity(pos, state);
        }
    }

//TYPE: ================ BlockEntity
    public class compatChairEntity extends ChairBlockEntity {
        public compatChairEntity(BlockPos blockPos, BlockState blockState) {
            super(blockPos, blockState);
        }

        @Override
        public BlockEntityType<?> getType() {
            return CHAIR.getTileHolder().get();
        }
    }

    public class compatTableEntity extends TableBlockEntity {
        public compatTableEntity(BlockPos blockPos, BlockState blockState) {
            super(blockPos, blockState);
        }

        @Override
        public BlockEntityType<?> getType() {
            return TABLE.getTileHolder().get();
        }
    }

    public class compatBenchEntity extends WoodenBenchBlockEntity {
        public compatBenchEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @Override
        public BlockEntityType<?> getType() {
            return BENCH.getTileHolder().get();
        }
    }

    public class compatCouchEntity extends CouchBlockEntity {
        public compatCouchEntity(BlockPos blockPos, BlockState blockState) {
            super(blockPos, blockState);
        }

        @Override
        public BlockEntityType<?> getType() {
            return COUCH.getTileHolder().get();
        }
    }

    public class compatFancyBedEntity extends FancyBedBlockEntity {
        public compatFancyBedEntity(BlockPos blockPos, BlockState blockState) {
            super(blockPos, blockState);
        }

        @Override
        public BlockEntityType<?> getType() {
            return FANCY_BED.getTileHolder().get();
        }
    }

    public class compatDiningBenchEntity extends DiningBenchBlockEntity {
        public compatDiningBenchEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @Override
        public BlockEntityType<?> getType() {
            return DINING_BENCH.getTileHolder().get();
        }
    }

    public class compatNightstandEntity extends NightstandBlockEntity {
        public compatNightstandEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @Override
        public BlockEntityType<?> getType() {
            return NIGHTSTAND.getTileHolder().get();
        }
    }

    public class compatDeskEntity extends DeskBlockEntity {
        public compatDeskEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @Override
        public BlockEntityType<?> getType() {
            return DESK.getTileHolder().get();
        }
    }

    public class compatSideTableEntity extends SideTableBlockEntity {
        public compatSideTableEntity(BlockPos pos, BlockState state) {
            super(pos, state);
        }

        @Override
        public BlockEntityType<?> getType() {
            return SIDE_TABLE.getTileHolder().get();
        }
    }

    public class compatCounterEntity extends ItemHoldingBlockEntity  {
        public compatCounterEntity(BlockPos pos, BlockState state) {
            super(counterTile, pos, state);
            this.setStack(Items.CALCITE.getDefaultInstance());
        }

        @Override
        public BlockEntityType<?> getType() {
            return counterTile;
        }
    }
}
