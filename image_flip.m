function image_flip

   function full_path_as_string = getFullPathForJava(obj,partialPath)
            curDir = pwd();
            if ispc
                full_path = sprintf('%s\\%s',curDir, partialPath);
            else
                full_path = sprintf('%s/%s',curDir, partialPath);
            end
            full_path_as_string = java.lang.String(full_path);
   end 

    currentDir = pwd();
    [pathstr,name,ext] = fileparts(currentDir);
    while not(strcmp(name,'avatol_cv'))
        cd('..');
        currentDir = pwd();
        [pathstr,name,ext] = fileparts(currentDir);
    end
    if ispc
        javaaddpath('.\\java\\lib');
        javaaddpath('.\\java\\bin');
    else
        javaaddpath('java/bin');
        javaaddpath('java/lib');
    end
    
    import edu.oregonstate.eecs.iis.avatolcv.*;
    import edu.oregonstate.eecs.iis.avatolcv.mb.*;
    import java.util.List;
    import java.lang.String;
    import java.lang.System.*;
    %
    clearvars();
    clearvars -global H;
    %global H;
    rootDir = pwd();
    avatolSystem = AvatolSystem(java.lang.String(rootDir));

    %session = Session(rootDir, avatolSystem);
    matrixDownloadsRootPath = getFullPathForJava('matrix_downloads');
    morphobankData = MorphobankData(matrixDownloadsRootPath);
    fprintf(' path %s ', matrixDownloadRootPath);
end
   