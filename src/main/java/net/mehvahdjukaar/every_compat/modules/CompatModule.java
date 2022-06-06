package net.mehvahdjukaar.every_compat.modules;

import net.mehvahdjukaar.every_compat.WoodGood;
import net.mehvahdjukaar.every_compat.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.every_compat.dynamicpack.ServerDynamicResourcesHandler;
import net.mehvahdjukaar.selene.block_set.BlockType;
import net.mehvahdjukaar.selene.block_set.leaves.LeavesType;
import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.mehvahdjukaar.selene.block_set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.selene.client.asset_generators.LangBuilder;
import net.mehvahdjukaar.selene.client.asset_generators.textures.Respriter;
import net.mehvahdjukaar.selene.client.asset_generators.textures.TextureImage;
import net.mehvahdjukaar.selene.resourcepack.AfterLanguageLoadEvent;
import net.mehvahdjukaar.selene.resourcepack.RPAwareDynamicTextureProvider;
import net.mehvahdjukaar.selene.resourcepack.RPUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.function.Supplier;


public abstract class CompatModule {

    protected final String modId;

    public CompatModule(String modId) {
        this.modId = modId;
    }

    public String getModId() {
        return modId;
    }

    public abstract String shortenedId();

    @Override
    public String toString() {
        return "EveryCompat " + LangBuilder.getReadableName(modId) + " Module";
    }

    public ResourceLocation modRes(String string) {
        return new ResourceLocation(modId, string);
    }

    public String makeBlockId(BlockType type, String blockName) {
        return this.shortenedId() + "/" + type.getVariantId(blockName, false);
    }

    public void onModSetup() {

    }

    public void onClientSetup() {

    }

    public void registerWoodBlocks(IForgeRegistry<Block> registry, Collection<WoodType> woodTypes) {

    }

    public void registerLeavesBlocks(IForgeRegistry<Block> registry, Collection<LeavesType> leavesTypes) {

    }

    public void registerItems(IForgeRegistry<Item> registry) {

    }

    public void registerTiles(IForgeRegistry<BlockEntityType<?>> registry) {

    }

    public void registerEntities(IForgeRegistry<EntityType<?>> registry) {

    }

    public void registerTileRenderers(IForgeRegistry<EntityType<?>> registry) {

    }

    public final boolean isEntryAlreadyRegistered(String name, IForgeRegistry<?> registry) {
        name = name.replace(this.shortenedId() + "/", ""); //af/quark/blossom_chair
        if (name.startsWith(modId + "/")) return true;        //discards one from this mod
        String name2 = name.replace("/", "_"); //quark_blossom_chair
        String name3 = name.substring(name.lastIndexOf("/") + 1); //blossom_chair
        if (registry.containsKey(new ResourceLocation(modId, name)) || //ones from the mod they are from. usually include vanilla types
                registry.containsKey(new ResourceLocation(modId, name2))) return true;
        if (this.shortenedId().equals("af")) return false; //hardcoding
        if (this.shortenedId().equals("vs")) return false; //we always register everything for these

        String woodFrom = name.substring(0,name.indexOf("/"));

        if (registry.containsKey(new ResourceLocation(woodFrom, name3))) return true;

        for (var c : WoodGood.COMPETITOR_MODS) {
            String compatModId = c.modId();
            for (var s : c.supportedMods()) {
                if (s.equals(woodFrom) && registry.containsKey(new ResourceLocation(compatModId, name3))) return true;
            }
            if (registry.containsKey(new ResourceLocation(compatModId, name)) ||
                    registry.containsKey(new ResourceLocation(compatModId, name2))) return true;
        }
        return false;
    }

    //resource pack stuff

    public void addStaticServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {

    }


    public void addDynamicServerResources(ServerDynamicResourcesHandler handler, ResourceManager manager) {

    }

    public void addStaticClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {

    }

    public void addDynamicClientResources(ClientDynamicResourcesHandler handler, ResourceManager manager) {

    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }

    public void addTranslations(ClientDynamicResourcesHandler clientDynamicResourcesHandler, AfterLanguageLoadEvent lang) {
    }

    public void registerColors(ColorHandlerEvent.Item event) {
    }

    @OnlyIn(Dist.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event) {
    }


    protected final Block getOwnBlock(String id){
        return ForgeRegistries.BLOCKS.getValue(modRes(id));
    }
    //utility functions

    protected final void addChildToOak(String category, String oakBlockName) {
        WoodType.OAK_WOOD_TYPE.addChild(category, ForgeRegistries.BLOCKS.getValue(modRes(oakBlockName)));
    }


    //post process some textures. currently only ecologics azalea
    public void addWoodTexture(WoodType wood, RPAwareDynamicTextureProvider handler, ResourceManager manager,
                               String path, Supplier<TextureImage> textureSupplier) {
        handler.addTextureIfNotPresent(manager, path, () -> {
            var t = textureSupplier.get();
            maybeFlowerAzalea(t, manager, wood);
            return t;
        });
    }

    //for ecologics
    protected void maybeFlowerAzalea(TextureImage image, ResourceManager manager, WoodType woodType) {
        if (woodType.getId().toString().equals("ecologics:flowering_azalea")) {
            WoodType azalea = WoodTypeRegistry.WOOD_TYPES.get(new ResourceLocation("ecologics:azalea"));
            if (azalea != null) {
                try (TextureImage mask = TextureImage.open(manager,
                        WoodGood.res("block/ecologics_overlay"));
                     TextureImage plankTexture = TextureImage.open(manager,
                             RPUtils.findFirstBlockTextureLocation(manager, azalea.planks))) {

                    Respriter respriter = Respriter.of(image);
                    var temp = respriter.recolorWithAnimationOf(plankTexture);

                    image.applyOverlay(temp, mask);
                    temp.close();

                } catch (Exception e) {
                    WoodGood.LOGGER.warn("failed to apply azalea overlay: ", e);
                }
            }
        }
    }



}
