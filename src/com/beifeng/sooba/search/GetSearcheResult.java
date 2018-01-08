package com.beifeng.sooba.search;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.junit.Test;

import com.beifeng.sooba.util.Constant;

public class GetSearcheResult {

	public static int eachePageNum = 5;//每页显示的个数
	/**
	 * 
	 * @param queryStr
	 * @param headSearchNum 得到查询的靠前的结果数
	 * @return
	 * @throws Exception
	 */
	public ScoreDoc[] getScoreDocs(String queryStr, int headSearchNum)
			throws Exception {

		Searcher searcher = new IndexSearcher(Constant.indexDir);
		TopDocCollector collector = new TopDocCollector(headSearchNum);
		Query query = this.getQuery(queryStr, headSearchNum);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		return hits;
	}

	public Query getQuery(String queryStr, int headSearchNum) throws Exception {
		PaodingAnalyzer analyzer = new PaodingAnalyzer();
		String field = "contents";
		QueryParser parser = new QueryParser(field, analyzer);
		Query query = parser.parse(queryStr);
		return query;
	}

	public List<DocumentEntity> getResult(String queryStr, int currentPageNum,int topNum)
			throws Exception {

		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
		Searcher searcher = new IndexSearcher(Constant.indexDir);

		//高亮显示设置
		Highlighter highlighter = null;

		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
				"<font color='red'><b>", "</b></font>");

		Highlighter highlighterTitle = null;

		SimpleHTMLFormatter formatTitle = new SimpleHTMLFormatter(
				"<FONT color=#c60a00>", "</FONT>");

		ScoreDoc[] hits = this.getScoreDocs(queryStr, topNum);
		Query query = this.getQuery(queryStr, topNum);
		highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(
				query));
		highlighterTitle = new Highlighter(formatTitle, new QueryScorer(query));
		highlighter.setTextFragmenter(new SimpleFragmenter(200));//这个200是指定关键字字符串的context
		//的长度，你可以自己设定，因为不可能返回整篇正文内容
		Document doc;
        String fileName="";
		int totalNumber = currentPageNum * eachePageNum;
		if (totalNumber > hits.length)
			totalNumber = hits.length;
		for (int i = (currentPageNum - 1) * eachePageNum; i < totalNumber; i++) {

			//打印文档的内容
			doc = searcher.doc(hits[i].doc);
			//			 System.out.println(doc.toString());

			//高亮出显示
			DocumentEntity docEntity = new DocumentEntity();
			TokenStream tokenStream = new PaodingAnalyzer().tokenStream(
					"contents", new StringReader(doc.get("contents")));
			docEntity.setContents(highlighter.getBestFragment(tokenStream, doc
					.get("contents")));
//			 System.out.println("----------"+i+"----------");
//			 System.out.println(docEntity.getContents());
			fileName=doc.get("fileName");
			tokenStream = new PaodingAnalyzer().tokenStream("fileName",
		    			new StringReader(fileName));
			String forMatt=highlighterTitle.getBestFragment(tokenStream,
					fileName);//需要注意：在处理时如果文本检索结果中不包含对应的关键字返回一个null
			if(forMatt==null)
				docEntity.setFilename(fileName);
			else docEntity.setFilename(forMatt);
			
			
		    String type1=doc.get("type");
			docEntity.setType(type1);
			docEntity.setId(doc.get("id"));
			if("pdf".equalsIgnoreCase(type1)){
				
				fileName=Constant.CONTEXT+Constant.PDFdir+fileName+"."+type1;
				docEntity.setOriginalFileName(fileName);
			}
			else if("doc".equalsIgnoreCase(type1))
			{
				fileName=Constant.CONTEXT+Constant.Docdir+fileName+"."+type1;
				docEntity.setOriginalFileName(fileName);
				
			}
		
			//  System.out.println(docEntity.getType());

			list.add(docEntity);
//			   System.out.println(docEntity.getFilename());
//			   System.out.println("--------------------"+doc.get("fileName"));
		}
		return list;
	}

	@Test
	public  void test1()throws Exception{
		this.getResult("挖掘", 2,200);
		
	}
}
