package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankAnnotationHelper;
import junit.framework.Assert;
import junit.framework.TestCase;

public class MBAnnotationParseTester extends TestCase {
    public void testParse(){
        String full = "{'ok':true,'annotations':[{'type':'rectangle','points':{'x':'61','y':'54'},'w':'1','h':'2'},{'type':'point','points':{'x':'18','y':'47'}},{'type':'polygon','points':[{'x':'31','y':'19'},{'x':'32','y':'26'},{'x':'45','y':'73'}]}]}";
        String justTypes = "{'type':'rectangle','points':{'x':'61','y':'54'},'w':'1','h':'2'},{'type':'point','points':{'x':'18','y':'47'}},{'type':'polygon','points':[{'x':'31','y':'19'},{'x':'32','y':'26'},{'x':'45','y':'73'}]}";
        String type1 = "{'type':'rectangle','points':{'x':'61','y':'54'},'w':'1','h':'2'}";
        String type2 = "{'type':'point','points':{'x':'18','y':'47'}}";
        String type3 = "{'type':'polygon','points':[{'x':'31','y':'19'},{'x':'32','y':'26'},{'x':'45','y':'73'}]}";
        String justTypesAttempt = MorphobankAnnotationHelper.getJustTypes(full);
        System.out.println(justTypesAttempt);
        Assert.assertEquals(justTypes, justTypesAttempt);
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{1}"),"{1}");
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{1},{2}"),"{1}");
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{1},{2},{3}"),"{1}");
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{11},{22}"),"{11}");
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{111},{222}"),"{111}");
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{{1}},{{2}}"),"{{1}}");
        Assert.assertEquals(MorphobankAnnotationHelper.getFirstAnnotationJson("{{{1}}},{{{2}}}"),"{{{1}}}");
        List<String> parts = MorphobankAnnotationHelper.splitTypes(justTypes);
        Assert.assertEquals(parts.get(0), type1);
        Assert.assertEquals(parts.get(1), type2);
        Assert.assertEquals(parts.get(2), type3);
    }
    
}
