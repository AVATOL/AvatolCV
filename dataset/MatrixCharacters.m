classdef MatrixCharacters < handle
    properties
	    datasetOneSet = 'no';
	    datasetTwoSet = 'no';
		datasetOne;
		datasetTwo;
		matrixName;
		characters = {};
        charactersPresenceAbscence = {};
        charactersWithAnnotationFiles = {};
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
                obj.dumpMediaForCharactersViaXpath(domNode);
                %obj.dumpMediaForCharacters(obj.datasetTwo);
                %obj.findCharactersWithAnnotationFiles();
			end
        end
        function dumpMediaForCharactersViaXpath(obj, domNode)
            import javax.xml.xpath.*;
            factory = XPathFactory.newInstance;
            xpath = factory.newXPath;
            %expression = xpath.compile('CodedDescriptions/CodedDescription/SummaryData/Categorical');
            expression = xpath.compile('Datasets/Dataset/CodedDescriptions');
            categoricals = expression.evaluate(domNode, XPathConstants.NODESET);
            length = categoricals.getLength();
            for i=0:categoricals.getLength()
                categoricalNode = categoricals.item(i);
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
        function dumpMediaForCharacters(obj, datasetNode)
            datasetChildren = datasetNode.getChildNodes;
            count = datasetChildren.getLength; 
            for i=0:count-1
                somethingNode = datasetChildren.item(i);
                nodeName = somethingNode.getNodeName();
				if strcmp(nodeName,'CodedDescriptions')
					codedDescriptionsNode = somethingNode;
					codedDescriptionsChildren = codedDescriptionsNode.getChildNodes;
					codedDescriptionsChildrenCount = codedDescriptionsChildren.getLength();
					for j=0:codedDescriptionsChildrenCount - 1
						codedDescriptionsChild = codedDescriptionsChildren.item(j);
						codedDescriptionsChildName = codedDescriptionsChild.getNodeName();
                        if strcmp(codedDescriptionsChildName,'CodedDescription')
                            codedDescriptionChildren = codedDescriptionsChild.getChildNodes;
                            codedDescriptionChildrenCount = codedDescriptionChildren.getLength();
                            for k=0:codedDescriptionChildrenCount - 1
                                codedDescriptionChildNode = codedDescriptionChildren.item(k);
                                codedDescriptionChildNodeName = codedDescriptionChildNode.getNodeName();
                                if (strcmp(codedDescriptionChildNodeName,'SummaryData'))
                                    summaryDataChildren = codedDescriptionChildNode.getChildNodes;
                                    summaryDataChildrenCount = summaryDataChildren.getLength();
                                    for l=0:summaryDataChildrenCount - 1
                                        summaryDataChildNode = summaryDataChildren.item(l);
                                        summaryDataChildNodeName = summaryDataChildNode.getNodeName();
                                        if (strcmp(summaryDataChildNodeName,'Categorical'))
                                            charId = char(summaryDataChildNode.getAttribute('ref'));
                                            categoricalChildren = summaryDataChildNode.getChildNodes;
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
                                end
                            end
				        end
				   end
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
        function findCharactersWithAnnotationFiles(obj)
            count = length(obj.charactersPresenceAbscence);
            for i=1:count
                character = obj.charactersPresenceAbscence(i);
                id = character.id;
                
                if character.hasStatePresent
                    obj.charactersPresenceAbscence = [ obj.charactersPresenceAbscence, character ];
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
                    obj.charactersPresenceAbscence = [ obj.charactersPresenceAbscence, character ];
                end
            end
        end 
	end


end