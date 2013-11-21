package home.standalone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Main {
    public static void main(String[] args) throws Exception {
        new Main().task1();
    }

    /*
     * screenshots captured from raz kids are renamed and cropped
     */
    public void task3() throws IOException {
        File[] pngFiles = new File("/dev/shm/S - Barack Obama").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {  // name: just the name, no path info
                return name.startsWith("Screenshot") && name.endsWith(".png");
            }
        });

        Set<File> pngFileSet = new TreeSet<>(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        pngFileSet.addAll(Arrays.asList(pngFiles));

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (File file : pngFileSet) {
            Path source = Paths.get(file.getAbsolutePath());
            Path destination = Paths.get(String.format(file.getParent() + File.separator + "h%02d.png", i));
            Files.move(source, destination);

            sb.append(String.format((i == 0 ? "" : "; ") + "convert -crop 620x850+90+140 h%02d.png v%02d.png", i, i));
            i++;
        }
        System.out.println(sb);
    }

    /*
     * rename a bunch of files in a directory
     */
    public void task2() throws Exception {
        File[] flacFiles = new File("/dev/shm/Michael Buble - Christmas").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".flac");
            }
        });

        String whatToRemove = " - Michael Buble";
        for (File flacFile : flacFiles) {
            String absolutePath = flacFile.getAbsolutePath();
            Path source = Paths.get(absolutePath);
            int lastSeparator = absolutePath.lastIndexOf(File.separator);
            int lastKeyword = absolutePath.lastIndexOf(whatToRemove);
            if (lastKeyword < 0 || lastKeyword < lastSeparator) {
                continue;
            }
            Path destination = Paths.get(absolutePath.replaceAll(whatToRemove, ""));
            Files.move(source, destination);
        }
    }

    /*
     * use the picture file names in /home/ly/wallpapers to make the adwaita-timed.xml
     */
    public void task1() throws Exception {
        String template = "<static>\n  <duration>%d</duration>\n  <file>%s</file>\n</static>\n";
/*
        String template = "<static>\n  <duration>%d</duration>\n  <file>%s</file>\n</static>\n<transition>\n  " +
                "<duration>5.0</duration>\n  <from>%s</from>\n  <to>%s</to>\n</transition>\n";
*/
        File directory = new File("/home/ly/wallpapers");
        File[] pictureFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".png");
            }
        });

        int duration = 24 * 3600 / pictureFiles.length;
        final Random random = new Random();

        class OO {
            File[] m(File[] a) {
                if (a == null || a.length == 0) {
                    return null;
                }

                int i = random.nextInt(a.length);
                File temp = a[a.length - 1];
                a[a.length - 1] = a[i];
                a[i] = temp;

                File[] result = new File[a.length - 1];
                System.arraycopy(a, 0, result, 0, a.length - 1);
                return result;
            }
        }

        OO oo = new OO();
        File[] permutedPictureFiles = new File[pictureFiles.length];
        File[] temp = pictureFiles;
        for (int i = 0; i < pictureFiles.length; i++) {
            File[] t2 = oo.m(temp);
            permutedPictureFiles[i] = temp[temp.length - 1];
            temp = t2;
        }

        File previousFile = null;
        for (File f : permutedPictureFiles) {
            if (previousFile != null) {
                System.out.printf(template, duration, previousFile.getAbsolutePath());
            }
            previousFile = f;
        }
    }
}


