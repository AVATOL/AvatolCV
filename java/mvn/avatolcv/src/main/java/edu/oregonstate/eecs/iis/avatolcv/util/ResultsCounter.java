package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResultsCounter {
    public static void main(String[] args){
        ResultsCounter rc = new ResultsCounter();
        rc.genOutput("C:\\Users\\admin-jed\\Downloads\\results_apex_angle_RW.csv");
        rc.genOutput("C:\\Users\\admin-jed\\Downloads\\results_apex_curvature_RW.csv");
        rc.genOutput("C:\\Users\\admin-jed\\Downloads\\results_base_angle_RW.csv");
        rc.genOutput("C:\\Users\\admin-jed\\Downloads\\results_base_curvature_RW.csv");
        
    }
    public void genOutput(String path){
        System.out.println("percentCorrect for threshold for " + path);
        List<ScoreInfo> infos = new ArrayList<ScoreInfo>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            while (null != (line = reader.readLine())){
                String[] parts = ClassicSplitter.splitt(line, ',');
                String conf = parts[3];
                String eval = parts[5];
                if (!eval.equals("?")){
                    //System.out.println("conf " + conf + "  eval " + eval);
                    ScoreInfo si = new ScoreInfo(conf, eval);
                    infos.add(si);
                }
            }
            reader.close();
            int curThreshold = 50;
            while (curThreshold < 100){
                double thresholdDouble = (double)curThreshold / 100;
                List<ScoreInfo> aboveThresholdList = getInfosAboveThreshold(thresholdDouble, infos);
                int numberCorrect = getNumberCorrect(aboveThresholdList);
                int numberIncorrect = getNumberIncorrect(aboveThresholdList);
                double percentCorrect = getPercentCorrect(aboveThresholdList);
                String correct = String.format( "%.2f", percentCorrect );
                String confString = String.format( "%.2f", thresholdDouble );
                //System.out.println("for confidence " + confString + "  percentCorrect " + correct + " of " + aboveThresholdList.size() + " (out of " + infos.size() + " scorable images)");
                System.out.println(confString + "," + numberCorrect + "," + numberIncorrect);
                curThreshold += 5;
            }
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }
    public int getNumberIncorrect(List<ScoreInfo> infos){
        int correctCount = getNumberCorrect(infos);
        int total = infos.size();
        int incorrectCount = total - correctCount;
        return incorrectCount;
    }
    public int getNumberCorrect(List<ScoreInfo> infos){
        int correctCount = 0;
        for (ScoreInfo si : infos){
            if (si.isCorrect()){
                correctCount++;
            }
        }
        return correctCount;
    }
    public List<ScoreInfo> getInfosAboveThreshold(double t,  List<ScoreInfo> infos){
        List<ScoreInfo> result = new ArrayList<ScoreInfo>();
        for (ScoreInfo si : infos){
            if (si.isAboveThreshold(t)){
                result.add(si);
            }
        }
        return result;
    }
    public double getPercentCorrect(List<ScoreInfo> infos){
        double correctCount = 0;
        double badCount = 0;
        for (ScoreInfo si : infos){
            if (si.isCorrect()){
                correctCount++;
            }
            else {
                badCount++;
            }
        }
        double total = correctCount + badCount;
        return correctCount / total;
    }
    public class ScoreInfo {
        private double confidence = 0;
        private boolean correct = false;
        public ScoreInfo(String conf, String eval){
            Double d = new Double(conf);
            confidence = d.doubleValue();
            if (eval.equals("correct")){
                correct = true;
            }
        }
        public boolean isCorrect(){
            return correct;
        }
        public boolean isAboveThreshold(double threshold){
            return confidence > threshold;
        }
    }
}
