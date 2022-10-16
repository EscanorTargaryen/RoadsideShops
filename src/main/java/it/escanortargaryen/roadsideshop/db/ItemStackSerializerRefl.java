//  MIT License
//  
//  Copyright (c) 2020 fren_gor
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package it.escanortargaryen.roadsideshop.db;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * From: <a href=
 * "https://www.spigotmc.org/threads/how-to-serialize-itemstack-inventory-with-attributestorage.152931/#post-3077661">https://www.spigotmc.org/threads/how-to-serialize-itemstack-inventory-with-attributestorage.152931/#post-3077661</a><br>
 * Optimized by fren_gor
 *
 * @author MIOR, fren_gor
 */
public final class ItemStackSerializerRefl {

    public static final String completeVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
    public static final int version = Integer.valueOf(completeVersion.split("_")[1]);
    private static Class<?> nbtTagCompoundClass, nmsItemStackClass, nbtCompressedStreamToolsClass, craftItemStackClass;
    private static Constructor<?> nbtTagCompoundConstructor, nmsItemStackContructor;
    private static Method aIn, aOut, createStack, asBukkitCopy, asNMSCopy, save, getTitle;
    private static final byte VERSION = 0x01;

    static {
        nbtTagCompoundClass = getNMSClass("NBTTagCompound");
        nmsItemStackClass = getNMSClass("ItemStack");
        nbtCompressedStreamToolsClass = getNMSClass("NBTCompressedStreamTools");
        craftItemStackClass = getCBClass("inventory.CraftItemStack");
        try {
            nbtTagCompoundConstructor = nbtTagCompoundClass.getDeclaredConstructor();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            aIn = nbtCompressedStreamToolsClass.getMethod("a", DataInputStream.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            aOut = nbtCompressedStreamToolsClass.getMethod("a", nbtTagCompoundClass, DataOutput.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            switch (version) {
                case 8:
                case 9:
                case 10: {
                    createStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass);
                    break;
                }
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                {
                    nmsItemStackContructor = nmsItemStackClass.getDeclaredConstructor(nbtTagCompoundClass);
                    nmsItemStackContructor.setAccessible(true);
                    break;
                }
                default:
                    break;
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            asBukkitCopy = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            asNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            save = nmsItemStackClass.getMethod("save", nbtTagCompoundClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        if (version < 14) {
            try {
                getTitle = Inventory.class.getDeclaredMethod("getTitle");
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    public static ItemStack deserializeItemStack(String data) {
        if (data == null || data.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
             DataInputStream dataInputStream = new DataInputStream(inputStream);) {
            Object nbtTagCompound = aIn.invoke(null, dataInputStream);
            Object craftItemStack = craftNMSItemStack(nbtTagCompound);
            return (ItemStack) asBukkitCopy.invoke(null, craftItemStack);
        } catch (ReflectiveOperationException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ItemStack[] deserializeItemStack(String[] data) {
        Validate.notNull(data, "Data cannot be null");
        ItemStack[] arr = new ItemStack[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = deserializeItemStack(data[i]);
        }
        return arr;
    }

    public static List<ItemStack> deserializeItemStack(List<String> data) {
        Validate.notNull(data, "Data cannot be null");
        List<ItemStack> l = new ArrayList<>(data.size());
        for (String s : data) {
            l.add(deserializeItemStack(s));
        }
        return l;
    }

    public static String serializeItemStack(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "";
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream);) {
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = asNMSCopy.invoke(null, item);
            save.invoke(nmsItemStack, nbtTagCompound);
            aOut.invoke(null, nbtTagCompound, (DataOutput) dataOutput);
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (ReflectiveOperationException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String[] serializeItemStack(ItemStack[] items) {
        Validate.notNull(items, "Items cannot be null");
        String[] arr = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            arr[i] = serializeItemStack(items[i]);
        }
        return arr;
    }

    public static List<String> serializeItemStack(List<ItemStack> items) {
        Validate.notNull(items, "Items cannot be null");
        List<String> l = new ArrayList<>(items.size());
        for (ItemStack s : items) {
            l.add(serializeItemStack(s));
        }
        return l;
    }

    public static String serializeInventory(Inventory inv) {
        Validate.notNull(inv, "Inventory cannot be null");
        Validate.isTrue(inv.getType() == InventoryType.CHEST,
                "Illegal inventory type " + inv.getType().toString() + "(expected CHEST).");

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream);) {
            dataOutput.writeByte(VERSION);

            dataOutput.writeByte(inv.getSize());
            if (version < 14) {
                dataOutput.writeBoolean(true);
                dataOutput.writeUTF((String) getTitle.invoke(inv));
            } else {
                dataOutput.writeBoolean(false);
            }

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack it = inv.getItem(i);
                if (it == null || it.getType() == Material.AIR)
                    continue;
                dataOutput.writeByte(i);
                dataOutput.writeUTF(serializeItemStack(it));
            }

            dataOutput.writeByte(-1);

            /*
             * Version - 1 byte Size - 1 byte NextIsPresent - Boolean Title - String
             * (Present only if the previous is true) Array: SlotIndex - 1 byte ItemStack -
             * String -1 (Array End) - 1 byte
             */
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (ReflectiveOperationException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InventoryMap deserializeInventory(String data) {
        Validate.notNull(data, "Data cannot be null");
        Validate.isTrue(!data.isEmpty(), "Data cannot be empty");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
             DataInputStream dataInputStream = new DataInputStream(inputStream);) {

            int version = dataInputStream.readByte();

            if (version != VERSION)
                throw new DeserializationException("Invalid inventory version \"" + version
                        + "\". The only supported version is the  \"" + VERSION + "\".");

            int size = dataInputStream.readByte();
            boolean present = dataInputStream.readBoolean();
            String title = present ? dataInputStream.readUTF() : null;

            InventoryMap map = new InventoryMap(size, size, title);

            while (true) {
                int slot = dataInputStream.readByte();
                if (slot == -1 || slot >= size)
                    break;
                ItemStack it = deserializeItemStack(dataInputStream.readUTF());

                map.put(slot, it);
            }

            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an object (designed for {@link YamlConfiguration#get(String)}) and try to
     * get an {@link ItemStack} out of it.
     * <p>
     * The object must be an ItemStack or the result of
     * {@link ItemStackSerializerRefl#serializeItemStack(ItemStack)}.
     *
     * @param obj Object to be transformed to an {@link ItemStack}
     * @return An {@link ItemStack} if the deserialization was successful, otherwise
     * null.
     * @throws DeserializationException If the object is not an ItemStack or the
     *                                  result of
     *                                  {@link ItemStackSerializerRefl#serializeItemStack(ItemStack)}.
     */
    public static ItemStack deserializeObject(Object obj) throws DeserializationException {
        if (obj instanceof ItemStack) {
            return (ItemStack) obj;
        } else if (obj instanceof String) {
            return ItemStackSerializerRefl.deserializeItemStack((String) obj);
        } else
            throw new DeserializationException("Couldn't deserialize object");
    }

    private static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + completeVersion + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Class<?> getCBClass(String name) {

        try {
            return Class.forName("org.bukkit.craftbukkit." + completeVersion + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private static Object craftNMSItemStack(Object nbtTagCompound) throws ReflectiveOperationException {
        switch (version) {
            case 8:
            case 9:
            case 10: {
                return createStack.invoke(null, nbtTagCompound);
            }
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16: {
                return nmsItemStackContructor.newInstance(nbtTagCompound);
            }
            default:
                return null;
        }
    }

    public static final class DeserializationException extends RuntimeException {

        private static final long serialVersionUID = 5732914764163243723L;

        public DeserializationException() {
        }

        public DeserializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public DeserializationException(String message) {
            super(message);
        }

        public DeserializationException(Throwable cause) {
            super(cause);
        }

    }

    public static final class InventoryMap extends HashMap<Integer, ItemStack> {

        private static final long serialVersionUID = -2375620583757374598L;

        private final int inventorySize;

        private String title;

        public void setTitle(String title) {
            Validate.isTrue(version > 13, "Title cannot be changed in 1.8-1.13");
            this.title = title;
        }

        InventoryMap() {
            throw new UnsupportedOperationException("Illegal use of constructor");
        }

        InventoryMap(int inventorySize, String title) {
            super();
            this.inventorySize = inventorySize;
            this.title = title;
        }

        InventoryMap(int initialCapacity, float loadFactor, int inventorySize, String title) {
            super(initialCapacity, loadFactor);
            this.inventorySize = inventorySize;
            this.title = title;
        }

        InventoryMap(int initialCapacity, int inventorySize, String title) {
            super(initialCapacity);
            this.inventorySize = inventorySize;
            this.title = title;
        }

        InventoryMap(InventoryMap m, String title) {
            super(m);
            inventorySize = m.getInventorySize();
            this.title = title;
        }

        public int getInventorySize() {
            return inventorySize;
        }

        public String getTitle() {
            return title;
        }

        public Inventory toInventory(InventoryHolder owner) {
            Inventory inv;
            if (title != null)
                inv = Bukkit.createInventory(owner, inventorySize, title);
            else
                inv = Bukkit.createInventory(owner, inventorySize);
            for (Entry<Integer, ItemStack> e : entrySet()) {
                inv.setItem(e.getKey(), e.getValue() == null ? null : e.getValue().clone());
            }
            return inv;
        }

        public ItemStack[] getContents() {
            ItemStack[] arr = new ItemStack[size()];
            int i = 0;
            for (ItemStack it : values()) {
                arr[i++] = it.clone();
            }
            return arr;
        }

        public ItemStack[] getStorageContents() {
            ItemStack[] arr = new ItemStack[inventorySize];
            for (int i = 0; i < inventorySize; i++) {
                ItemStack it = get(i);
                if (it == null) {
                    arr[i] = new ItemStack(Material.AIR);
                } else {
                    arr[i] = it.clone();
                }
            }
            return arr;
        }

    }

}
