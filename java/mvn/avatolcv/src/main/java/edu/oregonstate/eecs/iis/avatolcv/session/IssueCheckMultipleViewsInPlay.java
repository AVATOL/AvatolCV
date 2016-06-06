package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class IssueCheckMultipleViewsInPlay implements IssueCheck {
    private static final char NL = '\n';
    private List<NormalizedImageInfo> niis = null;
    private List<NormalizedKey> scoringConcernKeys = null;
    public IssueCheckMultipleViewsInPlay(List<NormalizedImageInfo> niis, List<NormalizedKey> scoringConcernKeys){
        this.niis = niis;
        this.scoringConcernKeys = scoringConcernKeys;
    }
    @Override
    public List<DataIssue> runIssueCheck() throws AvatolCVException {
        List<String> viewsSeen = new ArrayList<String>();
        NormalizedKey viewKey = new NormalizedKey("view");
        for (NormalizedKey scoringConcernKey : this.scoringConcernKeys){
            for (NormalizedImageInfo nii : niis){
                if (!nii.isExcluded()){
                    if (nii.hasKey(scoringConcernKey)){
                        if (nii.hasKey(viewKey)){
                            NormalizedValue viewValue = nii.getValueForKey(viewKey);
                            String viewName = viewValue.getName();
                            if (!viewsSeen.contains(viewName)){
                                viewsSeen.add(viewName);
                            }
                        }
                    }
                }
            }
        }
        List<DataIssue> dataIssues = new ArrayList<DataIssue>();
        if (viewsSeen.size() > 1){
            DataIssue di = new DataIssue();
            StringBuilder sb = new StringBuilder();
            sb.append("multiple views in play :  ");
            for (String view : viewsSeen){
                sb.append(view + "  ");
            }
            sb.append(NL);
            di.setDescription("" + sb);
            di.addActionOption("At the filter screen, filter out all but one view.  AvatolCV is not yet smart enough to work with different views per character during a scoring run");
            di.setType(getIssueType());
            dataIssues.add(di);
        }
        return dataIssues;
    }

    @Override
    public String getIssueType() {
        return this.getClass().getName();
    }

}
