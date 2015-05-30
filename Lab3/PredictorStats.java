
/* For Homework #3 you will compare a variety of branch predictors.
 * You should implement all of the predictors in their various class files.
 * For whichever predictor your code instatiates during a particular run, 
 * you maintain the statistics for that predictor with an instance of THIS class.
 *
 *  DO NOT MODIFY THIS FILE!!
 */
public class PredictorStats {

    long total_predictions;
    long total_correct_predictions;
    double correct_prediction_rate;
    String predictorType = "";
    String heuristic = "";
    
    public PredictorStats(String predictorType, String heuristic) {
	total_predictions = 0;
	total_correct_predictions = 0;
	correct_prediction_rate = 0.0;
	this.predictorType = predictorType;
	this.heuristic = heuristic;
    }
    
    public long totalIncorrect() {
	return total_predictions - total_correct_predictions;
    }
    
    /* 
     * You need to figure out when to call THIS function.
     */
    public void updateStat(boolean predictedCorrectly) {
    	if (predictedCorrectly)
	    total_correct_predictions++;
    	total_predictions++;
    }	
    
    /*
     * calculate the rate one time when the simulation is completed
     */
    public void calculateRate(){
    	correct_prediction_rate = total_correct_predictions/(double)total_predictions;
    }       
    
    public void print() {
    	System.out.format("** %s predictor, predicting %s ** \nPrediction Accuracy: %.2f%% (Mis-prediction rate: %.2f%%)\n", 
			  predictorType, heuristic, correct_prediction_rate*100.0, (1-correct_prediction_rate)*100.0);
    }
}
