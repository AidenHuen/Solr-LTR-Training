package Main;

import java.util.List;

import Common.DBhelper;

public class OriginalDataFactory{
	
	/***
	 * ��ȡ���������ԭʼ����ԭʼ����
	 */
    public static void saveOriginalData(){
		DBhelper db = new DBhelper();
		db.connect2dc();
		List<String> keywords = db.getHitKeyword();
		db.getKeywordProductPair(keywords);
		db.getProductPropAll();
		db.getProductComplexAll();
    }
    
	public static void main(String[] args) {

		OriginalDataFactory.saveOriginalData();	    
	}
}
