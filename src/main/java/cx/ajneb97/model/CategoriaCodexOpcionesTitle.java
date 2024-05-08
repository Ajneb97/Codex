package cx.ajneb97.model;

public class CategoriaCodexOpcionesTitle {

	private String title;
	private String subtitle;
	private int fadeIn;
	private int stay;
	private int fadeOut;
	public CategoriaCodexOpcionesTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		super();
		this.title = title;
		this.subtitle = subtitle;
		this.fadeIn = fadeIn;
		this.stay = stay;
		this.fadeOut = fadeOut;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public int getFadeIn() {
		return fadeIn;
	}
	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
	}
	public int getStay() {
		return stay;
	}
	public void setStay(int stay) {
		this.stay = stay;
	}
	public int getFadeOut() {
		return fadeOut;
	}
	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
	}
	
	
}
