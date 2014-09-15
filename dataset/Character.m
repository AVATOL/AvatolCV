classdef Character < handle
    properties
	    id = 'notYetSet';
        name = 'notYetSet';
        hasStatePresent = false;
	end
	
	methods
	    %    <CategoricalCharacter id='c524104'>
		%	      <Representation><Label>GEN skull, dorsal margin, shape at juncture of braincase and rostrum in lateral view</Label></Representation>
		%		  <States>
		%		        <StateDefinition id='cs1168103'><Representation><Label>concave</Label><Detail xml:lang="en" role="number">0</Detail><MediaObject ref="m151258"/></Representation></StateDefinition>
        %                <StateDefinition id='cs1168104'><Representation><Label>flat</Label><Detail xml:lang="en" role="number">1</Detail><MediaObject ref="m151267"/></Representation></StateDefinition>
        %                <StateDefinition id='cs1168105'><Representation><Label>convex</Label><Detail xml:lang="en" role="number">2</Detail></Representation></StateDefinition>
        %          </States><MediaObject ref="m307684"/>
		%      </CategoricalCharacter>
	    function obj = Character(catCharNode)
		    obj.loadId(catCharNode);
            obj.loadName(catCharNode);
            obj.checkIfStatePresentAbsent(catCharNode);
            if (strcmp(obj.name,'notYetSet'))
                msg = sprintf('No representation label for character\n\n %s.',xmlwrite(catCharNode));
                err = MException('MatrixCharactersError', msg);
                throw(err);
            end
            if (strcmp(obj.id,'notYetSet'))
                msg = sprintf('No id attribute for character\n\n %s.',xmlwrite(catCharNode));
                err = MException('MatrixCharactersError', msg);
                throw(err);
            end
        end
        function checkIfStatePresentAbsent(obj, catCharNode)
            childNodes = catCharNode.getChildNodes;
            count = childNodes.getLength;
            
            for i=0:count-1
                somethingNode = childNodes.item(i);
                nodeName = somethingNode.getNodeName();
				if strcmp(nodeName,'States')
					stateChildren = somethingNode.getChildNodes;
                    stateChildrenCount = stateChildren.getLength;
                    
                    for j=0:stateChildrenCount-1
                        stateChildNode = stateChildren.item(j);
                        stateChildNodeName = stateChildNode.getNodeName();
                        if strcmp(stateChildNodeName,'StateDefinition')
                            stateDefinitionChildren = stateChildNode.getChildNodes;
                            stateDefinitionChildrenCount = stateDefinitionChildren.getLength;
                            
                            for k=0:stateDefinitionChildrenCount-1
                                stateDefinitionChild = stateDefinitionChildren.item(k);
                                stateDefinitionChildName = stateDefinitionChild.getNodeName();
                                if strcmp(stateDefinitionChildName, 'Representation')
                                    representationChildren = stateDefinitionChild.getChildNodes;
                                    representationChildrenCount = representationChildren.getLength;
                                    
                                    for l=0:representationChildrenCount - 1
                                        representationChild = representationChildren.item(l);
                                        representationChildName = representationChild.getNodeName();
                                        if strcmp(representationChildName,'Label')
                                            stateLabel = char(representationChild.getTextContent);
                                            if strcmp(stateLabel, 'present')
                                                obj.hasStatePresent = true;
                                                %fprintf('name : %s      ---   stateLabel PRESENT %s\n', obj.id, stateLabel);
                                            else 
                                                %fprintf('name : %s      ---   stateLabel %s\n', obj.name, stateLabel);
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
        function loadName(obj, catCharNode)
            childNodes = catCharNode.getChildNodes;
            count = childNodes.getLength; 
            for i=0:count-1
                somethingNode = childNodes.item(i);
                nodeName = somethingNode.getNodeName();
				if strcmp(nodeName,'Representation')
                    labelNodes = somethingNode.getElementsByTagName('Label');
                    labelNode = labelNodes.item(0);
                    obj.name = char(labelNode.getTextContent);
                    return;
				end

            end
        end
        function loadId(obj,catCharNode)
            attributes = catCharNode.getAttributes;
            n_attr = attributes.getLength;
            for i = 0:n_attr-1
                attr = attributes.item(i);
                attrName = attr.getName;
                if (strcmp(attrName, 'id'))
                    obj.id = char(attr.getValue);
                end
            end
        end
	end



end