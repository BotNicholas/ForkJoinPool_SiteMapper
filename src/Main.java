import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        System.out.print("Enter site's url: ");
        String site = new Scanner(System.in).nextLine();

        String[] linkParts = site.split("/");
        ForkJoinPool forkjoin = new ForkJoinPool();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(linkParts[linkParts.length-1] + ".map.txt"));
            forkjoin.invoke(new SitesMapper(0, site, writer));
            writer.close();
            forkjoin.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " thread is finished!");
    }
}