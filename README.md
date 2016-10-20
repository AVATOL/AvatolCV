
# AvatolCV
NOTE: This material is based upon work supported by the National Science Foundation under Grant No. 1208272. 
Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) 
and do not necessarily reflect the views of the National Science Foundation.

This is the source code repository for AvatolCV, a desktop Java application for running biological image data 
through computer vision algorithms for the purpose of scoring morphologic characters.

To install an AvatolCV distribution, visit https://github.com/AVATOL/AvatolCVInstaller and consult the README file there.

# Documentation
The documentation available with this source code is located in the docs directory:

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
- edu.oregonstate.eecs.iis.avatolcv.normalized // all the classes dealing with AvatolCV's normalized data format
- edu.oregonstate.eecs.iis.avatolcv.results // classes supporting the ResultsViewer screen
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

# To Develop
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




# Special Files
- when you authenticate into BisQue, the authentication token Bisque offers is stored in avatol_cv/bisqueCookies.txt.  Once that file is in play, user is no longer asked to authenticate into BisQue.  This is possible because the auth token is valid for six (?) months.
- to avoid having to type in authentication info for BisQue and Morphobank during development, create a file called avatol_cv/defaults.txt of this form:
```
morphobankUserId=<thatID>
morphobankPassword=<thatPW>
bisqueUserId=<thatID>
bisquePassword=<thatPW>
```
