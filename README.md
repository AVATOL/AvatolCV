# AvatolCV
NOTE: This material is based upon work supported by the National Science Foundation under Grant No. 1208272. 
Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) 
and do not necessarily reflect the views of the National Science Foundation.

This is the source code repository for AvatolCV, a desktop Java application for running biological image data 
through computer vision algorithms for the purpose of scoring morphologic characters.

To install an AvatolCV distribution, visit https://github.com/AVATOL/AvatolCVInstaller and consult the README file there.

The documentation available with this source code is located in the docs directory:

AvatolCVUsersGuide.pdf
AvatolCVDeveloperDocumentation.pdf
AddingNewAlgorithmsToAvatolCV.pdf
AvatolCVAlgorithmsDescriptions.txt

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
      
      
The java packages contained in AvatolCV are:

- edu.oregonstate.eecs.iis.avatolcv.algorithm // classes that encapsulate algorithm related concerns
- edu.oregonstate.eecs.iis.avatolcv.core
- edu.oregonstate.eecs.iis.avatolcv.css
- edu.oregonstate.eecs.iis.avatolcv.datasource
- edu.oregonstate.eecs.iis.avatolcv.javafxui
- edu.oregonstate.eecs.iis.avatolcv.normalized
- edu.oregonstate.eecs.iis.avatolcv.results
- edu.oregonstate.eecs.iis.avatolcv.scoring
- edu.oregonstate.eecs.iis.avatolcv.session
- edu.oregonstate.eecs.iis.avatolcv.steps
- edu.oregonstate.eecs.iis.avatolcv.tools
- edu.oregonstate.eecs.iis.avatolcv.tutorial
- edu.oregonstate.eecs.iis.avatolcv.ui.javafx
- edu.oregonstate.eecs.iis.avatolcv.util
- edu.oregonstate.eecs.iis.avatolcv.ws
- edu.oregonstate.eecs.iis.avatolcv.ws.bisque
- edu.oregonstate.eecs.iis.avatolcv.ws.morphobank
