package de.mulenatic.api.core.product;

public class Product {

	private final int productId;
	private final String name;
	private final int weight;
	private final String serviceAddress;

    	public Product() {
		this.productId = 0;
		this.name = null;
		this.weight = 0;
		this.serviceAddress = null;
	}

	public Product(int productId, String serviceAddress, int weight, String name) {
		this.productId = productId;
		this.serviceAddress = serviceAddress;
		this.weight = weight;
		this.name = name;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public int getProductId() {
		return productId;
	}

	public String getName() {
		return name;
	}

	public int getWeight() {
		return weight;
	}


}
