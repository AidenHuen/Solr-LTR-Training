package DataStructure;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class FeatureConfig {
	public List<String> strProp = new ArrayList<String>();
	public List<String> valueProp = new ArrayList<String>();
	public List<String> rank_feature = new ArrayList<String>();
	
	public FeatureConfig(JSONArray featureJSON,JSONArray valueJSON,JSONArray strJSON){
		for(int i=0;i<featureJSON.length();i++){
			rank_feature.add(featureJSON.getString(i));
//			System.out.println(featureJSON.getString(i));
		}
		for(int i=0;i<valueJSON.length();i++){
			valueProp.add(valueJSON.getString(i));
		}
		for(int i=0;i<strJSON.length();i++){
			strProp.add(strJSON.getString(i));
		}	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
