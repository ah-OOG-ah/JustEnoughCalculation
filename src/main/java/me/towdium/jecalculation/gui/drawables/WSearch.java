package me.towdium.jecalculation.gui.drawables;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.data.label.ILabel;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: towdium
 * Date:   17-9-28.
 */
@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class WSearch extends WContainer {
    Consumer<ILabel> clbk;

    public WSearch(Consumer<ILabel> callback, WTextField tf, WLabelScroll... lss) {
        clbk = callback;
        tf.setLsnrText(s -> {
            boolean b = false;
            for (WLabelScroll i : lss) if (i.setFilter(s)) b = true;
            tf.setColor(b ? JecaGui.COLOR_TEXT_WHITE : JecaGui.COLOR_TEXT_RED);
        });
        add(tf);
        addAll(lss);
        for (WLabelScroll i : lss)
            i.setLsnrUpdate(j -> {
                if (clbk != null) clbk.accept(i.getLabelAt(j));
            });
    }
}
