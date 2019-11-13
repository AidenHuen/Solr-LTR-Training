package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import Calculation.BM25;
import Calculation.ValueCalculation;
import Calculation.Normalization;
import Common.IO;
import Common.Path;
import DataStructure.FeatureConfig;
import DataStructure.KeywordProductPair;
import DataStructure.Product;
import DataStructure.ProductFeature;
import DataStructure.productProp;
import DataStructure.productComplex;

/***
 * 利用原始数据，生成与ranlib匹配的样本集(训练集,测试计,验证集)
 * @author AdienHuen
 *
 */
public class SampleSetFactory {
	
	/***
	 * 读取关键词和对应商品和关键词对商品的转换率因子，计算作为衡量搜索结果的指标
	 * @return
	 */
	public static List<KeywordProductPair> readPairRelevancy(){ 
		List<String> rows = IO.readTxtFile(Path.keyword_product_pair_txt_path, Path.code);
		List<KeywordProductPair> pairList= new ArrayList<KeywordProductPair>();
		for(String row:rows){
			String[] term =  row.replace("\n","").split("`");
			double pro_uv = Double.valueOf(term[2]);
			double bas_uv =  Double.valueOf(term[3]);
			double pay_num = Double.valueOf(term[4]);
			double relevancy = ValueCalculation.getPairRelevancy(pro_uv, bas_uv, pay_num);
				KeywordProductPair pair = new KeywordProductPair(term[0],Integer.valueOf(term[1]),relevancy);
				pairList.add(pair);
			}
//			System.out.println(pair.keyword+" "+pair.productID+" "+pair.relevancy);
		return pairList;
	}
	
//	public static Map<Integer,String> readProductProp(){
//		List<String>IO.readTxtFile(Path.product_prop_txt,Path.code);
//	}
//	
	/***
	 * 读取商品complex特征
	 * @return productComplexDict，{商品:complex特征}字典
	 */
	public static Map<Integer,productComplex> readProductComplex(){
		List<String> rows = IO.readTxtFile(Path.product_complex_txt, Path.code);
		Map<Integer,productComplex> productComplexDict = new HashMap<Integer,productComplex>();
		for(String row:rows){
			String[] term = row.split("``");
			productComplex complex = new productComplex(Double.valueOf(term[1]),Double.valueOf(term[2]),Double.valueOf(term[3]));
			productComplexDict.put(Integer.valueOf(term[0]), complex);
		}
		return productComplexDict;
	}
	
	/***
	 * 读取商品prop特征
	 * @return productComplexDict，{商品:prop特征}字典
	 */
	public static Map<Integer,productProp> readProductProp(){
		List<String> rows = IO.readTxtFile(Path.product_prop_txt, Path.code);
		Map<Integer,productProp> productPropDict = new HashMap<Integer,productProp>();
		for(String row:rows){
			String[] term = row.split("``");
			productProp prop = new productProp(term[1].toLowerCase(),Double.valueOf(term[2]),Integer.valueOf(term[3]),Integer.valueOf(term[3]),Double.valueOf(term[5]));
			productPropDict.put(Integer.valueOf(term[0]), prop);
		}
		return productPropDict;
	}
	
	/***
	 * 获取关键词和对应标号（ranklib训练时需要标号）
	 * @return keywordID,{keyword:index}
	 */
	private static Map<String,Integer> readKeywordID(){
		Map<String,Integer> keywordID = new HashMap<String,Integer>();
		List<String> rows = IO.readTxtFile(Path.keyword_txt_path,Path.code);
		int i=0;
		for(String row:rows){
			row = row.replace("\n", "").toLowerCase();
			keywordID.put(row, i);
			i++;
		}
		return keywordID;
	}
	
	/***
	 * 获取相关样本集，并保存至data/SampleSet
	 * @throws Exception
	 */
//	public static void createSampleSet() throws Exception{
//		List<KeywordProductPair> pairList = SampleSetFactory.readPairRelevancy();
//		Map<Integer,ProductFeature> productInfoDict = new HashMap<Integer,ProductFeature>();
//		System.out.println("====更新训练集，验证集，测试集====");
//		System.out.println("---读取原始数据---");
//		Map<Integer,productProp> productPropDict = SampleSetFactory.readProductProp();
//		Map<Integer,productComplex> productComplexDict = SampleSetFactory.readProductComplex();
//		Map<String,Integer> keywordID = SampleSetFactory.readKeywordID();
//		for(KeywordProductPair pair:pairList){
//			Integer id = pair.productID;
//			System.out.println(id);
//			try{
//				productInfoDict.put(id, new ProductFeature(productComplexDict.get(id),productPropDict.get(id)));				
//			}
//			catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		System.out.println("---数据预处理---");
//		standardization.standard(productInfoDict);
//		BM25 bm25 = new BM25(productInfoDict);
//		
//		//清空文件
//		System.out.println("---清空原文件---");
//		IO.writeTxtFile("",Path.testSetPath);
//		IO.writeTxtFile("",Path.trainSetPath);
//		IO.writeTxtFile("",Path.valiSetPath);
//		//
//		System.out.println("---更新数据集---");
//		for(KeywordProductPair pair:pairList){
////			System.out.println(pair.productID);
////			productInfoDict.get(pair.productID).print();
//			try{
//				double value = bm25.getValue(pair.keyword,productInfoDict.get(pair.productID).product);
//				if(value > 0){
//					ProductFeature features = productInfoDict.get(pair.productID);
//					String row =pair.relevancy+" "+keywordID.get(pair.keyword)+" 1:"+value+" 2:"+features.price
//							+" 3:"+features.basket+ " 4:"+features.pay_num+" 5:"+features.review+" 6:"+features.add_time+"\n";
////					System.out.println(pair.relevancy+" "+keywordID.get(pair.keyword)+" 1:"+value+" 2:"+features.price
////							+" 3:"+features.basket+ " 4:"+features.pay_num+" 5:"+features.review+" 6:"+features.add_time);
//					double r = Math.random();
//					if(r>0.666){
//						IO.append(Path.testSetPath, row, Path.code);
//					}
//					else if(r>0.333&&r<0.666){
//						IO.append(Path.trainSetPath, row, Path.code);
//					}
//					else{
//						IO.append(Path.valiSetPath, row, Path.code);
//					}
//				}
//			}
//			catch(Exception e){
//				e.printStackTrace();
//			}
//			}
//			
//		System.out.println("===训练集，验证集，测试集已更新===");
//	}
	
	/***
	 * 获取相关样本集，并保存至data/SampleSet
	 * @throws Exception
	 */
	public static void createSampleSet() throws Exception{
		List<KeywordProductPair> pairList = SampleSetFactory.readPairRelevancy();
		System.out.println("====更新训练集，验证集，测试集====");
		System.out.println("---读取原始数据---");
		HashMap<Integer,Product> productDict = IO.readProductFeatureDict();
		System.out.println("read productDict finish");
		Map<String,Integer> keywordID = SampleSetFactory.readKeywordID();
		System.out.println("read keywordID finish");
		Map<String,Double> maxDict = new HashMap<String,Double>();
		Map<String,Double> minDict = new HashMap<String,Double>();
		Normalization.getlinerNormParms(maxDict,minDict);
		System.out.println("---数据预处理---");
		BM25 bm25 = new BM25(productDict);
		FeatureConfig conf = IO.readFeaturesCongfig();
		//清空文件
		System.out.println("---清空原文件---");
		IO.writeTxtFile("",Path.testSetPath);
		IO.writeTxtFile("",Path.trainSetPath);
		IO.writeTxtFile("",Path.valiSetPath);
		//
		System.out.println("---更新数据集---");
		for(KeywordProductPair pair:pairList){
			try{
				double value = bm25.getValue(pair.keyword,productDict.get(pair.productID).strProp.get("product_name"));

				if(value > 0){
					Product product = productDict.get(pair.productID);
					product.rank_feature.put("BM25", value); //添加BM25特征
					String row =pair.relevancy+" "+keywordID.get(pair.keyword);
					for(int i=0;i<conf.rank_feature.size();i++){
						if(conf.rank_feature.get(i).equals("BM25")){

							double normValue = Normalization.getLinerNormResult(product.rank_feature.get(conf.rank_feature.get(i)), maxDict.get(conf.rank_feature.get(i)), minDict.get(conf.rank_feature.get(i)));
							row+= " "+(i+1)+":"+normValue;
						}
						else{
							double normValue = Normalization.getLogLinerNormResult(product.rank_feature.get(conf.rank_feature.get(i)), maxDict.get(conf.rank_feature.get(i)), minDict.get(conf.rank_feature.get(i)));
							row+= " "+(i+1)+":"+normValue;
						}

					}
					row+="\n";

					double r = Math.random();
					if(r>0.666){
						IO.append(Path.testSetPath, row, Path.code);
					}
					else if(r>0.333&&r<0.666){
						IO.append(Path.trainSetPath, row, Path.code);
					}
					else{
						IO.append(Path.valiSetPath, row, Path.code);
					}
				}
			}
			catch(Exception e){
//				e.printStackTrace();
			}
		}
		System.out.println("===训练集，验证集，测试集已更新===");
		
	}
	

	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SampleSetFactory.createSampleSet();
	}
}