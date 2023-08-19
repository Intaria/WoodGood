package net.mehvahdjukaar.every_compat;

import net.mehvahdjukaar.every_compat.api.CompatModule;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;


public class EveryCompatClient {

    public static void commonInit() {
        EveryCompat.forAllModules(CompatModule::onClientInit);
        ClientHelper.addBlockEntityRenderersRegistration(EveryCompatClient::registerBlockEntityRenderers);
        ClientHelper.addBlockColorsRegistration(EveryCompatClient::registerBlockColors);
        ClientHelper.addItemColorsRegistration(EveryCompatClient::registerItemColors);
    }

    private static void registerBlockColors(ClientHelper.BlockColorEvent event) {
        EveryCompat.forAllModules(m -> m.registerBlockColors(event));
    }

    private static void registerItemColors(ClientHelper.ItemColorEvent event) {
        EveryCompat.forAllModules(m -> m.registerItemColors(event));
    }

    private static void registerBlockEntityRenderers(ClientHelper.BlockEntityRendererEvent event) {
        EveryCompat.forAllModules(m -> m.registerBlockEntityRenderers(event));
    }

    private static void registerItemsToTabs(RegHelper.ItemToTabEvent event) {
        EveryCompat.forAllModules(m -> m.registerItemsToTabs(event));
    }

    public static void clientSetup() {
        EveryCompat.forAllModules(CompatModule::onClientSetup);
    }

}
