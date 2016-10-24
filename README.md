Additional 
# AvatolCV
NOTE: This material is based upon work supported by the National Science Foundation under Grant No. 1208272. 
Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) 
and do not necessarily reflect the views of the National Science Foundation.

This is the source code repository for AvatolCV, a desktop Java application for running biological image data 
through computer vision algorithms for the purpose of scoring morphologic characters.

# Personnel
This system was developed at Oregon State University, School of EECS, Machine Learning Group by

- Jed Irvine - primary software developer
- Michael Lam - highClutterSegmenter algorithm
- Yao Zhou - basicSegmenter, basicOrientation and shapeTextureScoring algorithms
- Shell Hu - partsScoring algorithm
- Behrooz Mahasseni - algorithm integration support
- Sinisa Todorovic - Associate Professor of Computer Science, Computer Vision, co-Principal Investigator
- Thomas Dietterich, Distinguished Professor and Director of Intelligent Systems Research, co-Principal Investigator

in collaboration with 

- Maureen O'Leary, Stony Brook University - co-Principal Investigator and Morphobank integration support
- Maria Passarotti, Whirl-i-gig.com - Morphobank Web Services integration support
- Andrea Cirranello, American Museum Of Natural History - bat skull dataset
- Ramona Walls, CyVerse, University of Arizona - leaf dataset and BisQue integration support
- Kristian Kvilekval, University of California Santa Barbara - BisQue Web Services integration support
- Meg Daly, Ohio State University - nematocyst dataset

Additional National Science Foundation grants supporting the AVAToL project

- DEB 1208270 to M.A. O’Leary
- DEB 1208523 to M. Daly
- DEB 1208845 to D.W. Stevenson
- DEB 1208306 to N. B. Simmons
- DEB 1208619 to E. C. Theriot. 

To install an AvatolCV distribution, visit https://github.com/AVATOL/AvatolCVInstaller and consult the README file there.

# Documentation
The documentation available with this source code is located in the avatol_cv/docs directory:

- AvatolCVUsersGuide.pdf
- AvatolCVDeveloperDocumentation.pdf
- AddingNewAlgorithmsToAvatolCV.pdf
- AvatolCVAlgorithmsDescriptions.txt

# Subsystems
The big pieces of AvatolCV are:
- UI which guides user through a session wizard-style
- DataSource api with two implementations provided: 
      - MorphobankDataSource (interfaces with morphobank.org)
      - BisqueDataSource  (interfaces with http://bovary.iplantcollaborative.org/client_service)
- Algorithms api allowing segmentation, orientation, and character scoring algorithms to be hooked in
- Five research-grade algorithms are provided with AvatolCV
      - Segmentation:  basicSegmenter (segments simple leaf images)
      - Orientation:   basicOrientation (orients simple leaf images)
      - Scoring:       shapeTextureScoring (scores certain leaf shape aspect characters)
      - Segmentation:  highClutterSegmenter (segments specimens in high clutter environments)
      - Scoring:       partsScoring (scores presence/absence of small parts, such as teeth in a skull)
- A results reviewer
      
# The java code landscape

The java packages contained in AvatolCV are:

- edu.oregonstate.eecs.iis.avatolcv.algorithm // classes that encapsulate algorithm related concerns
- edu.oregonstate.eecs.iis.avatolcv.core   // a few core files
- edu.oregonstate.eecs.iis.avatolcv.css // css stylesheets for the JavaFX UI
- edu.oregonstate.eecs.iis.avatolcv.datasource // encapsulates sources of image data (Morphobank, BisQue) and interactions therewith
- edu.oregonstate.eecs.iis.avatolcv.javafxui // includes AvatolCVJavaFX.java which has main().  These files could be moved to the ui.javafx package
- edu.oregonstate.eecs.iis.avatolcv.normalized // all the classes dealing with AvatolCV's normalized data format- edu.oregonstate.eecs.iis.avatolcv.results // classes supporting the ResultsViewer screen
- edu.oregonstate.eecs.iis.avatolcv.scoring // classes supporting scoring prep mechanics and nuances
- edu.oregonstate.eecs.iis.avatolcv.session // classes supporting the notion of a scoring session and issues that arise therein
- edu.oregonstate.eecs.iis.avatolcv.steps // each class represents the data behind each screen in the wizard
- edu.oregonstate.eecs.iis.avatolcv.tools // some classes supporting editing of datasets that have been pulled in
- edu.oregonstate.eecs.iis.avatolcv.tutorial // now obsolete
- edu.oregonstate.eecs.iis.avatolcv.ui.javafx // JavaFX controllers for each screen in wizard, plus the corresponding .fxml files
- edu.oregonstate.eecs.iis.avatolcv.util // various utilities - some standalone, some used in the codebase
- edu.oregonstate.eecs.iis.avatolcv.ws // Web Service clients for Morphobank and Bisque
- edu.oregonstate.eecs.iis.avatolcv.ws.bisque // classes for unmarshalling data that flows in the BisQue Web Service conversation
- edu.oregonstate.eecs.iis.avatolcv.ws.morphobank // classes for unmarshalling data that flows in the Morphobank Web Service conversation

# To Continue Development
If using eclipse, after cloning, import this as an existing maven project:

- avatol_cv/java/mvn/avatolcv

To start AvatolCV, run the following class:

- edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVJavaFX.java
 
# Overview of Key Files
Documentation

- avatol_cv/docs/\*   

The executable jar file for AvatolCV (java -jar avatol_cv.jar)

- avatol_cv/java/lib/avatol_cv.jar   

License files for 3rdParty code

- avatol_cv/license/\*

Algorims and depended upon libraries live in the modules directory

- avatol_cv/modules\*

3rdParty libraries for the basicSegmenter need to live here (Mac only).  They are not in this repo and need to be installed. See avatol_cv/modules/Darwin_Library_Installation_Instructions_MACOS.pdf for instructions 

- avatol_cv/modules/3rdParty/darwin
- avatol_cv/modules/3rdParty/libsvm
- avatol_cv/modules/3rdParty/vlfeat

highClutterSegmenter lives in another repo (https://github.com/AVATOL/nematocyst) and needs to be installed as per this file, if it is to be run (highClutterSegmenter does not currently hook into either functioning scoring pipelines):

- avatol_cv/modules/hcSearch_Installation_Instructions.txt

Orientation algorithms live under this directory:

- avatol_cv/modules/orientation

The basicOrientation algorithm is included in this AvatolCV repo (yaoOrient) and is integrated with AvatolCV via this file:

- avatol_cv/modules/orientation/yaoOrient/algPropertiesMac.txt

These three files document the relationship between algProperties files and runConfig files:

- avatol_cv/modules/runConfigSpecForOrientation.txt
- avatol_cv/modules/runConfigSpecForScoring.txt
- avatol_cv/modules/runConfigSpecForSegmentation.txt

This file captures the specification of what form scoring algorithms must put output data so that it can be read by the ResultsViewer:

- avatol_cv/modules/scoredDataSpec.txt

The partsScopring algortihm is integrated with AvatolCV via these two files:

- avatol_cv/modules/scoring/batskullDPM/algPropertiesMac.txt
- avatol_cv/modules/scoring/batskullDPM/algPropertiesWindows.txt

The partScoring algorithm lives in another repo (https://github.com/AVATOL/bat).  See avatol_cv/docs/AvatolCVDeveloperDocumentation.pdf for positioning it correctly under batskullDPM

- avatol_cv/modules/scoring/batskullDPM/bat

The shapeTextureScoring algorithm lives in the AvatolCV repo and is integrated with AvatolCV with this file:

- avatol_cv/modules/scoring/leafScore/algPropertiesMac.txt

The highClutterSegmenter is integrated with AvatolCV via these two files:

- avatol_cv/modules/segmentation/hcsearchSeg/algPropertiesMac.txt
- avatol_cv/modules/segmentation/hcsearchSeg/algPropertiesWindows.txt

The highClutterSegmenter lives in another repo (https://github.com/AVATOL/nematocyst).  See avatol_cv/modules/hcSearch_Installation_Instructions.txt for instructions on how to position it. 

- avatol_cv/modules/segmentation/hcsearchSeg/nematocyst

The basicSegmenter algorithm lives in this AvatolCV repo, but has third party dependencies that are needed (See avatol_cv/modules/Darwin_Library_Installation_Instructions_MACOS.pdf for instructions).  basicSegmenter is integrated with AvatolCV via these two files.  The first runs the algorithm using a pretrained model, the second retrains the model.  These two will show up as different entries in the algorithm selection list in the UI:

- avatol_cv/modules/segmentation/yaoSeg/algPropertiesMac.txt
- avatol_cv/modules/segmentation/yaoSeg/algPropertiesMac_reTrain.txt

Data that is created during an AvatolCV session is stored under  the sessions dierectory:

- avatol_cv/sessions
 
Data for a particular run is stored under the directory like the following, where someProjectName would be replaced by the project name of the image set in either Morphobank or BisQue, and someRunID is the runID for the particular AvatolCV session (automatically generated):

- avatol_cv/sessions/someProjectName/someRunID

...which would appear something like this (Note that the runID is a datestamp followed by an underscore followed by a one-up number: 201610019_01): 

- avatol_cv/sessions/AVAToL Computer Vision Matrix/20161019_01

Images that are excluded during the session for some reason (i.e. they are the wrong view in Morphobank) will be representedin the exclusions directory under the runID directory.  Filenames are imageIDs

- avatol_cv/sessions/someProjectName/someRunID/exclusions/381004.txt

Answers to the filter screen are stored in this file for a given run:

- avatol_cv/sessions/someProjectName/someRunID/filter.txt

The partsScoring algorithm generated logs here.  Note that the partsScoring algorithm was developed against a bat skull dataset.  The first log is what happened during translating the current normalized input data files into a legacy format expected by the algorithm.  The second log is of the algorithm.  The third is of the translation from the legacy output format of the algorithm to the new normalized format for AvatiolCV's ResultsViewer to be able to read the file:

- avatol_cv/sessions/someProjectName/someRunID/logs/scoring/matlab_run_1476913497_translate_input.txt
- avatol_cv/sessions/someProjectName/someRunID/logs/scoring/matlab_run_1476913506_invoke_batskull_system.txt
- avatol_cv/sessions/someProjectName/someRunID/logs/scoring/matlab_run_1476927805_translate_output.txt

The output of the orientation stage, if it was run, would be put in this direcoty:

- avatol_cv/sessions/someProjectName/someRunID/orientedData

The runConfig files would appear as:

- avatol_cv/sessions/someProjectName/someRunID/runConfig_scoring.txt
- avatol_cv/sessions/someProjectName/someRunID/runConfig_segmentation.txt
- avatol_cv/sessions/someProjectName/someRunID/runConfig_orientation.txt

When a user pulls up the results of a scoring run into the results viewer, a statistics file is generated and named with this format :

- avatol_cv/sessions/someProjectName/someRunID/scoredData/outputStats_someRunID_scoredCharName.txt

... such as this:

- avatol_cv/sessions/someProjectName/someRunID/scoredData/outputStats_20161019_01_M3 presence.txt

Results of the scoring stage are put in files of the format:

- avatol_cv/sessions/someProjectName/someRunID/scoredData/scored_character_charID_charName.txt

...such as this:

- avatol_cv/sessions/someProjectName/someRunID/scoredData/scored_character_1824347_Upper I1 presence.txt

If the run is an evaluation run (see avatol_cv/docs/AvatolCVUsersGuide.pdf for explanation of evaluation run), the breakdown of which taxa were trained, scored, or ignored (as set in the runConfiguration screen) are captured in this file:

- avatol_cv/sessions/someProjectName/someRunID/scoredData/trainScoreIgnoreBreakdown.txt

The output of the segmentation stage, if it were executed, is put in this directory:

- avatol_cv/sessions/someProjectName/someRunID/segmentedData

The scoring manifest file (file that contains pathnames of images to score) is below.  The name of this file is included in the runConfigScoring file as dictated by the algProperties file.

- avatol_cv/sessions/someProjectName/someRunID/testImagesFile_scoring.txt

If all the images in the dataset are already scored, AvatolCV can be used to do an evaluation run, where it pretends that some number of images (specified by the user) are held back from use as training data and are scored.  Then the resulting scores can be compared to the true values.  This file lists those that were held out:

- avatol_cv/sessions/someProjectName/someRunID/trainingDataForScoring/holdout_character_someCharID_someCharName.txt

In evaluation runs, the user can mark certain taxa to ignore.  That's captured here:

- avatol_cv/sessions/someProjectName/someRunID/trainingDataForScoring/ignore_character_someCharID_someCharName.txt

For either evaluation or traditional scoring runs, the images to score are stored in:

- avatol_cv/sessions/someProjectName/someRunID/trainingDataForScoring/scoring_character_someCharID_someCharName.txt

For either evaluation or traditional scoring runs, the images to use for training are stored in:

- avatol_cv/sessions/someProjectName/someRunID/trainingDataForScoring/training_character_someCharID_someCharName.txt

If data is pulled in from a Morphobank project, it is saved in the morphobank folder before being convered to normalized form

- avatol_cv/sessions/someProjectName/morphobank
- avatol_cv/sessions/someProjectName/morphobank/annotations/c1824347_m380454_t773126.txt
- avatol_cv/sessions/someProjectName/morphobank/charStates/c1824347_t773120.txt
- avatol_cv/sessions/someProjectName/morphobank/mediaInfo/c1824347_t773120_v8902.txt

If data is pulled in from a BisQue project, it is saved in the bisque folder before being convered to normalized form

- avatol_cv/sessions/someProjectName/bisque

After data is pulled in from a data source, it is normalized to a standard form (see avatol_cv/docs/AvatolCVDeveloperDocumentation.pdf for more info)

- avatol_cv/sessions/someProjectName/normalized

If a file is excluded because of image quality, it is exluded for all runs with a file in this directory:
 
- avatol_cv/sessions/someProjectName/normalized/exclusions

All metadata for a particular image (in Bisque) or cellImage (in Morphobank) is stored in a file like this, where the imageID precedes the underscore and a one up number follows it.  The one up number is there because in Morphobank projects, you might have the same image reused for different cells of a given taxon, each with its own metadata, so each needing its own file:

- avatol_cv/sessions/someProjectName/normalized/imageInfo/380454_1.txt

This is where the pulled imageas are stored:

- avatol_cv/sessions/AVAToL Computer Vision Matrix/normalized/images/large/380454__1000.jpg

This is where thumbnails are stored:

- avatol_cv/sessions/AVAToL Computer Vision Matrix/normalized/images/thumbnail/380454__80.jpg

Once a scoring run is complete, a file is created in the sessionSummaries directory.  This is where resultsViewer looks to find which results are available to show:

- avatol_cv/sessionSummaries/someRunID_someCharName.txt

...for example...

- avatol_cv/sessionSummaries/20161019_01_M3 presence.txt



# Special Files
- when you authenticate into BisQue, the authentication token Bisque offers is stored in avatol_cv/bisqueCookies.txt.  Once that file is in play, user is no longer asked to authenticate into BisQue.  This is possible because the auth token is valid for six (?) months.
- to avoid having to type in authentication info for BisQue and Morphobank during development, create a file called avatol_cv/defaults.txt of this form:
```
morphobankUserId=<thatID>
morphobankPassword=<thatPW>
bisqueUserId=<thatID>
bisquePassword=<thatPW>
```
