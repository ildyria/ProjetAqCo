package Bench;

import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import Expected.Result;

public class BenchCalculus {
	static BenchData Calculate(TopDocs topdoc, Result result, int numFiles){
		BenchData res = null;
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;

		ScoreDoc[] hits = topdoc.scoreDocs;
		List<Integer> temp_files = result._files;
		
		for(int i = 0; i < hits.length; ++i)
		{
			Integer toFind = hits[i].id;
			if(temp_files.contains(toFind))
			{
				temp_files.remove(toFind);
				tp++;
			}
			else
			{
				fp++;
			}
		}
		fn = temp_files.size();
		tn = numFiles - (tp + fp + fn); 
		
		res = new BenchData(((Integer)result._num).toString(), tp, fp, fn, tn);
		
		return res;
	}
}
