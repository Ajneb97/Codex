package cx.ajneb97.model.item;

public class CommonItemTrimData {

	private String pattern;
	private String material;

	public CommonItemTrimData(String pattern, String material) {
		this.pattern = pattern;
		this.material = material;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public CommonItemTrimData clone(){
		return new CommonItemTrimData(pattern,material);
	}
}
