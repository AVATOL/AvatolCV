package edu.oregonstate.eecs.iis.avatolcv;

public class AvatolCVConstants {
    public static final String UNDETERMINED = "?";
    public static final String MORPHOBANK_UNSCORED_ID = "unscored";
    /*
     * NPA is a score that is assigned that means "not sure".  We have to treeat it specially in a number of ways.
     * 
     * First, when looking for issues, such as taxa that have both scored and unscored images, we ignore any marked NPA.
     * We don't want to consider them as classically "unscored", because if the user had 9 images scored and one marked NPA for a taxon, then 
     * if we considered it unscored, the user would be pushed into an error case for having a taxon that is mixed, but they can't fix it. 
     * They've done the best they can with scoring it as NPA.  So, we just leave it out of consideration for all the issue checking. 
     * 
     * It might wind up in a ScoringSet if an NPA image is in a taxon that is otherwise unscored, and this is fine.
     * It can't wind up in a TrainingInfo file (if it was in a trainingSet) because we screen it there.  
     */
    public static final String NPA = "NPA";
}
