package DataStructure;


/**
 * Product相关特征
 * @author AdienHuen
 * 
 */
public class ProductFeature {
//	public int productID; //商品id
	public String product;//商品
	public int catID;//类别id
	public String cat;//类别
	public int brandID;//品牌id
	public String brand;//品牌
	public double basket;
	public double pay_num;
	public double review;
	public double price;
	public double add_time;
	
	public ProductFeature(productComplex complex,productProp prop){
		this.product = prop.product;
		this.catID = prop.catID;
		this.brandID = prop.brandID;
		this.price = prop.price;
		this.add_time = prop.add_time;
		this.review = complex.review;
		this.basket = complex.basket;
		this.pay_num = complex.pay_num;
	}
	
	public void print(){
		String row = "";
		row += this.price+" ";
		row += this.add_time+" ";
		row += this.review+" ";
		row += this.basket+" ";
		row += this.pay_num;
		System.out.println(row);
	}
	
}
