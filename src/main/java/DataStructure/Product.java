package DataStructure;

import java.util.HashMap;
import java.util.Map;

/***
 * product特征封装形式
 * @author AdienHuen
 *
 */
public class Product {
	public Map<String,Integer> valueProp = new HashMap<String,Integer>();
	public Map<String,String> strProp = new HashMap<String,String>();
	public Map<String,Double> rank_feature = new HashMap<String,Double>();
}
