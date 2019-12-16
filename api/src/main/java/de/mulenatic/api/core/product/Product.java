package de.mulenatic.api.core.product;

public class Product {

    private  int productId;
    private  String name;
    private  int weight;
    private  String serviceAddress;

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

    public void setProductId(int productId) {
	this.productId = productId;
    }

    public void setName(String name) {
	this.name = name;
    }

    public void setWeight(int weight) {
	this.weight = weight;
    }

    public void setServiceAddress(String serviceAddress) {
	this.serviceAddress = serviceAddress;
    }

}
