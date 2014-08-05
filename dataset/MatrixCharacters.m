classdef MatrixCharacters < handle
    properties
        
	    datasetOneSet = 'no';
	    datasetTwoSet = 'no';
		datasetOne;
		datasetTwo;
		matrixName;
		characters = {};
        charactersPresenceAbsence = {};
        charactersWithAnnotationFiles = {};
        annotationFilenamesForCharacters;
	end
	
	methods
        % NOTE dom tree uses java arrays so index starting at 0
	    function obj = MatrixCharacters(domNode, matrixName)
            obj.parseDomNodeForCharacters(domNode);
			obj.matrixName = matrixName;
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
			    obj.parseDatasetNodeForCharacters(obj.datasetTwo);
                obj.findPresenceAbsenceCharacters();
                %obj.dumpMediaForCharacters(domNode);
                %obj.findCharactersWithAnnotationFiles(domNode);
			end
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
        
        function registerAnnotationFiles(obj, charId, categoricalNode, map)
            categoricalChildren = categoricalNode.getChildNodes;
            categoricalChildrenCount = categoricalChildren.getLength();
            for m=0:categoricalChildrenCount - 1
                 categoricalChildNode = categoricalChildren.item(m);
                 categoricalChildNodeName = categoricalChildNode.getNodeName();
                 if strcmp(categoricalChildNodeName, 'MediaObject')
                     mediaId = char(categoricalChildNode.getAttribute('ref'));
                     candidateAnnotationFilename = createAnnotationFilename(charId, mediaId);
                     if exist(candidateAnnotationFilename, 'file')
                         registerAnnotationFilename(map,charId, mediaId);
                     end
                 end
            end
        end
        %<Categorical ref="c524112"><MediaObject ref="m151268"/><State ref="s1168129"/></Categorical>
        function findCharactersWithAnnotationFiles(obj, domNode)
            obj.annotationFilenamesForCharacters = containers.Map();
            categoricals = domNode.getElementsByTagName('Categorical');
            length = categoricals.getLength();
            for i=0:categoricals.getLength() - 1
                categoricalNode = categoricals.item(i);
                %foo = categoricalNode.getAttribute('ref');
                charId = char(categoricalNode.getAttribute('ref'));
                if isCharIdQualified(charId)
                    registerAnnotationFiles(charId, categoricalNode, obj.annotationFilenamesForCharacters);
                end
            end
            
            
            
            %annotationFilenamesForCharacters = new java.util.Hashtable<java.lang.String, java.lang.String>;
            count = length(obj.charactersPresenceAbsence);
            for i=1:count
                character = obj.charactersPresenceAbsence(i);
                id = character.id;
                
                if character.hasStatePresent
                    obj.charactersPresenceAbsence = [ obj.charactersPresenceAbsence, character ];
                end
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