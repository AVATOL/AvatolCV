package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.List;


public class ScoringProvenanceInfo {
    public enum VoteResultType{
        VOTE_TIE,
        VOTE_MAJORITY,
        VOTE_UNANIMOUS,
        VOTE_NOT_NEEDED
    }
    private static final String SCORE_INFO_ITEM_DELIM = ":";
    private static final String SCORE_INFO_DELIM = ",";
    private static final String VOTE_RESULT_TYPE_DELIM = "-";
    private VoteResultType voteResultType = VoteResultType.VOTE_NOT_NEEDED;
    private List<ScoreInfo> scoreInfos = new ArrayList<ScoreInfo>();
    public void addScoreInfo(String id, String score, String confidence){
        ScoreInfo si = new ScoreInfo(id, score, confidence);
        scoreInfos.add(si);
    }
    public void setScoreinfo(List<ScoreItem> items){
        for (ScoreItem si : items){
            addScoreInfo(si.getImageID(),si.getNewValue().getName(), si.getConfidence());
        }
    }
    public class ScoreInfo{
        private String id = null;
        private String score = null;
        private String confidence = null;
        public ScoreInfo(String id, String score, String confidence){
            this.id = id;
            this.score = score;
            this.confidence = confidence;
        }
        public String getScore(){
            return this.score;
        }
        public String getConfidence(){
            return this.confidence;
        }
        public String getID(){
            return this.id;
        }
    }
    public String toString(){
        if (scoreInfos.size() == 0){
            return voteResultType.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(voteResultType.toString() + VOTE_RESULT_TYPE_DELIM);
        if (scoreInfos.size() > 1){
            for (int i = 0; i < scoreInfos.size() - 1; i++){
                ScoreInfo si = scoreInfos.get(i);
                sb.append(si.getID() + SCORE_INFO_ITEM_DELIM + si.getScore() + SCORE_INFO_ITEM_DELIM + si.getConfidence()+ SCORE_INFO_DELIM);
            }
        }
        ScoreInfo si = scoreInfos.get(scoreInfos.size() - 1);
        sb.append(si.getID() + SCORE_INFO_ITEM_DELIM + si.getScore() + SCORE_INFO_ITEM_DELIM + si.getConfidence());
        return ""+sb;
       
    }
    public void setVoteResultType(VoteResultType type) {
        this.voteResultType = type;
    }
    public String getVoteResultType(){
        return this.voteResultType.toString();
    }
}
