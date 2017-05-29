package gitCrawler;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by erikl on 4/25/2017.
 */
public class GitR {
    public static void runCommand(Path directory, String... command) throws IOException, InterruptedException, AssertionError {
        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());

        Process p = pb.start();
        int exit = p.waitFor();

        if (exit != 0) {
            inheritIO(p.getInputStream(), System.out);
            inheritIO(p.getErrorStream(), System.err);
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }

    }

    private static void inheritIO(final InputStream src, final PrintStream dest) {
        new Thread(() -> {
            Scanner sc = new Scanner(src);
            while (sc.hasNextLine()) {
                dest.println(sc.nextLine());
            }
        }).start();
    }
}
