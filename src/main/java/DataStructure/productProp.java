package DataStructure;

public class productProp {
	public double price;
	public double add_time;
	public String product;//��Ʒ
	public int catID;//���id
	public String cat;//���
	public int brandID;//Ʒ��id
	public String brand;//Ʒ��
	
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
