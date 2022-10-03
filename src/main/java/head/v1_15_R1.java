package head;

import head.HeadLib.InternalsProvider;
import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagList;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
class v1_15_R1 implements InternalsProvider {

    @Override
    public String getTextureValue(ItemStack item) {
        net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);

        NBTTagCompound tag = nmsStack.getTag();
        if (tag == null) {
            return null;
        }

        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        if (skullOwner == null) {
            return null;
        }
        NBTTagCompound properties = skullOwner.getCompound("Properties");
        if (properties == null) {
            return null;
        }
        NBTTagList textures = properties.getList("textures", 10);
        if (textures == null) {
            return null;
        }

        for (NBTBase base : textures) {
            if (base instanceof NBTTagCompound && ((NBTTagCompound) base).hasKeyOfType("Value", 8)) {
                return ((NBTTagCompound) base).getString("Value");
            }
        }
        return null;
    }

    @Override
    public ItemStack setSkullOwner(ItemStack item, Object compound) {
        net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        nmsStack.getOrCreateTag().set("SkullOwner", (NBTBase) compound);
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    @Override
    public NBTTagCompound createOwnerCompound(String id, String textureValue) {
        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.setString("Id", id);
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.setString("Value", textureValue);
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);
        return skullOwner;
    }

}