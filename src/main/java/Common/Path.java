package Common;

/***
 * 文件路径集
 * @author AdienHuen
 *
 */
public class Path {
    static public String code = "GBK"; //编码
    
    static public String keyword_txt_path = "data/OriginalDataSet/keywords.txt"; //关键词原始数据
    static public String keyword_product_pair_txt_path = "data/OriginalDataSet/keyword_product_pair.txt"; //关键词-产品对原始数据
    static public String product_prop_txt = "data/OriginalDataSet/product_prop.txt"; //所有产品的属性原始数据
    static public String product_complex_txt = "data/OriginalDataSet/product_complex.txt"; //所有产品一定时间内的销售，加够，评论统计量情况
    
    static public String testSetPath = "data/SampleSet/testSet.txt"; //测试集
    static public String trainSetPath = "data/SampleSet/trainSet.txt"; //训练集
    static public String valiSetPath = "data/SampleSet/valiSet.txt"; //验证集集
    
    static public String complex_json = "data/OriginalDataSet/complex.json";
    static public String prop_json = "data/OriginalDataSet/prop.json";
    static public String featureConfig_json = "conf/featureConfig.json";
    static public String products_json = "data/json/products.json"; //上传solr的产品数据路径
    static public String model_txt = "model/mymodel.txt"; //ranlib训练的模型

}
