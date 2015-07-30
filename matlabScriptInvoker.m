function matlabScriptInvoker(varargin)
    argCount = length(varargin);
    if argCount < 2
        fprintf('usage:  matlabScriptInvoke(dirContainingScript, scriptName, args...\n');
    else
        dirContainingScript = varargin{1};
        %addpath(dirContainingScript);
        
        varargin = varargin(2:length(varargin));
        scriptName = varargin{1};
        varargin = varargin(2:end);
        fprintf('argCount is %d\n', argCount);
        fprintf('dirContainingScript is %s\n', dirContainingScript);
        fprintf('scriptName is %s\n', scriptName);
        
        theFunction = str2func(scriptName);
        newArgList = mat2cell(varargin, 1, ones(1, numel(varargin)));
        hello();
        %theFunction(varargin{:}); <<< use this one once we get functions located
        %theFunction();
        %theFunction(newArgList{:});
        %newArgList = cell2mat(varargin);
        %theFunction(newArgList{:});
        %
        % ___try hardcoded reference to hello() with it compiled in
        %
        
        %THe problem - MCR can only run compiled matlab files! need to compile everything we ship.
        %THis means we have to support two modes - running with the MCR and running with matlab proper
        %THey will need to put into the props file a flag for matlab proper, default will be runtime.
    end
end

function singleArgFunction(arg1)
    fprintf('invoked single arg function with %s\n', char(arg1));
end

function twoArgFunction(arg1, arg2)
    fprintf('invoked double arg function with %s %s\n', char(arg1), char(arg2));
end


function threeArgFunction(arg1, arg2, arg3)
    fprintf('invoked triple arg function with %s %s %s\n', char(arg1), char(arg2), char(arg3));
end
