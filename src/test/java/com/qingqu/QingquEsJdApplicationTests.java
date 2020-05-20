package com.qingqu;

import com.alibaba.fastjson.JSON;
import com.qingqu.pojo.Content;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * es7.6.x 高级客户端测试 API
 */
@SpringBootTest
class QingquEsJdApplicationTests {

	//面向对象来操作
	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	// 测试索引的创建 Request
	@Test
	void testCreateIndex() throws IOException {
		// 1、创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("jd_goods4");
		Map<String, Object> stringObjectMap = new HashMap<>();

		//2、客户端执行请求 IndicesClient,请求后获得响应
		CreateIndexResponse createIndexResponse =
				client.indices().create(request, RequestOptions.DEFAULT);

		System.out.println(createIndexResponse);
	}

	//测试获取索引
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("jd_goods4");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//测试删除索引

	@Test
	void testDeleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("jd_goods4");
		// 删除
		AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println(delete.isAcknowledged());
	}

	//测试添加文档
	@Test
	void testAddDocument() throws IOException {
		//创建对象
		Content content = new Content("标题","价格","图片地址");
		//创建请求
		IndexRequest request = new IndexRequest("jd_goods4");

		//规则  put/kuang_index/_doc/1
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(1));
		request.timeout("1s");

		//将我们的数据放入请求  json
		IndexRequest source = request.source(JSON.toJSONString(content), XContentType.JSON);

		//客户端发送请求,获取响应的结果
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

		System.out.println(indexResponse.toString());//
		System.out.println(indexResponse.status()); //对应我们命令的返回的状态 CREATED
	}

	//获取文档，判断是否存在
	@Test
	void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("jd_goods4", "1");
		//不获取返回的_sources 的上下文了
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");

		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//获得文档的信息
	@Test
	void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("jd_goods4", "1");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
		System.out.println(getResponse.getSourceAsString()); //打印文档的内容
		System.out.println(getResponse);//返回的全部内容和命令是一样的
	}

	//更新文档的内容
	@Test
	void testUpdateDocument() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("jd_goods4","1");

		updateRequest.timeout("1s");

		Content content = new Content("标题","价格","图片地址");
		updateRequest.doc(JSON.toJSONString(content), XContentType.JSON);

		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println(updateResponse.status());
		System.out.println(updateResponse);

	}

	// 删除文档记录
	@Test
	void testDeleteDocument() throws IOException {

		DeleteRequest request = new DeleteRequest("jd_goods4", "1");
		request.timeout("1s");
		DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
		System.out.println(deleteResponse.status());
	}

	//特殊的，真的项目一般都会批量插入数据
	@Test
	void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("10s");

		ArrayList<Content> contentList = new ArrayList<>();


		for(int i=0 ;i< contentList.size();i++){
			//批量更新和批量删除，就在这里修改对应的请求就可以了
			bulkRequest.add(new IndexRequest("jd_goods4")
					.id(""+(i+1))
					.source(JSON.toJSONString(contentList.get(i)), XContentType.JSON));

		}

		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures());//是否失败，返回false代表成功
	}

	//查询
	// SearchRequest 搜索请求
	// SearchSourceBuilder 条件构造
	// HighLightBuilder 构建高亮
	// TermQueryBuilder  精确查询
	// XXX QueryBuilder   对应我们其他命令
	@Test
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("jd_goods4");
		//构建搜索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

		//查询条件，我们可以使用 QueryBuliders 工具来实现
		// QueryBuilders.termQuery   精确查询
		// QueryBuilders.matchAllQuery(); 匹配所有
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "qingqu1");

		//MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

		sourceBuilder.query(termQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		System.out.println(JSON.toJSONString(searchResponse.getHits()));
		System.out.println("============================");

		for(SearchHit documentFields:searchResponse.getHits().getHits()){
			System.out.println(documentFields.getSourceAsMap());
		}
	}
}