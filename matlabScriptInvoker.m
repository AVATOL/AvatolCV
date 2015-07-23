function matlabScriptInvoker(varargin)
    argCount = length(varargin);
    if argCount < 3
        fprintf('usage:  matlabScriptInvoke(dirContainingScript, scriptName, args...\n');
    else
        dirContainingScript = varargin{1};
        varargin = varargin(2:length(varargin));
        scriptName = varargin{1};
        varargin = varargin(2:end);
        fprintf('argCount is %d\n', argCount);
       
        
        theFunction = str2func(scriptName);
        newArgList = mat2cell(varargin, 1, ones(1, numel(varargin)));
        theFunction(newArgList{:});
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
