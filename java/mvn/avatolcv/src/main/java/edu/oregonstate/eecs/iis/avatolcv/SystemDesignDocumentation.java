package edu.oregonstate.eecs.iis.avatolcv;

/**
 * 
 * @author admin-jed
 *
 * This is a bogus class designed to let me generate some high level documentation about AvatolCV
 * 
 */
public class SystemDesignDocumentation {

    /**
     *  Pieces of the system:
     *  <ul>
     *     <li>wizard StepSequence, assembled by the the UI layer</li>
     *     <li>data source:  Bisque, Morphobank, local file system</li>
     *     <li>algorithm module(s):  segmentation, orientation, scoring</li>
     *     <li>UI layer: JavaFX Controllers and fxml, each associated with a step</li>
     *  </ul>
     */
    public void highLevelAbstractions(){}
    /**
     * As soon as the DataSource is specified, we use it to ask the questions
     * about <ul><li>dataset</li>
     *           <li>scoring concern</li>
     *           <li>image pull</li>
     *           <li>trainVsTest concern</li>
     *       </ul>.
     * <p>
     * The DataSource also generates normalized versions of metadata which are used by 
     *       <ul>
     *           <li>filter step</li>
     *           <li>quality exclusion step</li>
     *           <li>segmentation</li>
     *           <li>orientation</li>
     *           <li>scoring</li>
     *           <li>results review (results will be generated into the normalized format)</li>
     *       </ul> 
     */
    public void howAreDataSourcesWovenIn(){}
    /**
     * <ol>
     *     <li>ask for data source : Morphobank|Bisque|local file system (asked first so we can later render datasets from that source) </li>
     *     <li>login to data source</li>
     *     <li>ask what data set to focus on? (matrix in MB)  After this, we download as much metadata as we know to pull (batch 1)</li>
     *     <li>what is the focus of the session (presence/absence of parts|shape aspect|texture aspect) and which relevant scoring algorithm do you want to try? (asked now as answer can affect how scoring concern question is asked)</li>
     *     <li>what is the scoring concern?(character for MB, image attribute in Bisque or file system  After this, we pull the batch 2 (final batch) of metadata.  A normalized copy of metadata is generated so that downstream datasource-agnostic logic can use it.</li>
     *     <li>show summary and allow to filter out data from consideration (ex. only consider ventral view for MB)</li>
     *     <li>image pull step (we know the relevant images to pull for this session from prior answers and metadata pulled alread.  It is pulled into a common area for the chosen dataset so that if an image was pulled in a prior session, 
     *     it won't need to be repulled. </li>
     *     <li>exclude images based on quality</li>
     *     <li>trainVsTest screen (pending 9/18/15)</li>
     *     <li>segmentation screen (for running segmentation)</li>
     *     <li>orientation screen (for running segmentation) </li>
     *     <li>scoring screen</li>
     *     <li>results review screen (can get here from home screen for any prior session)</li>
     * </ol>
     */
    public void whatIsTheWizardSequence(){}
   
    /**
     * Metadata pulled from different data sources follow differing schemas.  Morphobank's data is organized around a matrix 
     * (characters vs taxa).  Bisque's (like LocalFileSystem will be) is just attributes of an image.
     * We want as much of our code as possible to key off files that are of a unified (normalized) data format.  So we convert 
     * all data source image metadata into what is essentially a properties file whose filename contains the id of the image in question.
     * Since in the Morphobank case, we have images relevant to multiple "cells" (a cell is the intersection of a character and a taxon).  
     * We don't want to download the image twice, but we need to keep metadata separately for each cell's image, so we adopt 
     * a strategy where we append a one up number as a suffix to the image id.  In the first pass implementation of avatolCV, I used 
     * taxonId+charID+viewID as the name of each file, but in the normalized world, we don't know which property keys are of interest, 
     * and using the ids - we'd need to use all of them, which would lead to crazy huge file names.
     * <p>
     * Certain keys are added by AvatolCV, these keys start with the letters "avcv"
     * <p>
     * For Morphobank case, a cell's metadata will be represented in the normalized image metadata file as a key called taxon with a value 
     * which is the specific taxon.  There's a key/value pair for character/characterIdAndName (format <someID>:<someName>) and
     * a key/value pair for characterStateIdAndName/characterStateValueIdAndName.  
     * Since the ui will need to render the characterIdAndName associated with a characterStateValueIdAndName, it presents a 
     * challenge as these are two separate entries.  To solve this, I introduce the ScoreIndex notion.  ScoreIndex info is stored 
     * in the normalized file with the special avcv prefix.  It specifies which key or value holds the scoreConcern name and the 
     * scoreConcern value.
     */
    public void whatIsANormalizedFile(){} 
    
    /**
     * <ul>
     *    <li>Axis 1: SIMPLE scoring concern vs DEPENDENT scoring concern:  for MB case, in addition to a scoring concern (character) we also 
     * have a training/test concern (taxon) where, depending on the value of the taxon, we will be either training or testing.  I'll 
     * refer to this as the dependent case, where  training and testing decisions are made depending on the value of a property.  
     * For leaf case, we have a simple scoring concern, which is the property chosen from the common properties associated with the 
     * image.   We aren't paying attention to any other properties to decide about training and test, we just train on the images 
     * where the property in question has a value specified.</li>
     *    </li>Axis 2: Algorithm EVALUATION vs ACTUAL_USE use case:  The data that needs to be shown is somewhat different for the two modes on this axis.  
     *    For algorithm evaluation mode, the user has scored everything already and we are going to use that info to help evaluate how an 
     *    algorithm performs.  We divide the data up into "to train" and to score" and then we generate scores and then we can compare the 
     *    scores with the true labels.  There is no "data acceptance" process associated with EVALUATION mode, whereas there is with ACTUAL_USE.</li>
     * </ul>
     */
    public void whatAreTheDifferentModesOfScoring(){}
    
}
