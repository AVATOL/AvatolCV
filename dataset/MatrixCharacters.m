classdef MatrixCharacters < handle
    properties
        
	    datasetOneSet = 'no';
	    datasetTwoSet = 'no';
		datasetOne;
		datasetTwo;
		matrixName;
		characters = {};
        charactersPresenceAbsence = {};
        charactersTrained = {};
        %annotationFileForCharacter = {};
        annotationFilenamesForCharacters = {};
        annotationForCharMediaAndLine = {};
        mediaForAnnotationFilenames = {};
        characterForAnnotationFilenames = {};
        taxonsForMedia = {};
        annotations = {};
        annotationsForCharacter = {};
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
                % set up Map containers
                obj.annotationFilenamesForCharacters = containers.Map();
                obj.mediaForAnnotationFilenames = containers.Map();
                obj.characterForAnnotationFilenames = containers.Map();
                obj.annotationsForCharacter = containers.Map();
            
                obj.erasePriorInputData();
                obj.createInputDataDir();
			    obj.parseDatasetNodeForCharacters(obj.datasetTwo);
                obj.findPresenceAbsenceCharacters();
                
                %obj.dumpMediaForCharacters(domNode);
                obj.loadTaxonsForMedia(domNode);
                obj.findAnnotationFilesForCharacter(domNode);
                obj.findTrainedCharacters();
                obj.loadAnnotations();
                %obj.findMediaToScoreForCharacter(domNode);
                obj.generateInputDataFiles();
			end
        end
        function loadAnnotations(obj)
            fprintf('running loadAnnotations');
            for i=1:length(obj.charactersTrained)
                character = obj.charactersTrained(i);
                charId = character.id;
                if obj.annotationFilenamesForCharacters.isKey(charId)
                    fprintf('character id : %s\n',charId);
                    annotationFilenames = obj.annotationFilenamesForCharacters(charId);
                    fprintf('char %s   length of annotation filenames %i.\n',charId,length(annotationFilenames));
                    for j=1:length(annotationFilenames)
                        annotationFilename = char(annotationFilenames(j));
                        fprintf('annotationFilename : %s\n',annotationFilename);
                        obj.loadAnnotationsFromFile(annotationFilename);
                    end
                end
            end
        end    
        function loadAnnotationsFromFile(obj, annotationPathname)
            import java.lang.String;
            import java.io.BufferedReader;
            import java.io.FileReader;
            line = java.lang.String;
            
            reader = BufferedReader(FileReader(annotationPathname));
            charId = obj.characterForAnnotationFilenames(annotationPathname);
            mediaId = obj.mediaForAnnotationFilenames(annotationPathname);
            line = reader.readLine();
            emptyArray = [];
            lineNumber = 1;
            while and((line ~= emptyArray),(not(strcmp(char(line),''))))
                fprintf('line : %s\n',char(line));
                key = obj.createAnnotationKey(annotationPathname,lineNumber);
                annotation = Annotation(char(line), 1, mediaId, annotationPathname);
                obj.annotations(key) = { annotation };
                obj.registerAnnotationForCharacter(annotation,charId);
                lineNumber = lineNumber + 1;
                line = reader.readLine();
            end
            
        end
        
        function registerAnnotationForCharacter(obj, annotation, charId)
            if isKey(obj.annotationsForCharacter, charId)
                annotationsForChar = obj.annotationsForCharacter(charId);
            else
                annotationsForChar = {};
            end
            annotationsForChar = [ annotationsForChar, annotation ];
            obj.annotationsForCharacter(charId) = annotationsForChar;
        end
        
        function key = createAnnotationKey(obj,annotationFilename,lineNumber)
            charId = obj.characterForAnnotationFilenames(annotationFilename);
            mediaId = obj.mediaForAnnotationFilenames(annotationFilename);
            key = sprintf('%s_%s_%i',charId, mediaId, lineNumber);
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
                     filename = files(i).name;
                     matchingIndices = strfind(filename, '.txt');
                     if not(isempty(matchingIndices))
                         % match, delete
                         pathname = obj.getPathnameForFile(inputDataDir,filename);
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
                mediaNodes = specimenNode.getElementsByTagName('MediaObject');
                count = mediaNodes.getLength();
                for j=0:mediaNodes.getLength() - 1
                    mediaNode = mediaNodes.item(j);
                    mediaId = char(mediaNode.getAttribute('ref'));
                    obj.taxonsForMedia(mediaId) = taxonId;
                end
            end
        end    
        function filename = getMediaFilenameForMediaId(obj, mediaId)
            filename = 'fileNotFound';
            if ispc()
                mediaDir = sprintf('%s\\media', obj.matrixDir);
            else
                mediaDir = sprintf('%s/media', obj.matrixDir);
            end
            mediaIdPrefix = strrep(mediaId, 'm', 'M');
            files = dir(mediaDir);
            for i=1:length(files)
                candidateFilename = files(i).name;
                LEFT OFF TRYING TO MATCH
                indicesArray = strfind(filename, mediaIdPrefix);
                if not(isempty(indicesArray))
                    filename = candidateFilename;
                    return;
                end
            end
        end
        % sorted_input_data_<charID>_charName.txt
        % for each annotation file, add line
        % training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID
         % for each media file which needs scoring, add line
        % image_to_score:media/<name_of_mediafile>:taxonID 
        function generateInputDataFiles(obj)
            inputDataLines = {};
            for i=1:length(obj.charactersTrained)
                character = obj.charactersTrained(i);
                charId = character.id;
                if isKey(obj.annotationsForCharacter, charId)
                    annotationsForChar = obj.annotationsForCharacter(charId);
                    for j=1:length(annotationsForChar)
                        annotationForChar = annotationsForChar(j);
                        charNameText = annotationForChar.charNameText;
                        charStateText = annotationForChar.charStateText;
                        charState = annotationForChar.charState;
                        mediaId = annotationForChar.mediaId;
                        annotationPathname = annotationForChar.pathname;
                        mediaFilename = obj.getMediaFilenameForMediaId(mediaId);
                        taxonId = obj.taxonsForMedia(mediaId);
                        lineNumber = annotationForChar.lineNumber;
                        inputDataLine = sprintf('training_data:media/%s:%s:%s:%s:%s',mediaFilename,charState,charStateText,annotationPathname, taxonId,lineNumber);
                        inputDataLines = [ inputDataLines, inputDataLine ];
                    end
                end
            end
            filename = sprintf('sorted_input_data_%s_%s.txt',charId,charNameText);
            if ispc()
                inputFilePathname = sprintf('%s\\input\\%s', obj.matrixDir, filename);
            else
                inputFilePathname = sprintf('%s/input/%s', obj.matrixDir, filename);
            end
            if (exist(inputFilePathname, 'file'))
                delete(inputFilePathname);
            end
            fileId = fopen(inputFilePathname,'w');
            for i=1:length(inputDataLines)
                inputDataLine = inputDataLines(i);
                fprintf(fileId,'%s\n',inputDataLine);
            end
            close(fileId);
            
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
            fprintf('running findAnnotationFilesForCharacter');
            categoricals = domNode.getElementsByTagName('Categorical');
            for i=0:categoricals.getLength() - 1
                categoricalNode = categoricals.item(i);
                %foo = categoricalNode.getAttribute('ref');
                charId = char(categoricalNode.getAttribute('ref'));
                if obj.isCharIdOfInterest(charId)
                    annotationPathnamesNew = obj.getAnnotationFilePathnames(charId, categoricalNode);
                    
                    annotationPathnamesAll = {};
                    if isKey(obj.annotationFilenamesForCharacters,charId)
                        annotationPathnamesAll = obj.annotationFilenamesForCharacters(charId);
                    end
                    
                    for j=1:length(annotationPathnamesNew)
                        annotationPathnameNew = annotationPathnamesNew(j);
                        annotationPathnamesAll = [ annotationPathnamesAll, annotationPathnameNew ];
                    end
                    obj.annotationFilenamesForCharacters(charId) = annotationPathnamesAll;
                    %fprintf('char %s   length of annotation filenames %i.\n',charId,length(annotationPathnamesAll));
                    obj.setMediaForAnnotationFilenames(categoricalNode);
                    
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
        
        function setMediaForAnnotationFilenames(obj, categoricalNode)
            mediaObjects = categoricalNode.getElementsByTagName('MediaObject');
            charId = char(categoricalNode.getAttribute('ref'));
            for i=0:mediaObjects.getLength()-1
                mediaObject = mediaObjects.item(i);
                mediaId = char(mediaObject.getAttribute('ref'));
                pathname = obj.getAnnotationFilePathname(obj.matrixDir, charId, mediaId);
                if exist(pathname, 'file')
                    obj.mediaForAnnotationFilenames(pathname) = mediaId;
                    obj.characterForAnnotationFilenames(pathname) = charId;
                end
            end
        end
        function pathnames = getAnnotationFilePathnames(obj, charId, categoricalNode)
            pathnames = {};
            mediaObjects = categoricalNode.getElementsByTagName('MediaObject');
            for i=0:mediaObjects.getLength()-1
                mediaObject = mediaObjects.item(i);
                mediaId = char(mediaObject.getAttribute('ref'));
                %fprintf('...: %s_%s\n',charId,mediaId);
                pathname = obj.getAnnotationFilePathname(obj.matrixDir, charId, mediaId);
                %fprintf('...checking pathname: %s\n',pathname);
                if exist(pathname, 'file')
                    %fprintf('found path: %s\n',pathname);
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
            categoricalCharacterNodes = datasetNode.getElementsByTagName('CategoricalCharacter');
            for i=0:categoricalCharacterNodes.getLength-1
                categoricalCharacterNode = categoricalCharacterNodes.item(i);
                newChar = Character(categoricalCharacterNode);
                obj.characters = [ obj.characters, newChar ];
            end		
        end
		function parseDatasetNodeForCharactersObsolete(obj, datasetNode)
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
        function findTrainedCharacters(obj)
            for i=1:length(obj.characters)
                character = obj.characters(i);
                charId = character.id;
                if isKey(obj.annotationFilenamesForCharacters,charId)
                    filenames = obj.annotationFilenamesForCharacters(charId);
                    if not(isempty(filenames))
                        obj.charactersTrained = [ obj.charactersTrained, character ];
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