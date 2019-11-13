package Main;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import Calculation.Normalization;
import Common.IO;
import Common.Path;
import DataStructure.Product;
import DataStructure.ProductFeature;
import DataStructure.productProp;
import DataStructure.productComplex;

/***
 * 将product以及相关属性转换为json格式，用于上传至solr
 * @author AdienHuen
 *
 */
public class ProductJsonSetFactory {
	/**
	 * 读取.txt产品原始数据，并转换为jsonArray类 
	 * @return
	 */
	private static JSONArray products2Json(){
		// TODO Auto-generated method stub
		Map<Integer,productProp> productPropDict = SampleSetFactory.readProductProp();
		Map<Integer,productComplex> productComplexDict = SampleSetFactory.readProductComplex();
		Map<Integer,ProductFeature> productInfoDict = new HashMap<Integer,ProductFeature>();
		for(Integer id:productPropDict.keySet()){

			try{
				productInfoDict.put(id, new ProductFeature(productComplexDict.get(id),productPropDict.get(id)));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		Normalization.linerNorm(productInfoDict);
		JSONArray productsJson = new JSONArray();
		
		for(Integer id:productInfoDict.keySet()){
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("id", id);
			jsonobj.put("product_name", productInfoDict.get(id).product);
			jsonobj.put("price_weight", productInfoDict.get(id).price);
			jsonobj.put("add_time_weight", productInfoDict.get(id).add_time);
			jsonobj.put("review_weight", productInfoDict.get(id).review);
			jsonobj.put("pay_num_weight", productInfoDict.get(id).review);
			jsonobj.put("basket_weight", productInfoDict.get(id).basket);
//			System.out.println(jsonobj);
			productsJson.put(jsonobj);
		}

		return productsJson;
	 }
	
	/**
	 * 将productDict转换为jsonArray类 
	 * @return
	 */
	private static JSONArray products2Json(HashMap<Integer,Product> productDict){
		// TODO Auto-generated method stub
		JSONArray productsJson = new JSONArray();
		Map<String,Double> maxDict = new HashMap<String,Double>();
		Map<String,Double> minDict = new HashMap<String,Double>();
		Normalization.getlinerNormParms(maxDict,minDict);
		for(Integer id:productDict.keySet()){
			JSONObject jsonobj = new JSONObject();
			Product product = productDict.get(id);
			for(String key:product.strProp.keySet()){
				jsonobj.put(key, product.strProp.get(key));
			}
			for(String key:product.rank_feature.keySet()){
				jsonobj.put(key, Normalization.getLogLinerNormResult(product.rank_feature.get(key), maxDict.get(key), minDict.get(key)));
			}
//			for(String key:product.valueProp.keySet()){
//				jsonobj.put(key, product.valueProp.get(key));
//			}
			System.out.println(jsonobj.toString());
			productsJson.put(jsonobj);
		}
		return productsJson;
	 }
	
	/*
	 * 将porducts相关特征，以json的形式，并保存到data目录下的json文件夹
	 */
	static public void createProductsJson(){
			
		JSONArray productsJson = products2Json(IO.readProductFeatureDict());

		try{
			IO.writeTxtFile("", Path.products_json);
			IO.writeTxtFile(productsJson.toString(), Path.products_json);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		createProductsJson();
	}
}
