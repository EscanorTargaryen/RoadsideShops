// MIT License
//
// Copyright (c) 2020 fren_gor
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package saving;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

public class SavingUtil<S extends ConfigurationSerializable> {

	private final JavaPlugin instance;
	private final Function<S, String> getFileName;
	private File directory;

	public SavingUtil(JavaPlugin instance, Function<S, String> getFileName) {
		this.instance = instance;
		this.getFileName = getFileName;
		this.directory = instance.getDataFolder();
		if (!directory.exists())
			directory.mkdirs();
	}

	public SavingUtil(JavaPlugin instance, Function<S, String> getFileName, String subDirectory) {
		this.instance = instance;
		this.getFileName = getFileName;
		this.directory = new File(instance.getDataFolder(), subDirectory);
		if (!directory.exists())
			directory.mkdirs();
	}

	public void save(S s) {

		Validate.notNull(s);
		String name = Util.sha1(getFileName.apply(s));
		File t = new File(directory, name + ".tmp");
		File f = new File(directory, name + ".dat");

		YamlConfiguration yaml = new YamlConfiguration();

		yaml.set("obj", s);

		try {
			yaml.save(t);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			Files.move(t, f);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Trying to save direcly to " + f.getName());
			try {
				yaml.save(f);
			} catch (IOException e1) {
				System.out.println("Couldn't save direcly to " + f.getName());
				e1.printStackTrace();
				return;
			}
		}

		if (t.exists()) {
			t.delete();
		}
	}

	@SuppressWarnings("unchecked")
	public @Nullable S load(String fileName) {
		Validate.notNull(fileName);

		File f = new File(directory, Util.sha1(fileName) + ".dat");

		if (!f.exists()) {
			return null;
		}

		YamlConfiguration yaml = new YamlConfiguration();

		try {
			yaml.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}

		return (S) yaml.get("obj");

	}

	public void remove(S s) {
		Validate.notNull(s);
		File f = new File(directory, Util.sha1(getFileName.apply(s)) + ".dat");

		if (f.exists()) {
			f.delete();
		}
	}

	@SuppressWarnings("unchecked")
	public List<S> loadAll() {
		YamlConfiguration yaml = new YamlConfiguration();
		List<S> list = new LinkedList<>();
		Logger log = instance.getLogger();
		for (File f : directory.listFiles()) {
			if (f.getName().endsWith(".dat")) {

				try {
					yaml.load(f);
				} catch (Exception e) {
					File f1 = new File(f.getPath() + ".corrupted");
					log.severe("Couldn't load file " + f.getName() + ". Renaming it " + f1.getName());
					e.printStackTrace();
					try {
						Files.move(f, f1);
					} catch (Exception e1) {
						log.severe("Couldn't rename " + f.getName() + " to " + f1.getName());
						e1.printStackTrace();
						continue;
					}
					f.delete();
					continue;
				}

				list.add((S) yaml.get("obj"));
			}
		}
		return list;
	}

}
