package edu.oregonstate.eecs.iis.avatolcv.results;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestScoringProvenanceInfo extends TestCase {
    public void testCaseTie(){
        ScoringProvenanceInfo spi = new ScoringProvenanceInfo();
        spi.addScoreInfo("id1", "score1", "conf1");
        spi.addScoreInfo("id2", "score2", "conf2");
        spi.setVoteResultType(ScoringProvenanceInfo.VoteResultType.VOTE_TIE);
        Assert.assertEquals("VOTE_TIE-id1:score1:conf1,id2:score2:conf2",spi.toString());
    }
    public void testCaseMajority(){
        ScoringProvenanceInfo spi = new ScoringProvenanceInfo();
        spi.addScoreInfo("id1", "score1", "conf1");
        spi.addScoreInfo("id2", "score2", "conf2");
        spi.addScoreInfo("id3", "score1", "conf3");
        spi.setVoteResultType(ScoringProvenanceInfo.VoteResultType.VOTE_MAJORITY);
        Assert.assertEquals("VOTE_MAJORITY-id1:score1:conf1,id2:score2:conf2,id3:score1:conf3",spi.toString());
    }
    public void testCaseUnanimous(){
        ScoringProvenanceInfo spi = new ScoringProvenanceInfo();
        spi.addScoreInfo("id1", "score1", "conf1");
        spi.addScoreInfo("id2", "score1", "conf2");
        spi.addScoreInfo("id3", "score1", "conf3");
        spi.setVoteResultType(ScoringProvenanceInfo.VoteResultType.VOTE_UNANIMOUS);
        Assert.assertEquals("VOTE_UNANIMOUS-id1:score1:conf1,id2:score1:conf2,id3:score1:conf3",spi.toString());
    }
}
