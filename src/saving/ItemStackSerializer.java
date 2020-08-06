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

package saving;

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
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.experimental.UtilityClass;

/**
 * From: <a href="https://www.spigotmc.org/threads/how-to-serialize-itemstack-inventory-with-attributestorage.152931/#post-3077661">https://www.spigotmc.org/threads/how-to-serialize-itemstack-inventory-with-attributestorage.152931/#post-3077661</a><br>
 * Optimized by fren_gor
 * 
 * @author MIOR, fren_gor
 */
@UtilityClass
public final class ItemStackSerializer {

	private static final String completeVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
	private static final int version = Integer.valueOf(completeVersion.split("_")[1]);
	@SuppressWarnings("unused")
	private static final int releawse = Integer.valueOf(completeVersion.split("R")[1]);
	private static Class<?> nbtTagCompoundClass, nmsItemStackClass, nbtCompressedStreamToolsClass, craftItemStackClass;
	private static Constructor<?> nbtTagCompoundConstructor, nmsItemStackContructor;
	private static Method aIn, aOut, createStack, asBukkitCopy, asNMSCopy, save, getStorageContents;

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
				case 16: {
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
		if (version > 9) {
			try {
				getStorageContents = Inventory.class.getDeclaredMethod("getStorageContents");
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
		ItemStack[] arr = new ItemStack[data.length];
		for (int i = 0; i < data.length; i++) {
			arr[i] = deserializeItemStack(data[i]);
		}
		return arr;
	}

	public static List<ItemStack> deserializeItemStack(List<String> data) {
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
		String[] arr = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			arr[i] = serializeItemStack(items[i]);
		}
		return arr;
	}

	public static List<String> serializeItemStack(List<ItemStack> items) {
		List<String> l = new ArrayList<>(items.size());
		for (ItemStack s : items) {
			l.add(serializeItemStack(s));
		}
		return l;
	}

	public static String serializeInventory(Inventory inv) {
		Validate.notNull(inv, "Inventory cannot be null");

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				DataOutputStream dataOutput = new DataOutputStream(outputStream);) {

			String[] ser = serializeItemStack(
					version > 9 ? (ItemStack[]) getStorageContents.invoke(inv) : inv.getContents());

			dataOutput.writeByte(ser.length);
			for (String s : ser) {
				dataOutput.writeUTF(s);
			}

			return new BigInteger(1, outputStream.toByteArray()).toString(32);
		} catch (ReflectiveOperationException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ItemStack[] deserializeInventory(String data) {
		Validate.notNull(data, "Data cannot be null");

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
				DataInputStream dataInputStream = new DataInputStream(inputStream);) {

			ItemStack[] arr = new ItemStack[dataInputStream.readByte()];

			for (int i = 0; i < arr.length; i++) {
				arr[i] = deserializeItemStack(dataInputStream.readUTF());
			}

			return arr;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get an object (designed for {@link YamlConfiguration#get(String)}) and try to get an {@link ItemStack} out of it.
	 * 
	 * The object must be an ItemStack or the result of {@link ItemStackSerializer#serializeItemStack(ItemStack)}.
	 * 
	 * @param obj
	 *            Object to be transformed to an {@link ItemStack}
	 * @return An {@link ItemStack} if the deserialization was successful, otherwise null.
	 * @throws DeserializationException
	 *             If the object is not an ItemStack or the result of {@link ItemStackSerializer#serializeItemStack(ItemStack)}.
	 */
	public static ItemStack deserializeObject(Object obj) throws DeserializationException {
		if (obj instanceof ItemStack) {
			return (ItemStack) obj;
		} else if (obj instanceof String) {
			return ItemStackSerializer.deserializeItemStack((String) obj);
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

	private static Class<?> getCBClass(String name) {

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
}
