classdef Matrix < handle
    %MATRIX maps to the sdd file's codedDescriptions element
    
    properties
        taxonIds = {};
        matrixRowForTaxonMap = containers.Map();
    end
    
    methods
        function obj = Matrix(domNode)
             codedDescriptions = domNode.getElementsByTagName('CodedDescription');
             for 0=1:length(codedDescriptions)-1
                 codedDescription = codedDescriptions(i);
                 matrixRow = MatrixRow(codedDescription);
                 taxonId = matrixRow.getTaxonId();
                 obj.taxonIds = [ obj.taxonIds, taxonId ];
                 obj.matrixRowForTaxonMap(char(taxonId)) = matrixRow;
             end
        end
        
        function cells = getCellsForCharacter(obj, charId)
            cells = {};
            for i=1:length(obj.taxonIds)
                taxonId = char(obj.taxonIds(i));
                matrixRow = obj.matrixRowForTaxonMap(taxonId);
                cell = matrixRow.getCellForCharacter(charId);
                cells = [ cells, cell ];
            end
        end    
        
        function mediaforCharacter = getMediaAssociatedWithCharacter(obj, charId)
            media
            cells = obj.getCellsForCharacter(charId);
            for i=1:length(cells)
                cell = cells(i);
                
            end    
        end    
    end
    
end

