package DataStructure;


/**
 * Product�������
 * @author AdienHuen
 * 
 */
public class ProductFeature {
//	public int productID; //��Ʒid
	public String product;//��Ʒ
	public int catID;//���id
	public String cat;//���
	public int brandID;//Ʒ��id
	public String brand;//Ʒ��
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
