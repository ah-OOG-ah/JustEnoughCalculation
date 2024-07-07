package me.towdium.jecalculation.nei;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.Tags;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.nei.adapter.IAdapter;

public class NEICalculatorConfig implements IConfigureNEI {

    @Override
    public void loadConfig() {
        Adapter.init();
        /*
         * null For the recipeHandlers which extend `TemplateRecipeHandler` without override the `getOverlayIdentifier`
         * function
         */
        Set<String> baseOverlayIdentifiers = new HashSet<>(
            Arrays.asList("crafting", "crafting2x2", "smelting", "fuel", "brewing", null));
        Set<String> adapterIdentifiers = Adapter.adapters.stream()
            .map(IAdapter::getAllOverlayIdentifier)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

        Stream.concat(baseOverlayIdentifiers.stream(), adapterIdentifiers.stream())
            .forEach(ident -> {
                API.registerGuiOverlay(JecaGui.class, ident);
                API.registerGuiOverlayHandler(JecaGui.class, new JecaOverlayHandler(), ident);
            });
    }

    @Override
    public String getName() {
        return JustEnoughCalculation.MODNAME;
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
