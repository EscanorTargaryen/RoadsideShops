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

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class Util {

    private static MessageDigest SHA_1;

    static {
        try {
            SHA_1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha1(String input) {
        byte[] messageDigest = SHA_1.digest(input.getBytes());

        BigInteger no = new BigInteger(1, messageDigest);

        StringBuilder hashtext = new StringBuilder(no.toString(16));

        while (hashtext.length() < 32) {
            hashtext.insert(0, '0');
        }
        return hashtext.toString();
    }

    /**
     * @param money Amount to format
     * @return The formetted string
     * @formatter:off 1
     * 10
     * 50
     * 100
     * 1000
     * 10k
     * 50k
     * 100k
     * 200k
     * 500k
     * 1M
     * 100.5M
     * 100'000M
     * @formatter:on
     */
    public static String format(double money) {

        int i = (int) money;

        if (i < 10000) {
            return formatDecimal(money, 100);
        }

        if (i < 1000000) {
            return formatDecimal(money / 1000.0, 10) + "k";
        }

        String s = formatDecimal(money / 1000000.0, 10);
        // 1200200.5
        // 1.2M
        // 1200200200200.5
        // 1'200'200.2M

        int index = s.indexOf('.');
        String pre = s.substring(0, index);

        StringBuilder f = new StringBuilder(pre);

        for (int n = pre.length() % 3; n < f.length(); n += 4) {

            f.insert(n, '\'');

        }

		/*char[] arr = pre.toCharArray();
		int count = 0;
		for (int n = arr.length - 1; n >= 0; n--) {
			char c = arr[n];
			f.insert(0, c);
			if (count++ % 3 == 0 && count != 1) {
				f.insert(1, '\'');
			}
		}*/

        f.append(s.substring(index));

        return f.toString() + "M";

    }

    private static String formatDecimal(double d, int factor) {
        d = (Math.round(d * factor) / (double) factor);
        int i = (int) d;
        if (d - i == 0)
            return String.valueOf(i);
        else
            return String.valueOf(d);
    }

    public static List<String> filterTabCompleteOptions(Collection<String> options, String... args) {
        String lastArg = "";
        if (args.length > 0) {
            lastArg = args[(args.length - 1)].toLowerCase();
        }
        List<String> Options = new ArrayList<>(options);
        for (int i = 0; i < Options.size(); i++) {
            if (!Options.get(i).toLowerCase().startsWith(lastArg)) {
                Options.remove(i--);
            }
        }
        return Options;
    }

    public static String capitalize(String s) {
        StringBuilder b = new StringBuilder(s.toLowerCase());
        b.setCharAt(0, Character.toUpperCase(b.charAt(0)));
        for (int i = 0; i < s.length(); i++) {
            if (b.charAt(i) == ' ') {
                i++;
                if (i < s.length())
                    b.setCharAt(i, Character.toUpperCase(b.charAt(i)));
            }
        }
        return b.toString();
    }

    public static String[] splitBySpaces(String source, String prefix, int parts) {

        Validate.notNull(source, "Source string mustn't be null");
        Validate.isTrue(parts > 1, "Parts must be > 2");

        source = source.trim();
        while (source.contains("  ")) {
            source = source.replace("  ", " ");
        }
        char[] arr = source.toCharArray();
        List<Integer> l = new ArrayList<>(arr.length);

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ' ') {
                l.add(i);
            }
        }

        if (l.size() == 0) {
            return new String[]{prefix + source};
        }
        if (l.size() < parts) {
            String[] s = new String[l.size() + 1];
            int old = l.get(0);
            s[0] = prefix + source.substring(0, old);
            for (int i = 1; i < s.length - 1; i++) {
                int o = l.get(i);
                s[i] = prefix + source.substring(old + 1, o);
                old = o;
            }
            s[l.size()] = prefix + source.substring(old + 1);
            return s;
        }

        String[] s = new String[parts];
        int index = arr.length / parts;
        int ind = index;
        int old = getNearest(l, ind);
        s[0] = prefix + source.substring(0, old);
        ind += index;

        for (int i = 1; i < parts - 1; i++) {

            int in = getNearest(l, ind);
            ind += index;
            s[i] = prefix + source.substring(old + 1, in);
            old = in;

        }

        s[parts - 1] = prefix + source.substring(old + 1);

        return s;

    }

    private static int getNearest(List<Integer> l, int index) {

        int previuos = Integer.MAX_VALUE;
        int blank = 0;

        for (int i : l) {
            int abs = Math.abs(index - i);
            if (abs < previuos) {
                previuos = abs;
                blank = i;
            } else
                break;
        }
        return blank;
    }

    /**
     * By Jonas Klemming https://stackoverflow.com/a/237204
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static boolean isDouble(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }

        int dots = 0;
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c == '.') {
                dots++;
                if (dots > 1)
                    return false;
                continue;
            }
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    @Deprecated
    public static <K, V> void saveMapToYaml(YamlConfiguration yaml, String path, Map<K, V> map,
                                            Function<K, String> getKey) {

        yaml.set(path, null);
        yaml.createSection(path);
        ConfigurationSection sect = yaml.getConfigurationSection(path);
        for (Entry<K, V> e : map.entrySet()) {
            sect.set(getKey.apply(e.getKey()), e.getValue());
        }

    }

    @Deprecated
    public static <K, V> Map<K, V> readMapFromYaml(YamlConfiguration yaml, String path,
                                                   Function<String, K> transformKey) {

        Map<K, V> map = new HashMap<>();
        ConfigurationSection sect = yaml.getConfigurationSection(path);
        for (String k : sect.getKeys(false)) {
            @SuppressWarnings("unchecked")
            V v = (V) sect.get(k);
            // Optional<K> opt = transformKey.apply(k);
            // if (opt.isPresent()) {
            // map.put(opt.get(), v);
            // }
            map.put(transformKey.apply(k), v);
        }
        return map;

    }

    /**
     * Return a random integer in a certain range
     *
     * @param min The min number of the range
     * @param max The max number of the range
     * @return A number between min and max (included)
     */
    public static int nextInt(int min, int max) {
        if (min == max) {
            return max;
        }

        return ThreadLocalRandom.current().nextInt(max - min + 1) + min;
    }

}
