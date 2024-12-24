package cx.ajneb97.model.item;

import java.util.ArrayList;
import java.util.List;

public class CommonItemBookData {

    private List<String> pages;
    private String author;
    private String generation;
    private String title;
    public CommonItemBookData(List<String> pages, String author, String generation, String title) {
        super();
        this.pages = pages;
        this.author = author;
        this.generation = generation;
        this.title = title;
    }
    public List<String> getPages() {
        return pages;
    }
    public void setPages(List<String> pages) {
        this.pages = pages;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getGeneration() {
        return generation;
    }
    public void setGeneration(String generation) {
        this.generation = generation;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public CommonItemBookData clone(){
        return new CommonItemBookData(new ArrayList<>(pages),author,generation,title);
    }
}
