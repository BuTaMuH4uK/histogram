class Node {
	private String key;
	private String weight;

	public Node (String key, String weight) {
		this.key = key;
		this.weight = weight;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
}