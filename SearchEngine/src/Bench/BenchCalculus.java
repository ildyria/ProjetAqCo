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
    	System.out.println("" + result._num + " : ");
    	System.out.print("expected : ");
    	if(temp_files.size() == 0)
    	{
    		System.out.println("no results");
    	}
    	else
    	{
        	for(int i = 0; i < temp_files.size(); ++i)
        	{
        		System.out.print(temp_files.get(i) + " ");
        	}
        	System.out.println();
    	}

    	System.out.print("returned : ");
		for(int i = 0; i < hits.size(); ++i)
		{
			Integer toFind = hits.get(i).doc;
			System.out.print(hits.get(i).doc + " ");
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
		System.out.println();
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
		int nb_precision = 0;
		for(int i = 0; i < data.size(); ++i)
		{
			error_rate += data.get(i).get_error_rate();
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
		stats.put("recall", recall / nb_recall );
		stats.put("error rate", error_rate / data.size());
		
		return stats;
	}
}
