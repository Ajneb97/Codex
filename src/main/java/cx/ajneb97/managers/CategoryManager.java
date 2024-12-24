package cx.ajneb97.managers;

import cx.ajneb97.Codex;
import cx.ajneb97.model.structure.Category;

import java.util.ArrayList;

public class CategoryManager {

	private Codex plugin;
	private ArrayList<Category> categories;
	public CategoryManager(Codex plugin) {
		this.plugin = plugin;
		this.categories = new ArrayList<>();
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public Category getCategory(String name){
		for(Category c : categories){
			if(c.getName().equals(name)){
				return c;
			}
		}
		return null;
	}

	public int getTotalDiscoveries(String categoryName){
		Category category = getCategory(categoryName);
		if(category == null){
			return 0;
		}
		return category.getDiscoveries().size();
	}

	public int getTotalDiscoveries(){
		int total = 0;
		for(Category category : categories){
			total = total+category.getDiscoveries().size();
		}
		return total;
	}
}
