package com.beifeng.sooba.search;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import com.beifeng.sooba.util.Constant;

public class AdvanceGetSearcheResult {
     
	private String queryStr;
	private  int eachePageNum = 10;//ÿҳ��ʾ�ĸ���
	private String docType;
	private String qtype;
	private  int totalNum = 100;
	public AdvanceGetSearcheResult(int eachePageNum,String docType,String qtype,String queryStr,int totalNum){
		this.eachePageNum=eachePageNum;
		this.docType=docType;
		this.qtype=qtype;
		this.queryStr=queryStr;
		this.totalNum=totalNum;
	}
	
	/**
	 * 
	 * @param queryStr
	 * @param headSearchNum �õ���ѯ�Ŀ�ǰ�Ľ����
	 * @return
	 * @throws Exception
	 */
	public ScoreDoc[] getScoreDocs()
			throws Exception {

		Searcher searcher = new IndexSearcher(Constant.indexDir);
		TopDocCollector collector = new TopDocCollector(this.totalNum);
		Query query = this.getQuery();
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		return hits;
	}

	public Query getQuery() throws Exception {
		PaodingAnalyzer analyzer = new PaodingAnalyzer();
		String field = "contents";
		Query query =null;
		
		BooleanQuery booleanQuery=new BooleanQuery();
		
		if(this.qtype.equals("term")){
			QueryParser parser = new QueryParser(field, analyzer);
			query = parser.parse(queryStr);
			
		}else if("fuzz".equals(this.qtype)){
			Term term=new Term(field,queryStr);
			 query=new FuzzyQuery(term);
		}else {
			Term term=new Term(field,queryStr);
			query=new PrefixQuery(term);
		}
		if(!"all".equals(this.docType)){
			Term typeTerm=new Term("type",this.docType);
			TermQuery typeQuery=new TermQuery(typeTerm);
			booleanQuery.add(typeQuery, BooleanClause.Occur.MUST);
		}

//		System.out.println("--this.docType---"+this.docType);
		booleanQuery.add(query, BooleanClause.Occur.MUST);

		return booleanQuery;
	}

	public List<DocumentEntity> getResult(int currentPageNum)
			throws Exception {

		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
		Searcher searcher = new IndexSearcher(Constant.indexDir);

		//������ʾ����
		Highlighter highlighter = null;

		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(
				"<font color='red'><b>", "</b></font>");

		Highlighter highlighterTitle = null;

		SimpleHTMLFormatter formatTitle = new SimpleHTMLFormatter(
				"<FONT color=#c60a00>", "</FONT>");

		ScoreDoc[] hits = this.getScoreDocs();
		Query query = this.getQuery();
		highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(
				query));
		highlighterTitle = new Highlighter(formatTitle, new QueryScorer(query));
		highlighter.setTextFragmenter(new SimpleFragmenter(200));//���200��ָ���ؼ����ַ�����context
		//�ĳ��ȣ�������Լ��趨����Ϊ�����ܷ�����ƪ��������
		Document doc;
        String fileName="";
		int totalNumber = currentPageNum * eachePageNum;
		if (totalNumber > hits.length)
			totalNumber = hits.length;
		for (int i = (currentPageNum - 1) * eachePageNum; i < totalNumber; i++) {
            
			//��ӡ�ĵ�������
			doc = searcher.doc(hits[i].doc);
			//			 System.out.println(doc.toString());
//            if(this.docType.equals(doc.get("type"))){
			//��������ʾ
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
					fileName);//��Ҫע�⣺�ڴ���ʱ����ı���������в�������Ӧ�Ĺؼ��ַ���һ��null
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
		
//		}//end for if
//            else continue;
		}//end for 
		return list;
	}

	
}
