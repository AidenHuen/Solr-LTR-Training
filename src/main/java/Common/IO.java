package Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import DataStructure.FeatureConfig;
import DataStructure.Product;

public class IO {
	
    public static List<String> readTxtFile(String filePath,String encoding){
    	List<String> rows = new ArrayList<String>();
        try {
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
//                        System.out.println(lineTxt);
                        rows.add(lineTxt);
                    }
                    read.close();
		        }else{
		            System.out.println("找不到指定的文件"+":"+filePath);
		        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return rows;
    }
    
    /** 
     * 创建文件 
     * @param fileName 
     * @return 
     */  
    public static boolean createFile(String name)throws Exception{ 
    File fileName = new File(name);
     boolean flag=false;  
     try{  
      if(!fileName.exists()){  
       fileName.createNewFile();  
       flag=true;  
      }  
     }catch(Exception e){  
      e.printStackTrace();  
     }  
     return true;  
    }   

    public static boolean writeTxtFile(String content,String  name)throws Exception{  
    	File fileName = new File(name);
    	RandomAccessFile mm=null;  
    	  boolean flag=false;  
    	  FileOutputStream o=null;  
    	  try {  
    	   o = new FileOutputStream(fileName);  
    	      o.write(content.getBytes("GBK"));  
    	      o.close();  
    	//   mm=new RandomAccessFile(fileName,"rw"); 
    	//   mm.writeBytes(content);  
    	   flag=true;  
    	  } catch (Exception e) {  
    	   // TODO: handle exception  
    	   e.printStackTrace();  
    	  }finally{  
    	   if(mm!=null){  
    	    mm.close();  
    	   }  
    	  }  
    	  return flag;  
    }
    
	public static void append(String file, String str, String code)throws Exception{
		/*
		 * 向文件file追加str，以code的编码形式
		 */
		RandomAccessFile raf=new RandomAccessFile(file, "rw");
		raf.seek(raf.length());
		raf.write(str.getBytes(code));
		raf.close();
	}
	
	/***
	 * 读取商品特征配置文件
	 * @return 返回商品特征配置对象
	 */
	static public FeatureConfig readFeaturesCongfig(){
		  List<String> rows = IO.readTxtFile(Path.featureConfig_json, Path.code);
		  String jsonStr = "";
		  for(String row:rows){
			  jsonStr+=row;
		  }
		  JSONObject jsonObj = new JSONObject(jsonStr);
		  JSONArray featureJSON = jsonObj.getJSONArray("rank_feature");
		  JSONArray valueJSON = jsonObj.getJSONArray("value_prop");
		  JSONArray strJSON = jsonObj.getJSONArray("str_prop");
		  FeatureConfig featureConfig = new FeatureConfig(featureJSON,valueJSON,strJSON);
		  return featureConfig;
	}
	
	
	/**
	 * 读取商品一般属性
	 * @param productFeatureSet
	 * @param conf
	 * @return
	 */
	static public HashMap<Integer,Product> readPropJson(HashMap<Integer,Product> productFeatureSet,FeatureConfig conf){
//		
		List<String> rows = IO.readTxtFile(Path.prop_json, Path.code);
		JSONObject propJson = new JSONObject(rows.get(0));
		List<String> features = new ArrayList<String>();
		for(Object o:propJson.keySet()){
			features.add(o.toString());
		}
		for(String row:rows){
			Product product = new Product();
			propJson = new JSONObject(row);
			for(String feature:features){
				if(conf.strProp.contains(feature)){
					product.strProp.put(feature, propJson.getString(feature));
				}
				else if(conf.valueProp.contains(feature)){
					product.valueProp.put(feature, Integer.valueOf(propJson.getString(feature)));
				}
				else{
					product.rank_feature.put(feature, Double.valueOf(propJson.getString(feature)));
//					System.out.println(feature+":"+Double.valueOf(propJson.getString(feature)));
				}
			}
			productFeatureSet.put(product.valueProp.get("product_id"), product);
		}
		return productFeatureSet;
	}

	static public HashMap<Integer,Product> readComplexJson(HashMap<Integer,Product> productFeatureSet,FeatureConfig conf){

		List<String> rows = IO.readTxtFile(Path.complex_json, Path.code);
		JSONObject complexJson = new JSONObject(rows.get(0));
		List<String> features = new ArrayList<String>();
		for(Object o:complexJson.keySet()){
			features.add(o.toString());
		}
		for(String row:rows){
			Product product = new Product();
			complexJson = new JSONObject(row);
			for(String feature:features){
				int product_id = complexJson.getInt("product_id");
				if(productFeatureSet.get(product_id)!=null){
					if(conf.strProp.contains(feature)){
						productFeatureSet.get(product_id).strProp.put(feature, complexJson.getString(feature));
					}
					else if(conf.valueProp.contains(feature)){
						productFeatureSet.get(product_id).valueProp.put(feature,complexJson.getInt(feature));
					}
					else{
						productFeatureSet.get(product_id).rank_feature.put(feature, complexJson.getDouble(feature));
//						System.out.println(feature+":"+Double.valueOf(propJson.getString(feature)));
					}
				}
			}

		}
		return productFeatureSet;
	}
	
	/**
	 * 读取商品特征
	 * @return 商品特征哈希集
	 */
	public static HashMap<Integer,Product> readProductFeatureDict(){
    	FeatureConfig conf = readFeaturesCongfig();
    	HashMap<Integer,Product> productFeatureDict = new HashMap<Integer,Product>();
    	productFeatureDict = IO.readPropJson(productFeatureDict,conf);
    	productFeatureDict = IO.readComplexJson(productFeatureDict, conf);
    	return productFeatureDict;
	}
	
    public static void main(String[] args){ 
    	HashMap<Integer,Product> maps = IO.readProductFeatureDict();
//    	for(Integer i:maps.keySet()){
//    		System.out.println(i);
//    	}
    	
    }  
}
