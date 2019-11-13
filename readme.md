# Solr-LTR-Training<br>
## 简介
>  项目用于为Apache Solr（7.10）训练排序学习模型。项目基于特定格式的原始数据生成用于排序学习训练的数据集，并利用**ranklib**对数据集进行训练生成模型参数文件，接着将ranklib的模型格式转换为solr的模型格式。最后将model上传到solr，实现ltr排序。
目前项目支持的模型为[org.apache.solr.ltr.model.MultipleAdditiveTreesModel](https://lucene.apache.org/solr/7_0_0//solr-ltr/org/apache/solr/ltr/model/MultipleAdditiveTreesModel.html)。
项目自带的原始数据源于https://www.banggood.com 某一星期内的搜索记录数据和某一个月内的商品特征数据。<br>
<br><br>
## Quick Start
>  下面将详细描述在现有数据[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)的情况下，
进行MART模型训练和solr-ltr配置的具体流程和操作。<br> 


>### 构造训练集，测试集，验证集 <br>
>  运行脚本程序[Solr-LTR-Training/src/main/java/Main/SampleSetFactory](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Main/SampleSetFactory.java)。
该程序可更新Solr-LTR-Training/data/SampleSet下存放的训练集trainSet.txt，验证集VailiSet.txt，测试集testSet.txt。数据用于ranklib模型。
这些数据集的构造，基于原始数据[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)。
三个数据集的格式一致，随机等比例的进行分配。脚本执行的代码如下：<br>
```Java
SampleSetFactory.createSampleSet();
```
>  数据集格式如下所示：<br>
```Java
1.6666666666666665 0 1:0.052396521256776483 2:0.13374604874102394 3:0.5227324654138016 4:0.30751221714520194 5:0.0 6:0.8661299198332197
6.0 0 1:0.05388126843863542 2:0.21648865808128998 3:0.43786728585006884 4:0.2384830647363997 5:0.07789122848925072 6:0.732263157721875
3.6 0 1:0.06491879482663733 2:0.08029717416530516 3:0.5155211937446508 4:0.4131687522256421 5:0.2587490602637329 6:0.9433190286166003
2.25 0 1:0.057118366297761736 2:0.15670786934272418 3:0.6853757184226908 4:0.6270966778889606 5:0.27923713326906724 6:0.9016873920030167
```
>在第一行中，1.6666666666666665表示搜索词与商品间的匹配度（类似与线性回归中的Y值），计算基于[Solr-LTR-Training/data/OriginalData/keyword_product_pair.txt](Solr-LTR-Training/data/OriginalDataSet/keyword_product_pair.txt)中的属性。
具体的属性含义在数据文件描述中说明。匹配度的计算在文件[Solr-LTR-Training/src/main/java/Calculation/ValueCalculation.java](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Calculation/ValueCalculation.java)中。
第二个值，“0”为关键词的标号，取决于关键词在[Solr-LTR-Training/data/OriginalData/keywords.txt](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/data/OriginalDataSet/keywords.txt)中的为位置；
“1:0.052396521256776483”指特征标号和对应的权重。特征标号所对应的特征可参照特征配置文件[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中"rerank_feature"数组的特征顺序,标号为1就是指"rerank_feature"数组中的第一个特征。
特征的都经过标准化处理，相关代码在文件[Solr-LTR-Training/src/main/java/Calculation/Normalization.java](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Calculation/Normalization.java)。
其中对“BM25”的处理比较特殊，为单纯的min-max标准化(Min-max normalization)，这是考虑到BM25的值偏离度不大。其余的特征先进行log运算，然后再进行min-max标准化。<br>

> ### 训练MART模型 <br>
>利用[ranklib](https://sourceforge.net/p/lemur/wiki/RankLib/)训练模型。在项目根目录下执行命令：<br>
```Java
java -jar ./ranklib-2.3/bin/RankLib.jar -train ./data/SampleSet/trainSet.txt -test ./data/SampleSet/testSet.txt -validate ./data/SampleSet/valiSet.txt -ranker 6 -metric2t NDCG@10 -metric2T ERR@10 -save ./model/MART.txt
```
>部分参数描述:<br>
>>**-train <文件名>** ：调用训练集<br>
>>**-test <文件名>** ：调用测试集<br>
>>**-validate <文件名>** ：调用验证集<br>
>>**-metric2t**: 模型测试方法<br>
>>**-save <文件名>**: 模型保存路径<br>
>>**-ranker [1~8]** : 选择模型，ranklib支持8类LTR模型，模型名与对应标号如下：<br>
>>>MART (Multiple Additive Regression Trees, a.k.a. Gradient boosted regression tree) [6]<br>
>>>RankNet [1] <br>
>>>RankBoost [2]<br>
>>>AdaRank [3]<br>
>>>Coordinate Ascent [4]<br>
>>>LambdaMART [5]<br>
>>>ListNet [7]<br>
>>>Random Forests [8]<br>
><br>
>训练后,查看MART.txt文件:
```Java
## LambdaMART
## No. of trees = 1000
## No. of leaves = 10
## No. of threshold candidates = 256
## Learning rate = 0.1
## Stop early = 100

<ensemble>
	<tree id="1" weight="0.1">
		<split>
			<feature> 4 </feature>
			<threshold> 0.34135595 </threshold>
			<split pos="left">
				<feature> 2 </feature>
				<threshold> 0.1963398 </threshold>
				<split pos="left">
					<output> 0.3290064334869385 </output>
				</split>
				<split pos="right">
					<output> -0.8738656044006348 </output>
				</split>
			</split>
			<split pos="right">
				<feature> 3 </feature>
.....
.....
</ensemble>
```
>模型文件为xml格式。而solr的模型格式是json而且分布和参数命名都与ranklib训练出来的结果有一定的差别，因此还需要进行模型格式的转换。为此，专门写了一个solr模型转换的脚本程序。

>### ranklib模型转solr模型<br>
>运行脚本程序[Solr-LTR-Training/src/main/java/Main/SolrModelFactory.java](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Main/SolrModelFactory.java)。所执行的代码如下:<br>
```Java
SolrModelFactory mf = new SolrModelFactory(); //初始化对象
mf.storeMultipleAdditiveTrees("model/MART.txt","model/MART.json");//调用storeMultipleAdditiveTrees函数，"model/MART.txt"为ranklib训练的模型文件，"model/MART.json"为转换后的solr模型文件
```
>转换后的solr MART模型格式如:[MART.json](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/model/MART.json)<br>


>### Solr基本配置<br>
>接下来，是Solr的环境配置和待搜索的数据导入，为ltr模型的应用作准备。
>##### 启动Solr
>在solr（7.1.0）根目录下输入命令：<br>
```Java
./bin/solr start -Dsolr.ltr.enabled=true
```
>##### 创建solr core
>在solr根目录下输入命令：<br>
```Java
./bin/solr create -c products
```
>##### 配置solrconfig.xml
>该配置文件在solr-7.1.0/server/solr/products/conf，在文件中添加如下信息：<br>

```Java
<lib dir="${solr.install.dir:../../../..}/dist/" regex="solr-ltr-\d.*\.jar" />
<queryParser name="ltr" class="org.apache.solr.ltr.search.LTRQParserPlugin"/>
<cache name="QUERY_DOC_FV"
    class="solr.search.LRUCache"
    size="4096"
    initialSize="2048"
    autowarmCount="4096"
    regenerator="solr.search.NoOpRegenerator" />
<transformer name="features" class="org.apache.solr.ltr.response.transform.LTRFeatureLoggerTransformerFactory">
	<str name="fvCacheName">QUERY_DOC_FV</str>
</transformer>
```
>配置完后，保存并重启solr：
```Java
./bin/solr stop -all
./bin/solr start -Dsolr.ltr.enabled=true
```

>##### 导入商品数据
>为了测试方便，目前商品的field仅有product_name,product_id和用于ltr排序的特征属性。商品数据在文件**Solr-LTR-Training/data/json/products.json**中。
该文件由脚本程序[Solr-LTR-Training/src/main/java/Main/ProductJsonSetFactory.java](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Main/ProductJsonSetFactory.java)生成。商品的特征与[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中的
特征配置相一致（命名一致）。同时商品数据不存在"BM25"(在搜索的时候,Solr即时计算获得)。可见，学习排序在solr中的应用其实是一个“重排序”的功能。先基于文本关联度抽取一定量商品，再根据ltr模型进行再排序。
需要注意的是，商品的特征属性也是经过标准化处理的，log运算和max-min标准化(与模型训练时的数据集格式相一致)。<br>
>输入以下命令导入商品数据：<br>
```Java
./bin/post %path%/Solr-LTR-Training/data/json/products.json //路径为绝对路径，修改%path%
```
>这里可以对field的数据类型进行配置，也可以让solr自动识别各filed的数据类型<br>

>##### 导入模型和特征
>使用solr-ltr前，需要导入两个文件分别是特征文件和模型文件。<br>
>导入特征文件，在输入命令行：
```Java
curl -XPUT "http://localhost:8983/solr/products/schema/feature-store" --data-binary "@%PATH%/solr-ltr-feature.json" -H "Content-type:application/json" 
```
>命令中products为core名，%PATH%为文件所在路径。特征文件所在位置为[Solr-LTR-Training/conf/solr-ltr-feature.json](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/conf/solr-ltr-feature.json),文件的格式如下：<br>
```Java
[
	{
	"name" : "BM25",
	"class" : "org.apache.solr.ltr.feature.OriginalScoreFeature",
	"params" : {}
	},
	{
	"name":  "add_time", 
	"class": "org.apache.solr.ltr.feature.FieldValueFeature",
	"params": {"field": "add_time"}
	},
	{
	"name":  "price",
	"class": "org.apache.solr.ltr.feature.FieldValueFeature",
	params": {"field": "price"}
	},
	{
	"name":  "basket",
	"class": "org.apache.solr.ltr.feature.FieldValueFeature",
	"params": {"field": "basket"}
	},
	{
	"name":  "pay_num",
	"class": "org.apache.solr.ltr.feature.FieldValueFeature",
	"params": {"field": "pay_num"}
	},
	{
	"name":  "review",
	"class": "org.apache.solr.ltr.feature.FieldValueFeature",
	"params": {"field": "review"}
	}
]
```
>其中“name"为特征名，可随意命名,"parms"可指定该特征所用到的商品"field"。像上述所示，就是直接用field的值来作为ltr的特征。不过,BM25比较特殊，BM25并不是商品的属性，需要搜索的时候即时计算。因此用到的类为原始搜索得分org.apache.solr.ltr.feature.OriginalScoreFeature。<br>
>导入模型文件前，需要为训练好的模型文件添加一些配置。这是因为训练模型的时所用到的数据集中，BM25特征是经过max-min标准化的，而solr搜索时BM25的计算,B并没有进行标准化处理。因此要在[MART.json](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/model/MART.json)中添加对BM25特征标准化处理的配置。配置如下所示：<br>
```Java
	{  
	{
		"class" : "org.apache.solr.ltr.model.MultipleAdditiveTreesModel",
		"name" : "LambdaMART",
		"features":[
		{ 	
			"name" : "BM25"
			"norm" : {
				"class" : "org.apache.solr.ltr.norm.MinMaxNormalizer",
				"params" : { "min":"0.0", "max":"92.47450525426174" }
			}
		},
		{ "name" : "price"},
		{ "name" : "basket"},
		{ "name" : "pay_num"},
		{ "name" : "review"},
		{ "name" : "add_time"},
	   ],
	  }
	......  
	｝
```
>“"params" : { "min":"0.0", "max":"92.47450525426174"}”中的参数值在构造训练集，测试集，验证集时打印,也就是执行[Solr-LTR-Training/src/main/java/Main/SampleSetFactory.java](https://github.com/AdienHuen/Solr-LTR-Training/blob/master/src/main/java/Main/SampleSetFactory.java)的时候。<br>
>修改后，即可导入模型文件，命令如下：
```Java
curl -XPUT 'http://localhost:8983/solr/products/schema/model-store' --data-binary "@%Path%/MART.json" -H 'Content-type:application/json'
```
>%Path%为文件所在路径。<br>
>导入成功后，可以在UI上看到产生了两个新的文件_schema_feature-store.json和_schema_model-store.json。<br>
>(https://github.com/AdienHuen/Solr-LTR-Training/blob/master/picture/configResult.jpg)<br>

> ### 测试solr-ltr效果<br>
>solr配置好后，通过http协议，发送命令获取搜索结果：
```Java
http://localhost:8983/solr/products/query?q=product_name:bag&rq={!ltr%20model=MART%20reRankDocs=1000}&fl=product_name,id,score,[features]
```
>其中rq={!ltr%20model=MART%20reRankDocs=100}是利用ltr模型进行重排序的请求。"model=MART"指使用模型MART(模型名可随便在MART.json里配置)，“reRankDocs=100”指去前1000的document进行重排序。<br>

>输出结果如下：<br>
```Java
{
	"responseHeader":{
	"status":0,
	"QTime":481,
	"params":{
		"q":"product_name:bag",
		"fl":"product_name,id,score,[features]",
		"rq":"{!ltr model=MART reRankDocs=1000}"}},
	"response":{"numFound":7195,"start":0,"maxScore":0.8912517,"docs":[
		{
		"product_name":["Men Canvas Retro Canvas Travel Cycling Crossbody Bag Chest Bag"],
			"id":"7f0e8737-bb99-4f87-8999-8c0173e36dc6",
			"score":0.8912517,
			"[features]":"BM25=4.741643,add_time=0.9638977,price=0.48802152,basket=0.57379055,pay_num=0.5554985,review=0.40577072"},
		  {
			"product_name":["Women Nylon Travel Passport Bag Crossbody Travel Bag Useful Shoulder Bag"],
			"id":"d9de9961-febb-4617-b763-ac06e06f932b",
			"score":0.06596118,
			"[features]":"BM25=5.257847,add_time=0.95407635,price=0.42589036,basket=0.7309676,pay_num=0.65739316,review=0.4881277"},
		  {
			"product_name":["  Bike Frame Front Triangle Bag Cycling Pipe Pouch Tool Bag"],
			"id":"6d47babf-265c-4eab-ba22-b5a2ed93d305",
			"score":0.050511904,
			"[features]":"BM25=4.741643,add_time=0.8198018,price=0.10394799,basket=0.42217502,pay_num=0.42017365,review=0.50690013"},
		  {
			"product_name":["Waterproof Shoe Bag Travel Shoe Bag Shoe Case Bag Multicolor"],
			"id":"0480a2b1-3203-487f-8508-b5f715c6cc30",
			"score":-0.050930798,
			"[features]":"BM25=5.3548636,add_time=0.93256116,price=0.16925557,basket=0.18676566,pay_num=0.17468569,review=0.26945937"},
		  {
			"product_name":["Multifunction Canvas Tool Waist Bag Maintenance Bag Tools Kit Bag"],
			"id":"d206567d-21d2-4715-a7c0-e069dfc293b2",
			"score":-0.058703594,
			"[features]":"BM25=5.3548636,add_time=0.6287582,price=0.21007025,basket=0.38157985,pay_num=0.36548063,review=0.43501958"},
		  {
			"product_name":["Student Forest Wind Lace Fringe Retro Floral Dot Zipper Pencil Storage Bag Makeup Bag Stationery Bag"],
			"id":"d3b2741c-39fe-4b6f-b08d-73dd3e0ff518",
			"score":-0.271069,
			"[features]":"BM25=4.8211126,add_time=0.86471075,price=0.12270968,basket=0.3378133,pay_num=0.17468569,review=0.07789123"},
		  {
			"product_name":["Women Casual Vintage Bucket Bag Crossboby Bag Light Weight Beach Bag"],
			"id":"cdee24e1-53a5-4239-9aa6-15b248769682",
			"score":-0.2802631,
			"[features]":"BM25=5.257847,add_time=0.9587532,price=0.42707208,basket=0.52535886,pay_num=0.37616515,review=0.23367369"},
		  {
			"product_name":["Naturehike Travel Storage Bag Nylon Waterproof Compression Packing Picnic Bag For Sleeping Bag Clothing"],
			"id":"0b61238a-7339-42b4-bafa-707701258fe5",
			"score":-0.3123042,
			"[features]":"BM25=4.9868,add_time=0.58752835,price=0.28478464,basket=0.3398781,pay_num=0.34937137,review=0.3473506"},
		  {
			"product_name":["12 Hole Ocarina Protective Bag Thick Waterproof Protective Bag"],
			"id":"85cd2415-2f79-485c-9cf9-5b360f043897",
			"score":-0.3215233,
			"[features]":"BM25=4.8607717,add_time=0.89839727,price=0.10492812,basket=0.21536767,pay_num=0.23848307,review=0.3783941"},
		  {
			"product_name":["Women Graffiti Toiletry Bag Cosmetic Bag Travel Must-have High End Digital Usb Cable Storage Bag"],
			"id":"346f9802-6aca-450c-bfff-d8260a016db7",
			"score":-0.38795632,
			"[features]":"BM25=4.8211126,add_time=0.82167375,price=0.29424262,basket=0.43147457,pay_num=0.28643885,review=0.07789123"}]
	  }}
```
>可见，前几名的商品无论是销量，评论量还是加够量都比较高。后几名商品相对比较少。效果应该是比较显著的。<br>
>去掉重排序的请求，输入命令：
```Java
http://localhost:8983/solr/products/query?q=product_name:bag&fl=product_name,id,score,[features]
```
>结果：<br>
```Java
	{
	  "responseHeader":{
		"status":0,
		"QTime":3,
		"params":{
		  "q":"product_name:bag",
		  "fl":"product_name,id,score,[features]"}},
	  "response":{"numFound":28561,"start":0,"maxScore":5.9003043,"docs":[
		  {
			"id":"1005270",
			"product_name":["women inflatable bag swimming bag waterproof bag beach bag inflatable bag"],
			"score":5.9003043,
			"[features]":"BM25=5.9003043,add_time_weight=0.7116663,price_weight=0.3607677,basket_weight=0.23175651,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"product_name":["Women Inflatable Bag Swimming Bag Waterproof Bag Beach Bag Inflatable Bag"],
			"id":"d208a105-a57e-4998-ab0c-588c0946c756",
			"score":5.9003043,
			"[features]":"BM25=5.9003043,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"product_name":["Women Inflatable Bag Swimming Bag Waterproof Bag Beach Bag Inflatable Bag"],
			"id":"0557f31f-b1d3-4db4-8645-feb08fbb4dd3",
			"score":5.9003043,
			"[features]":"BM25=5.9003043,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"product_name":["Women Inflatable Bag Swimming Bag Waterproof Bag Beach Bag Inflatable Bag"],
			"id":"bb2c72c3-3fce-4083-a362-888dfdad7a0f",
			"score":5.9003043,
			"[features]":"BM25=5.9003043,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"id":"1131505",
			"product_name":["women pu leather smartphone bag bucket bag coin bag crossbody bag vintage shoulder bag"],
			"score":5.6918616,
			"[features]":"BM25=5.6918616,add_time_weight=0.9724192,price_weight=0.30402035,basket_weight=0.41182953,pay_num_weight=0.07789242,review_weight=0.07789242"},
		  {
			"id":"1136889",
			"product_name":["women portable cosmetic bag passport bag nylon wristlet clutches bag digital bag phone bag"],
			"score":5.6918616,
			"[features]":"BM25=5.6918616,add_time_weight=0.9904379,price_weight=0.3632397,basket_weight=0.3949647,pay_num_weight=0.12345657,review_weight=0.12345657"},
		  {
			"product_name":["Women PU Leather Smartphone Bag Bucket Bag Coin Bag Crossbody Bag Vintage Shoulder Bag"],
			"id":"e9bb9cdf-3bf8-46ce-8c2c-0539cae3206c",
			"score":5.6918616,
			"[features]":"BM25=5.6918616,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"product_name":["Women Portable Cosmetic Bag Passport Bag Nylon Wristlet Clutches Bag Digital Bag Phone Bag"],
			"id":"614efd7e-8136-4e09-8b1d-9340baf3f659",
			"score":5.6918616,
			"[features]":"BM25=5.6918616,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"product_name":["Women PU Leather Smartphone Bag Bucket Bag Coin Bag Crossbody Bag Vintage Shoulder Bag"],
			"id":"481a7597-2541-482d-9d8a-9d13a69aebb8",
			"score":5.6918616,
			"[features]":"BM25=5.6918616,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"},
		  {
			"product_name":["Women Portable Cosmetic Bag Passport Bag Nylon Wristlet Clutches Bag Digital Bag Phone Bag"],
			"id":"dcc7382b-6d52-4e13-8024-006486da321f",
			"score":5.6918616,
			"[features]":"BM25=5.6918616,add_time_weight=0.0,price_weight=0.0,basket_weight=0.0,pay_num_weight=0.0,review_weight=0.0"}]
	  }}
```
>可见在没用ltr的情况下，默认的搜索效果非常糟糕，排名前面的商品销量，评论等各方面都不高<br>
## 数据文件描述<br>
下面是关于项目中部分文件的描述，若需理解数据的含义和结构，从而增删训练特征，则需要详细阅读以下内容<br>
>#### 特征配置文件FeatureConf<br>
>特征配置文件[Solr-LTR-Training/conf/FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)为json格式，用以定义特征。
定义的特征将用于利用原始数据的属性，产生ranklib训练的数据集[Solr-LTR-Training/data/SampleSet](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/SampleSet)（验证集，训练集，测试集）<br>
>修改后，即可导入模型文件，命令如下：
```Java
{  
	"name": "productConfig",
	"str_prop": ["product_name"],
	"value_prop": ["product_id","cat_id","brand_id"],
	"rank_feature": ["BM25","price","basket","pay_num","review","add_time"]
}  
```
>其中 "str_prop"用于设置字符串型的属性名，“value_prop”用于设置数值型且不作为ltr特征的属性名。
上述二者作为document的field上传到solr，但不用于ltr排序。而"rank_feature"则是用于ltr计算的特征属性。上述例子中，特征包含：<br>
>>**BM25**:关联性因子，solr中默认的原始得分为org.apache.solr.ltr.feature.OriginalScoreFeature。<br>
>>**review**：商品评论数<br>
>>**price**：商品价格<br>
>>**pay_num**：商品30天内的销量<br>
>>**basket**：商品30天内的加购量<br>
>>**add_time**：商品30天内的加购量<br>
>**注意**:若需要增减特征因子，可以在rank_feature中添加新的因子<br>
>**注意**: 由于BM25是即时计算的，并非商品属性，因此若要采用其他关联度因子或者改变BM25计算的字符串对象，则需要修改BM25相关代码。<br>
><br>

>#### 原始数据文件<br>
>原始数据在目录[Solr-LTR-Training/data/OriginalData](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中,
包含了prop.json,complex.json,keyword.txt,keyword_product_pair.txt等文件,下面介绍相关文件内容和作用<br>
>##### 商品一般属性（prop.json）<br>
>描述:prop.json存放商品的一般属性原始数据,其中product_id为唯一区分项。数据格式如下:<br>
```Java
{"product_id":"7104","product_name":"12 Colors Acrylic Nail Art Tips Glitter Powder Dust","price":"5.78","add_time":"1507896522","cat_id":"1334","brand_id":"0"}
{"product_id":"7105","product_name":"500 White French Acrylic Half False Tips 3D Nail Art","price":"5.69","add_time":"1509783901","cat_id":"1327","brand_id":"0"}
{"product_id":"7157","product_name":"Acrylic UV Gel False Fake Nail Art Tips Clipper Manicure Cutter Tool","price":"3.76","add_time":"1507896522","cat_id":"1367","brand_id":"0"}
{"product_id":"7340","product_name":"5pcs 2 Way Nail Art Dotting Marbleizing Painting Pen","price":"2.25","add_time":"1507896522","cat_id":"1343","brand_id":"0"}
```
>**注意**:属性名（如："product_id"）要与特征配置文件[FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中的属性名一致<br>
>**注意**:可在此为商品添加新的特征项<br>
><br>
><br>

>##### 商品统计属性(complex.json)<br>
>描述:complex.json存放商品的统计属性，例如一定时间内的销售量，加够量等,其中product_id为唯一区分项。数据格式如下：<br>
```Java
	{"product_id":"18576","basket":"0.0","review":"0","pay_num":"0.0"}
	{"product_id":"18589","basket":"36.0","review":"135","pay_num":"7.0"}
	{"product_id":"18599","basket":"0.0","review":"10","pay_num":"0.0"}
```
>**注意**:属性名（如："basket"）要与特征配置文件[FeatureConf.json](https://github.com/AdienHuen/Solr-LTR-Training/tree/master/data/OriginalDataSet)中的属性名一致<br>
>**注意**:可在此为商品添加新的特征项<br>
><br>
><br>
>##### 搜索词-商品对(keyword_product_pair.txt)<br>
>描述:keyword_product_pair.txt存放搜索词以及商品的关系，例如在第一行中,"squishy"为搜索词，"1153352"为商品id,"14"是搜索词下点击该商品的uv，"6"是搜索词下加够该商品的数量，"4"搜索词下该商品的销量。数据格式如下：<br>
```Java
squishy`1153352`14`6`4`
squishy`1113507`18`10`3`
squishy`1181645`23`13`3`
squishy`1122654`6`3`3`
squishy`1160930`35`8`3`
squishy`1145181`19`17`3`
squishy`1120879`42`10`3`
squishy`1168577`30`17`3`
```
><br>
><br>
  
>##### 搜索词(keywords.txt)<br>
>描述:keywords.txt存放keyword_product_pair.txt中包含的搜索词，用于将搜索词转化为标号(ranklib训练的需要)<br>
><br>
><br>