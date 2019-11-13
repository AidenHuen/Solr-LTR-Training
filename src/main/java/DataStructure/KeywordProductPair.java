package DataStructure;

/***
 * 封装关键词-商品对及其关联度因子
 * @author AdienHuen
 *
 */
public class KeywordProductPair {
	public String keyword;
	public int productID;
	public double relevancy;
	
	public KeywordProductPair(String keyword,int productID,double relevancy){
		this.keyword = keyword;
		this.productID = productID;
		this.relevancy = relevancy;
	}
	
	
}
