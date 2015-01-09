function avatol_cv
    
    currentDir = pwd();
    fprintf('current dir name is %s',currentDir);
%    [pathstr,name,ext] = fileparts(currentDir);
%    fprintf('current dir name is %s',name);
    % the following loop is for development convenience - if avatol_cv
    % crashes after it changes to a subdir, then this code
    % ensures it "rights" itself on the next launch by cd'ing to the 
    % proper dir first.
%    while not(strcmp(name,'avatol_cv'))
%        cd('..');
%        currentDir = pwd();
%        [pathstr,name,ext] = fileparts(currentDir);
%        fprintf('current dir name is %s',name);
%    end
    
    if ispc
        %javaaddpath('.\\java\\bin');
        %javaaddpath('.\\java\\lib');
        javaBinDir = sprintf('%s\\java\\bin',currentDir);
        javaLibDir = sprintf('%s\\java\\lib',currentDir);
        javaJarFile = sprintf('%s\\java\\lib\\avatol_cv.jar',currentDir);
        javaaddpath(javaBinDir);
        javaaddpath(javaLibDir);
        javaaddpath(javaJarFile);
        %javaaddpath('.\\java\\lib\\avatol_cv.jar');
    else
        javaaddpath('java/bin');
        javaaddpath('java/lib');
        javaaddpath('java/lib/avatol_cv.jar');
    end
    
    %import java.util.List;
    import java.lang.*;
    %import java.lang.System.*;
   
    import edu.oregonstate.eecs.iis.avatolcv.*;
    import edu.oregonstate.eecs.iis.avatolcv.mb.*;
    
    %
    clearvars();
    clearvars -global H;
    %javaclasspath
    %global H;
    rootDir = pwd();
    %rootDirString = java.lang.String(rootDir)
    %avatolSystem = AvatolSystem(rootDirString);
    %session = Session(rootDir, avatolSystem);
    session = Session(rootDir);
end
   
















