package gitCrawler;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by erikl on 4/25/2017.
 */
public class GitR {
    public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());

        Process p = pb.start();
        int exit = p.waitFor();

        if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }

    }
}
