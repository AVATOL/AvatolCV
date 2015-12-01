package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

//character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
//taxon=773126|Artibeus jamaicensis
//view=8905|Skull - ventral annotated teeth
public class TestNormalizedTypeIDName extends TestCase {
    
    // type:id|name
    public void testAllPresent(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("type:id|name");
            Assert.assertEquals("type", tin.getType());
            Assert.assertEquals("id", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    
    // :id|name
    public void testNoType1(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName(":id|name");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("id", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // type:|name
    public void testNoID1(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("type:|name");
            Assert.assertEquals("type", tin.getType());
            Assert.assertEquals("ID_name", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // type:id|
    public void testNoName(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("type:id|");
            Assert.assertEquals("type", tin.getType());
            Assert.assertEquals("id", tin.getID());
            Assert.assertEquals("NAME_id", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // name
    public void testNameOnly(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("name");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("ID_name", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // |name  (derive id)
    public void testNoTypeOrID1(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("|name");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("ID_name", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // :name  (assume name if there is no |)
    public void testNoTypeOrID2(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName(":name");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("ID_name", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // id|name
    public void testNoType2(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("id|name");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("id", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // type:name (assume name if there is no |
    public void testNoID2(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("type:name");
            Assert.assertEquals("type", tin.getType());
            Assert.assertEquals("ID_name", tin.getID());
            Assert.assertEquals("name", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // id|  (derive name)
    public void testNoTypeOrName1(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("id|");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("id", tin.getID());
            Assert.assertEquals("NAME_id", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    // :id|  (derive name)
    public void testNoTypeOrName2(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName(":id|");
            Assert.assertEquals(NormalizedTypeIDName.TYPE_UNSPECIFIED, tin.getType());
            Assert.assertEquals("id", tin.getID());
            Assert.assertEquals("NAME_id", tin.getName());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    
    // type: (error if no id or name)
    public void testNoIDOrName1(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("type:");
            Assert.fail("should have thrown exception on case where no id or name given");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
    }
 // type: (error if no id or name)
    public void testNoIDOrName2(){
        try {
            NormalizedTypeIDName tin = new NormalizedTypeIDName("type:|");
            Assert.fail("should have thrown exception on case where no id or name given");
        }
        catch(AvatolCVException ace){
            Assert.assertTrue(true);
        }
    }
    
}
