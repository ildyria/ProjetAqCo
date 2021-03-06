package Bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.search.ScoreDoc;

import Expected.ResultsParser;

public class Benchmark {
	public HashMap<String, Double> _bench;
	ArrayList<BenchData> _datas;
	ResultsParser _expected;
	
	public Benchmark(Map<Integer, List<ScoreDoc>> queryResults){
		_expected = new ResultsParser("CISIDonnees/CISI.REL",queryResults.size());
		_datas = new ArrayList<BenchData>();
		_bench = new HashMap<String, Double>();
//		System.out.println("entries : " + _expected._entries.size());
		Iterator<Entry<Integer, List<ScoreDoc>>> it = queryResults.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<Integer, List<ScoreDoc>> pair = it.next();
	    	_datas.add(BenchCalculus.Calculate(pair.getValue(), _expected._entries.get(pair.getKey()-1), 1460));
	    }
		
	    _bench = BenchCalculus.Moyenne(_datas);
	}
	
	@Override
	public String toString(){
//		return "precision : " + String.format("%.3g", _bench.get("precision")) +  "%, " +
//				"recall : " + String.format("%.3g", _bench.get("recall")) + "%, " +
//				"specificity : " + String.format("%.4g", _bench.get("specificity")) + "%, " +
//				"error rate : " + String.format("%.3g", _bench.get("error rate")) + "% " +
//				"false reject : " + String.format("%.3g", _bench.get("false")) + "%." +
//		"\nv2 : \n"
//		+ "precision : " + String.format("%.3g", _bench.get("v2_precision")) +  "%, " +
//		"recall : " + String.format("%.3g", _bench.get("v2_recall")) + "%, " +
//		"specificity : " + String.format("%.4g", _bench.get("v2_specificity")) + "%, " +
//		"error rate : " + String.format("%.3g", _bench.get("v2_error rate")) + "% " +
//		"false reject : " + String.format("%.3g", _bench.get("v2_false")) + "%.";

		return 	String.format("%.3g", _bench.get("v2_precision")) +  " ; " +
				String.format("%.3g", _bench.get("v2_recall")) + " ; " +
				String.format("%.4g", _bench.get("v2_specificity")) + " ; " +
				String.format("%.3g", _bench.get("v2_error rate")) + " ; " +
				String.format("%.3g", _bench.get("v2_false"));
	}
	
	public String max_precision(){
		return String.format("%.3g", _bench.get("max_precision"));
	}

	public String max_recall(){
		return String.format("%.3g", _bench.get("max_recall"));
	}
}
