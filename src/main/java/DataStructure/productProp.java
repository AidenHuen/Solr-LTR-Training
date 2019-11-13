package DataStructure;

public class productProp {
	public double price;
	public double add_time;
	public String product;//商品
	public int catID;//类别id
	public String cat;//类别
	public int brandID;//品牌id
	public String brand;//品牌
	
	public productProp(String product,double price,int catID,int brandID,double add_time){
		this.product = product;
		this.price = price;
		this.catID = catID;
		this.brandID = brandID;
		this.add_time = add_time;
	}
	
	
	public static void main(String[] args) {

		// TODO Auto-generated method stub

	}

}
