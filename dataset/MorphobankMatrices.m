classdef MorphobankMatrices < handle
    %MORPHOBANKMATRICES Wrapper class for Morphobank matrix data
    %   Knows about the sdd.xml file, the media and annotations directories
    %   and files.
    
    properties
        parentDir;
        matrixDirNames = {};
    end
    
    methods
        function obj = MorphobankMatrices(parentDir)
            obj.parentDir = parentDir;
            obj.loadMatrixDirNames();
        end
        function loadMatrixDirNames(obj)
            A = dir(obj.parentDir);
            %now A has all files and directories. So you can access by loop
            %A(1).name
            %to get only Directory indexes you can do following code.

            myDir = find(vertcat(A.isdir));

            %now myDir has indexes of directory(folders only).

            %to print the name of directory to confirm, you can do it like this.

            %A(myDir).name

            for i=1:length(A)
                if A(i).isdir()
                    name = A(i).name;
                    if (strcmp(name,'.')) 
                        % do nothing
                    elseif strcmp(name,'..')
                        % do nothing
                    else
                        obj.matrixDirNames = [ obj.matrixDirNames, name ];
                    end
                end
                
            end
            
        end
        function path = getSDDFilePath(obj, matrixName)
            path = 'unknown';
            matrixDirPath = sprintf('%s/%s',obj.parentDir, char(matrixName));
            fileList = dir(matrixDirPath);
            for i=1:length(fileList)
                name = fileList(i).name;
                indices = strfind(name, 'sdd.xml');
                if (length(indices) == 1)
                    path = sprintf('%s/%s',matrixDirPath, name);
                end
            end
            if strcmp(path,'unknown')
                msg = sprintf('No sdd file found for matrix %s.',matrixName);
                err = MException('MatrixCharactersError', msg);
                throw(err);
            end
        end
    end
    
end

