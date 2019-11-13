package Calculation;

import java.util.HashMap;
import java.util.Map;

import DataStructure.Product;
import DataStructure.ProductFeature;

public class BM25 {
	double AvgDocLen;
	double k=1.2,b=0.75; 
	Map<String,Double> IDF;
	
	
//	public BM25(Map<Integer,ProductFeature> productInfoDict){
//		AvgDocLen = ValueCalculation.getAvgDocLength(productInfoDict);
//		IDF = ValueCalculation.getIDF();
//	}
	
	public BM25(HashMap<Integer,Product> productDict){
		AvgDocLen = ValueCalculation.getAvgDocLength2(productDict);
		IDF = ValueCalculation.getIDF(productDict);
	}
	
	public double getValue(String keyword,String product_name){
		String[] keywordSet = keyword.toLowerCase().split(" ");
		String[] docWords = product_name.toLowerCase().split(" ");
		double docLen = docWords.length;
		Map<String,Integer> productWordFreq = new HashMap<String,Integer>();
		double result = 0;
		for(String word:docWords){
			if(productWordFreq.get(word) !=null){
				productWordFreq.put(word, productWordFreq.get(word)+1);
			}
			else{
				productWordFreq.put(word,1);
			}
		}

		for(String word:keywordSet){
			if(productWordFreq.containsKey(word)){
				result += this.IDF.get(word)*(this.k+1)*productWordFreq.get(word)
				/(productWordFreq.get(word)+this.k*(1-this.b)+this.b*docLen/this.AvgDocLen);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
