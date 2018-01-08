package com.test.search;


	import java.io.StringReader;

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

import com.beifeng.sooba.util.Constant;

	public class TestHighLight {
		

		public static void main(String[] args) throws Exception{
			
			Searcher searcher = new IndexSearcher(Constant.indexDir);

			PaodingAnalyzer analyzer=new PaodingAnalyzer();
			String field = "contents";
			String queryStr = "��������";
			
			 QueryParser parser = new QueryParser(field, analyzer);
			 Query query = parser.parse(queryStr);
		        
			 TopDocCollector  collector = new TopDocCollector(10);
	        
			 searcher.search(query,collector);
			 ScoreDoc[] hits =collector.topDocs().scoreDocs;
			 
			 //������ʾ����
			 Highlighter highlighter = null;
					
			 SimpleHTMLFormatter simpleHTMLFormatter=new SimpleHTMLFormatter("<font color='red'><b>","</b></font>");  

		
			 Highlighter highlighterTitle = null;
				
			 SimpleHTMLFormatter formatTitle=new SimpleHTMLFormatter("<FONT color=red>","</FONT>");  

			 
			 
			 highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query)); 
			 highlighterTitle = new Highlighter(formatTitle,new QueryScorer(query)); 
			 highlighter.setTextFragmenter(new SimpleFragmenter(200));//���200��ָ���ؼ����ַ�����context
	                                     //�ĳ��ȣ�������Լ��趨����Ϊ�����ܷ�����ƪ��������
			 Document doc;
			 for(int i=0;i<hits.length;i++){
				 
				 System.out.println(hits[i].doc);
				 System.out.println(hits[i].score);
				//��ӡ�ĵ�������
				 doc = searcher.doc(hits[i].doc);
//				 System.out.println(doc.toString());

					 
				//��������ʾ
				 
				 TokenStream tokenStream =new PaodingAnalyzer().tokenStream("fileName", new StringReader(doc.get("fileName")));
				 System.out.println(highlighterTitle.getBestFragment(tokenStream,doc.get("fileName")+"<br>"));

				 
					 System.out.println("--------------------");
				
				tokenStream =new PaodingAnalyzer().tokenStream("contents", new StringReader(doc.get("contents")));
				System.out.println(highlighter.getBestFragment(tokenStream,doc.get("contents"))+"<br>");
				
			
			 }
			

	        
		}
		
		

	}
