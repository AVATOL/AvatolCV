function avatol_cv
    if ispc
        javaaddpath('.\\java\\lib');
        javaaddpath('.\\java\\bin');
    else
        javaaddpath('java/bin');
        javaaddpath('java/lib');
    end
    
    import edu.oregonstate.eecs.iis.avatolcv.*
    import java.util.List
    import java.lang.String
    import java.lang.System.*
    %
    clearvars();
    clearvars -global H;
    %global H;
    rootDir = pwd();
    avatolSystem = AvatolSystem(java.lang.String(rootDir));

    session = Session(rootDir, avatolSystem);

end
   
















