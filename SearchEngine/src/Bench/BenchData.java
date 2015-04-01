package Bench;

public class BenchData {
	public String _name;
	public int _tp; // ceux qu'on a bien trouvé
	public int _fp; // ceux qui ont été trouvé mais qui ne conviennent pas
	public int _fn; // ceux qu'on aurait du trouver et qu'on a manqué
	public int _tn; // ceux qu'on a bien éliminé

	public BenchData(String n, int tp, int fp, int fn, int tn)
	{
		_name = new String(n);
		_tp = tp;
		_fp = fp;
		_fn = fn;
		_tn = tn;
	}

	public double get_precision(){ // what percent of what we got is what we wanted
		return (_tp + _fp != 0) ? (_tp/(double)(_tp + _fp))*100 : 0.;
	}

	public double get_recall(){ // what we got over what was expected
		return (_tp + _fn != 0) ? (_tp/(double)(_tp + _fn))*100 : 999;
	}

	public double get_error_rate(){ // what is our error rate (fn & fp)/total
		return ((_fp + _fn)/(double)(_fn + _tp + _fp + _tn))*100;
	}

	@Override
	public String toString(){
		return _name + " -  precision : " + String.format("%.3g", get_precision()) + "%, " +
						"sensitivity : " + String.format("%.3g", get_recall()) + "%, " +
						"error rate : " + String.format("%.3g", get_error_rate()) + "%.";
	}
}
