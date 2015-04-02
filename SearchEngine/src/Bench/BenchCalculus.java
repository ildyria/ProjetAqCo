package Bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;

import Expected.Result;

public class BenchCalculus {

	public static BenchData Calculate(List<ScoreDoc> hits, Result result, int numFiles){
		BenchData res = null;
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;

		List<Integer> temp_files = result._files;

		for(int i = 0; i < hits.size(); ++i)
		{
			Integer toFind = hits.get(i).doc;
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
		
		res = new BenchData(((Integer)result._num).toString(), tp, fp, fn, tn, temp_files.size());
		return res;
	}
	
	public static HashMap<String, Double> Moyenne(ArrayList<BenchData> data)
	{
		HashMap<String, Double> stats = new HashMap<String, Double>();
		double precision = 0;
		double recall = 0;
		double error_rate = 0;
		double specificity = 0;
		double false_reject = 0;
		int nb_recall = 0;
		int nb_precision = 0;
		
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;
		
		for(int i = 0; i < data.size(); ++i)
		{
			tp += data.get(i)._tp;
			fp += data.get(i)._fp;
			fn += data.get(i)._fn;
			tn += data.get(i)._tn;
			
			error_rate += data.get(i).get_error_rate();
			specificity += data.get(i).get_specificity();
			false_reject += data.get(i).get_false_reject();
			if(data.get(i).get_recall() != 999)
			{
				recall += data.get(i).get_recall();
				nb_recall++;
			}
			if(data.get(i).get_precision() != 999)
			{
				precision += data.get(i).get_precision();
				nb_precision++;
			}
		}

		
		stats.put("precision", precision / nb_precision);
		stats.put("specificity", specificity / data.size() );
		stats.put("recall", recall / nb_recall );
		stats.put("error rate", error_rate / data.size());
		stats.put("false", false_reject / data.size());

		stats.put("v2_precision", (tp + fp != 0) ? (tp/(double)(tp + fp))*100 : 0);
		stats.put("v2_specificity", (tn/(double)(tn + fp))*100);
		stats.put("v2_recall", (tp/(double)(tp + fn))*100);
		stats.put("v2_error rate", ((fp + fn)/(double)(fn + tp + fp + tn))*100);
		stats.put("v2_false", (fn/(double)(tn + fn))*100);
		
		return stats;
	}
}
