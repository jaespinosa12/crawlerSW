package crawlDBLP;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Crawler {

	private HashSet<String> links;
    private List<List<String>> articles;

    public Crawler() {
        links = new HashSet<>();
        articles = new ArrayList<>();
    }

    //Find all URLs that start with "https://dblp.org/db/journals/" and add them to the HashSet
    public void getPageLinks(String URL) {
        if (!links.contains(URL)) {
            try {
                Document document = Jsoup.connect(URL).get();
                Elements otherLinks = document.select("a[href^=\"https://dblp.org/db/journals/\"]");

                for (Element page : otherLinks) {
                    if (links.add(URL)) {
                        //Remove the comment from the line below if you want to see it running on your editor
                        System.out.println(URL);
                    }
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    //Connect to each link saved in the article and find all the articles in the page
    public void getArticles() {
        links.forEach(x -> {
            Document document;
            try {
                document = Jsoup.connect(x).get();
                Elements articleLinks = document.select("h2 a[href^=\"https://dblp.org/db/\"]");
                for (Element article : articleLinks) {
                    //Only retrieve the titles of the articles that contain Java 8
                	ArrayList<String> temporary = new ArrayList<>();
                    temporary.add(article.text()); //The title of the article
                    temporary.add(article.attr("abs:href")); //The URL of the article
                    articles.add(temporary);
                    //if (article.text().matches("^.*?(Java 8|java 8|JAVA 8).*$")) {
                        //Remove the comment from the line below if you want to see it running on your editor, 
                        //or wait for the File at the end of the execution
                        //System.out.println(article.attr("abs:href"));

                       // ArrayList<String> temporary = new ArrayList<>();
                        //temporary.add(article.text()); //The title of the article
                        //temporary.add(article.attr("abs:href")); //The URL of the article
                        //articles.add(temporary);
                   // }
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void writeToFile() {
    	try (PrintWriter writer = new PrintWriter(new File("./test.csv"))) {

    	      StringBuilder sb = new StringBuilder();
    	      sb.append("Nombre,");
    	      sb.append(',');
    	      sb.append("Link");
    	      sb.append('\n');
    	      
    	      articles.forEach(a -> {
                  sb.append(a.get(0));
				  sb.append(',');
				  sb.append(a.get(1));
				  sb.append('\n');
              });
    	      writer.write(sb.toString());
    	      System.out.println("done!");
    	      writer.close();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public static void main(String[] args) {
        Crawler bwc = new Crawler();
        bwc.getPageLinks("https://dblp.org");
        bwc.getArticles();
        bwc.writeToFile();        
    }
	
	    
}
