package Calculation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.IO;
import Common.Path;
import DataStructure.Product;
import DataStructure.ProductFeature;

public class ValueCalculation {

	static public double getPairRelevancy(double pro_uv,double bas_uv,double pay_num){
		return 8*pay_num/(pro_uv+1)+2*bas_uv/(pro_uv+1);
	}
	
	static public double getAvgDocLength(Map<Integer,ProductFeature> productInfoDict){
		double num = productInfoDict.keySet().size();
		double sum = 0;
		for(Integer id:productInfoDict.keySet()){
			sum += productInfoDict.get(id).product.split(" ").length;
		}
		return sum/num;
	}

	static public double getAvgDocLength2(Map<Integer,Product> productInfoDict){
		double num = productInfoDict.keySet().size();
		double sum = 0;
		for(Integer id:productInfoDict.keySet()){	
			sum += productInfoDict.get(id).strProp.get("product_name").split(" ").length;
		}
		return sum/num;
	}
	
	static public Map<String,Double> getIDF(HashMap<Integer,Product> productDict){

		List<String> rows = IO.readTxtFile(Path.prop_json, Path.code);
		Map<String,Double> idfMap = new HashMap<String,Double>();
		double docNum = rows.size();
		for(Integer i:productDict.keySet()){
			String[] terms = productDict.get(i).strProp.get("product_name").split(" ");
			Set<String> tSet = new HashSet<String>(Arrays.asList(terms));
			for(String term:tSet){
				term = term.toLowerCase();
				if(idfMap.get(term) !=null){
					idfMap.put(term, idfMap.get(term)+1);
				}
				else{
					idfMap.put(term,1.0);
				}
			}
		}
		for(String key:idfMap.keySet()){
			idfMap.put(key, Math.log(1+docNum/(idfMap.get(key)+1)));
		}
//		for(String key:idfMap.keySet()){
//			System.out.println(key+" "+idfMap.get(key));
//		}
		return idfMap;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
