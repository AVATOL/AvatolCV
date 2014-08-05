classdef MatrixCharacters < handle
    properties
        
	    datasetOneSet = 'no';
	    datasetTwoSet = 'no';
		datasetOne;
		datasetTwo;
		matrixName;
		characters = {};
        charactersPresenceAbsence = {};
        %annotationFileForCharacter = {};
        annotationFilenamesForCharacters = {};
        mediaForAnnotationFilenames = {};
        taxonsForMedia = {};
        matrixDir;
	end
	
	methods
        % NOTE dom tree uses java arrays so index starting at 0
	    function obj = MatrixCharacters(domNode, matrixName, matrixDir)
            obj.matrixDir = matrixDir;
			obj.matrixName = matrixName;
            obj.parseDomNodeForCharacters(domNode);
        end
	    function parseDomNodeForCharacters(obj, domNode)
			datasetsNode = domNode.getDocumentElement;
            fprintf('docNode name %s\n', char(datasetsNode.getNodeName()));
            datasetNodesAndWhiteSpaceNodes = datasetsNode.getChildNodes;
            count = datasetNodesAndWhiteSpaceNodes.getLength; 
            for i=0:count-1
                somethingNode = datasetNodesAndWhiteSpaceNodes.item(i);
                nodeName = somethingNode.getNodeName();
				if strcmp(nodeName,'Dataset')
					if (strcmp(obj.datasetOneSet,'no'))
						obj.datasetOne = somethingNode;
						obj.datasetOneSet = 'yes';
					else
						obj.datasetTwo = somethingNode;
						obj.datasetTwoSet = 'yes';
					end
				end

            end
			if (strcmp('no',obj.datasetOneSet))
			    msg = sprintf('First Dataset element not present for matrix %s.',obj.matrixName);
                err = MException('MatrixCharactersError', msg);
                throw(err);
			elseif (strcmp('no',obj.datasetTwoSet))
			    msg = sprintf('Second Dataset element not present for matrix %s.',obj.matrixName);
                err = MException('MatrixCharactersError', msg);
                throw(err);
            else
                obj.erasePriorInputData();
                obj.createInputDataDir();
			    obj.parseDatasetNodeForCharacters(obj.datasetTwo);
                obj.findPresenceAbsenceCharacters();
                %obj.dumpMediaForCharacters(domNode);
                obj.loadTaxonsForMedia(domNode);
                obj.findAnnotationFilesForCharacter(domNode);
                obj.findMediaToScoreForCharacter(domNode);
                obj.generateInputDataFiles();
			end
        end
        function inputDataDir = getInputDataDir(obj)
            if ispc
                inputDataDir = sprintf('%s\\input',obj.matrixDir );
             else
                inputDataDir = sprintf('%s/input',obj.matrixDir );
             end
        end
        function createInputDataDir(obj)
            inputDataDir = obj.getInputDataDir();
             if not(exist(inputDataDir, 'dir'))
                 mkdir(inputDataDir);
             end
        end
        function erasePriorInputData(obj)
             inputDataDir = obj.getInputDataDir();
             if exist(inputDataDir, 'dir')
                 files = dir(inputDataDir);
                 for i=1:length(files)
                     filename = char(files(i));
                     matchingIndices = strfind(filename, '.txt');
                     if not(isempty(matchingIndices))
                         % match, delete
                         pathname = getPathnameForFile(inputDataDir,filename);
                         delete(pathname);
                     end
                 end
             end
        end
        function pathname = getPathnameForFile(obj, parent, filename)
            if ispc
            	pathname = sprintf('%s\\%s',parent,filename );
            else
            	pathname = sprintf('%s/%s',parent, filename );
            end
        end
        function loadTaxonsForMedia(obj, domNode)
            obj.taxonsForMedia = containers.Map();
            specimens = domNode.getElementsByTagName('Specimen');
            for i=0:specimens.getLength() - 1
                specimenNode = specimens.item(i);
                %foo = categoricalNode.getAttribute('ref');
                taxonNodes = specimenNode.getElementsByTagName('TaxonName');
                taxonNode = taxonNodes.item(0);
                taxonId = char(taxonNode.getAttribute('ref'));
                mediaNodes = specimenNode.getElementsByTagName('MediaObjects');
                for j=0:mediaNodes.getLenth() - 1
                    mediaNode = mdeiaNodes.item(j);
                    mediaId = char(mediaNode.getAttribute('ref'));
                    obj.taxonsForMedia(mediaId) = taxonId;
                end
            end
        end    
        function characterState = getCharacterStateFromAnnotationFile(obj, pathname)
            instead of this, I should create a class called Annotation and load them from files, keep a hash of them keyed by c54738_m43788_lineNumber
            LEFT OFF HERE
        end
         %<Categorical ref="c524112"><MediaObject ref="m151268"/><State ref="s1168129"/></Categorical>
        function findMediaToScoreForCharacter(obj, domNode)
            obj.annotationFilenamesForCharacters = containers.Map();
            categoricals = domNode.getElementsByTagName('Categorical');
            for i=0:categoricals.getLength() - 1
                categoricalNode = categoricals.item(i);
                %foo = categoricalNode.getAttribute('ref');
                charId = char(categoricalNode.getAttribute('ref'));
                if obj.isCharIdOfInterest(charId)
                    annotationPathnames = obj.getAnnotationFilePathnames(charId, categoricalNode);
                    obj.annotationFilenamesForCharacters(charId) = annotationPathnames;
                end
            end
        end
        
        % sorted_input_data_<charID>_charName.txt
        % for each annotation file, add line
        % training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID
         % for each media file which needs scoring, add line
        % image_to_score:media/<name_of_mediafile>:taxonID 
        function generateInputDataFiles(obj)
            
        end
        function dumpMediaForCharacters(obj, domNode)
            categoricals = domNode.getElementsByTagName('Categorical');
            length = categoricals.getLength();
            for i=0:categoricals.getLength() - 1
                categoricalNode = categoricals.item(i);
                %foo = categoricalNode.getAttribute('ref');
                charId = char(categoricalNode.getAttribute('ref'));
                categoricalChildren = categoricalNode.getChildNodes;
                categoricalChildrenCount = categoricalChildren.getLength();
                mediaCount = 0;
                stateCount = 0;
                for m=0:categoricalChildrenCount - 1
                     categoricalChildNode = categoricalChildren.item(m);
                     categoricalChildNodeName = categoricalChildNode.getNodeName();
                     if strcmp(categoricalChildNodeName, 'MediaObject')
                          mediaCount = mediaCount + 1;
                     elseif strcmp(categoricalChildNodeName, 'State')
                          stateCount = stateCount + 1;
                     end
                end
                if or((mediaCount > 1),(stateCount > 1))
                     fprintf('char %s media count %i state count %i\n', charId, mediaCount, stateCount);
                end
            end
        end
        
        
        function getAttribute(obj, attributeName, node)
            attributes = node.getAttributes;
            n_attr = attributes.getLength;
            for i = 0:n_attr-1
                attr = attributes.item(i);
                attrName = attr.getName;
                if (strcmp(attrName, attributeName))
                    obj.id = char(attr.getValue);
                end
            end
        end
        
        %<Categorical ref="c524112"><MediaObject ref="m151268"/><State ref="s1168129"/></Categorical>
        function findAnnotationFilesForCharacter(obj, domNode)
            obj.annotationFilenamesForCharacters = containers.Map();
            obj.mediaForAnnotationFilenames = containers.Map();
            categoricals = domNode.getElementsByTagName('Categorical');
            for i=0:categoricals.getLength() - 1
                categoricalNode = categoricals.item(i);
                %foo = categoricalNode.getAttribute('ref');
                charId = char(categoricalNode.getAttribute('ref'));
                if obj.isCharIdOfInterest(charId)
                    annotationPathnames = obj.getAnnotationFilePathnames(charId, categoricalNode);
                    obj.annotationFilenamesForCharacters(charId) = annotationPathnames;
                    obj.setMediaForAnnotationFilenames(categoricalNode, obj.mediaForAnnotationFilenames);
                end
            end
        end
        function result = isCharIdOfInterest(obj, charId)
            result = false;
            for i=1:length(obj.charactersPresenceAbsence)
                character = obj.charactersPresenceAbsence(i);
                if strcmp(character.id,charId)
                    result = true;
                end
            end
        end
        
        function setMediaForAnnotationFilenames(obj, categoricalNode, mediaForAnnotationFilenamesMap)
            mediaObjects = categoricalNode.getElementsByTagName('MediaObject');
            for i=0:mediaObjects.getLength()-1
                mediaObject = mediaObjects.item(i);
                mediaId = char(mediaObject.getAttribute('ref'));
                pathname = getAnnotationFilePathname(obj.matrixDir, charId, mediaId);
                if exist(pathname, 'file')
                    mediaForAnnotationFilenamesMap(pathname) = mediaId;
                end
            end
        end
        function pathnames = getAnnotationFilePathnames(obj, charId, categoricalNode)
            pathnames = {};
            mediaObjects = categoricalNode.getElementsByTagName('MediaObject');
            for i=0:mediaObjects.getLength()-1
                mediaObject = mediaObjects.item(i);
                mediaId = char(mediaObject.getAttribute('ref'));
                pathname = getAnnotationFilePathname(obj.matrixDir, charId, mediaId);
                if exist(pathname, 'file')
                    pathnames = [ pathnames, pathname ];
                end
            end
        end
        
        function pathname = getAnnotationFilePathname(obj, matrixDir, charId, mediaId)
            if ispc
            	pathname = sprintf('%s\\annotations\\%s_%s.txt',obj.matrixDir, charId, mediaId);
            else
                pathname = sprintf('%s/annotations/%s_%s.txt',obj.matrixDir, charId, mediaId);
            end
        end
		function parseDatasetNodeForCharacters(obj, datasetNode)
            datasetChildren = datasetNode.getChildNodes;
            count = datasetChildren.getLength; 
            for i=0:count-1
                somethingNode = datasetChildren.item(i);
                nodeName = somethingNode.getNodeName();
				if strcmp(nodeName,'Characters')
					charactersNode = somethingNode;
					charactersChildren = charactersNode.getChildNodes;
					charactersChildrenCount = charactersNode.getLength();
					for j=0:charactersChildrenCount - 1
						candidateNode = charactersChildren.item(j);
						candidateNodeName = candidateNode.getNodeName();
						if (strcmp(candidateNodeName,'CategoricalCharacter'))
						    newChar = Character(candidateNode);
						    obj.characters = [ obj.characters, newChar ];
				        end
				   end
                end
            end		
        end
        function findPresenceAbsenceCharacters(obj)
            count = length(obj.characters);
            for i=1:count
                character = obj.characters(i);
                if character.hasStatePresent
                    obj.charactersPresenceAbsence = [ obj.charactersPresenceAbsence, character ];
                end
            end
        end 
	end


end