package Bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import Expected.Result;

public class BenchCalculus {

	public static BenchData Calculate(TopDocs topdoc, Result result, int numFiles){
		BenchData res = null;
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;

		ScoreDoc[] hits = topdoc.scoreDocs;
		List<Integer> temp_files = result._files;
		
		for(int i = 0; i < hits.length; ++i)
		{
			Integer toFind = hits[i].doc;
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
		System.out.println(res.toString());
		return res;
	}
	
	public static HashMap<String, Double> Moyenne(ArrayList<BenchData> data)
	{
		HashMap<String, Double> stats = new HashMap<String, Double>();
		double precision = 0;
		double recall = 0;
		double error_rate = 0;
		int nb_recall = 0;
		for(int i = 0; i < data.size(); ++i)
		{
			precision += data.get(i).get_precision();
			error_rate += data.get(i).get_error_rate();
			if(data.get(i).get_recall() != 999)
			{
				recall += data.get(i).get_recall();
				nb_recall++;
			}
		}

		stats.put("precision", precision / data.size());
		stats.put("recall", recall / nb_recall );
		stats.put("error rate", error_rate / data.size());
		
		return stats;
	}
}
