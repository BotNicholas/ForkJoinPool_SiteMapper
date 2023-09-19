import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SitesMapper extends RecursiveTask<String> {
    private int level;
    private String site;

    private BufferedWriter writer;

    public SitesMapper(int level, String site, BufferedWriter writer) {
        this.level = level;
        this.site = site;
        this.writer = writer;
    }

    @Override
    protected String compute() {
        try{
            Document document = Jsoup.connect(site).maxBodySize(0).get();
            Elements links = document.select("a");

            Pattern pattern = Pattern.compile("/{3,4}");

            List<Element> sortedLinks = links.stream().filter((e)->{
                return e.attr("abs:href").contains(site) && !e.attr("abs:href").equals(site);
            }).collect(Collectors.toList());

            if(sortedLinks.size()>0){
                sortedLinks.forEach((l)->{
                    System.out.println("\t".repeat(level) + l.attr("abs:href"));
                    try {
                        writer.write("\t".repeat(level) + l.attr("abs:href")+"\n");
                        writer.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    SitesMapper mapper = new SitesMapper(level+1, l.attr("abs:href"), writer);
                    mapper.fork();

                    try {
                        Thread.currentThread().sleep(150);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    mapper.join();
                });
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}