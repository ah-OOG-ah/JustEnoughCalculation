package me.towdium.jecalculation.data.label.labels;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.polyfill.mc.client.renderer.GlStateManager;
import me.towdium.jecalculation.polyfill.mc.util.NonNullList;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: towdium
 * Date:   17-9-10.
 */
@ParametersAreNonnullByDefault
public class LOreDict extends ILabel.Impl {
    public static final String IDENTIFIER = "oreDict";
    public static final String KEY_NAME = "name";
    public static final String KEY_AMOUNT = "amount";
    public static final boolean MODE_FORCE = true;

    protected String name;

    public LOreDict(String name) {
        this(name, 1);
    }

    public LOreDict(String name, int amount) {
        super(amount);
        this.name = name;
    }

    public LOreDict(LOreDict lod) {
        super(lod);
        this.name = lod.name;
    }

    public LOreDict(NBTTagCompound nbt) {
        super(nbt);
        name = nbt.getString(KEY_NAME);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getDisplayName() {
        return Utilities.I18n.format("label.ore_dict.name", name);
    }

    public static Optional<ILabel> mergeOreDict(ILabel a, ILabel b, boolean add) {
        if (a instanceof LOreDict && b instanceof LOreDict) {
            LOreDict lodA = (LOreDict) a;
            LOreDict lodB = (LOreDict) b;
            if (lodA.getName().equals(lodB.getName()))
                return Optional.of(new LOreDict(lodA.getName(),
                                                add ? lodA.getAmount() + lodB.getAmount() : lodA.getAmount() - lodB.getAmount()));
        }
        return Optional.empty();
    }

    public static List<ILabel> guess(List<ILabel> iss) {
        ILabel l = iss.get(0);
        HashSet<Integer> ids = new HashSet<>();
        int amount = l.getAmount();
        for (int i : OreDictionary.getOreIDs(((LItemStack) l).getRep()))
            if (check(i, iss)) ids.add(i);
        return ids.stream().map(i -> new LOreDict(OreDictionary.getOreName(i), amount))
                  .collect(Collectors.toList());
    }

    // check labels in the list suitable for the ore id
    private static boolean check(int id, List<ILabel> labels) {
        ArrayList<ItemStack> ores = OreDictionary.getOres(OreDictionary.getOreName(id));
        if (labels.size() == 1 && ores.size() == 1
            && (ores.get(0).getItemDamage() == OreDictionary.WILDCARD_VALUE || !MODE_FORCE))
            return false;

        boolean acceptable = true;
        for (ItemStack ore : ores) {
            boolean found = false;
            for (ILabel label : labels) {
                if (label instanceof LItemStack && OreDictionary.itemMatches(
                        ore, ((LItemStack) label).getRep(), false)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                acceptable = false;
                break;
            }
        }
        for (ILabel label : labels) {
            boolean found = false;
            for (ItemStack ore : ores) {
                if (label instanceof LItemStack && OreDictionary.itemMatches(
                        ore, ((LItemStack) label).getRep(), false)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                acceptable = false;
                break;
            }
        }
        return acceptable;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean matches(Object l) {
        return l instanceof LOreDict && name.equals(((LOreDict) l).name);
    }

    @Override
    public LOreDict copy() {
        return new LOreDict(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawLabel(JecaGui gui) {
        NonNullList<ItemStack> list = NonNullList.create();
        OreDictionary.getOres(name).forEach(is -> {
            if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                is.getItem().getSubItems(is.getItem(), CreativeTabs.tabAllSearch, list);
            } else list.add(is);
        });
        if(list.isEmpty()) return;
        long index = System.currentTimeMillis() / 1500;
        gui.drawResource(Resource.LBL_ORE_DICT, 0, 0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(1, 1, 0);
        GlStateManager.scale(14f / 16, 14f / 16, 1);
        gui.drawItemStack(0, 0, list.get((int) (index % list.size())), false);
        GlStateManager.popMatrix();
    }

    @Override
    public NBTTagCompound toNBTTagCompound() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setString(KEY_NAME, name);
        ret.setInteger(KEY_AMOUNT, amount);
        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<String> getToolTip(List<String> existing, boolean detailed) {
        super.getToolTip(existing, detailed);
        existing.add(FORMAT_BLUE + FORMAT_ITALIC + JustEnoughCalculation.Reference.MODNAME);
        return existing;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ amount;
    }

    public String getName() {
        return name;
    }
}
