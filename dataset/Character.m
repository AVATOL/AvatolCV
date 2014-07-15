classdef Character < handle
    properties
	    id = 'notYetSet';
        name = 'notYetSet';
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
        function loadName(obj, catCharNode)
            childNodes = catCharNode.getChildNodes;
            count = childNodes.getLength; 
            for i=0:count-1
                somethingNode = childNodes.item(i);
                nodeName = somethingNode.getNodeName();
				if strcmp(nodeName,'Representation')
					representationChildren = somethingNode.getChildNodes;
                    representationChildrenCount = representationChildren.getLength;
                    for j=0:representationChildrenCount-1
                        childNode = representationChildren.item(j);
                        childNodeName = childNode.getNodeName();
                        if strcmp(childNodeName,'Label')
                            obj.name = char(childNode.getTextContent);
                        end
                    end
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