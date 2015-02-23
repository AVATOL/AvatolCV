function dpmTest
    if ispc
        javaaddpath('.\\java\\lib');
        javaaddpath('.\\java\\bin');
    else
        javaaddpath('java/bin');
        javaaddpath('java/lib');
    end
    
    import edu.oregonstate.eecs.iis.avatolcv.*
    import edu.oregonstate.eecs.iis.avatolcv.mb.*
    %md = MorphobankData(java.lang.String('C:\\avatol\\git\\avatol_cv\\matrix_downloads'));
    %md.loadMatrix('BOGUS');
    
    algorithms = Algorithms();
    list_of_characters = {};
    list_of_characters = [ list_of_characters , 'c427749' ];
    list_of_characters = [ list_of_characters , 'c427752' ];
    list_of_characters = [ list_of_characters , 'c427753' ];
    list_of_characters = [ list_of_characters , 'c427754' ];
    list_of_characters = [ list_of_characters , 'c427760' ];
    input_folder = 'C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input\\DPM\\c427749c427751c427753c427754c427760\\v3540';
    %input_folder = '/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv/matrix_downloads/BAT/input/DPM/t281048/c427749c427753c427754c427760/v3540';
    %input_folder = 'C:\avatol\git\avatol_cv\data\input\DPM\N\vent';
    
    output_folder = 'C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output\\DPM\\c427749c427751c427753c427754c427760\\v3540';
    %output_folder = '/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv/matrix_downloads/BAT/output/DPM/t281048/c427749c427753c427754c427760/v3540';
   
    detection_results_folder = 'C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\detection_results\\DPM\\c427749c427751c427753c427754c427760\\v3540';
    %detection_results_folder = '/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv/matrix_downloads/BAT/detection_results/DPM/t281048/c427749c427753c427754c427760/v3540';
    %detection_results_folder = 'C:\avatol\git\avatol_cv\data\detection_results';
    
    fontname = 'helvetica';
    fontsize = 12;
    startingAlgorithmString = 'something';
    statusMessage = uicontrol('style', 'text' ,...
                                     'Units', 'normalized',...
                                     'position', [0 0 1 1] ,...
                                     'FontName', fontname ,...
                                     'FontSize', fontsize ,...
                                     'Tag','messageText' ,...
                                     'Background',[1 1 1],...
                                     'String', startingAlgorithmString,...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
		
    progressIndicator = ProgressIndicator(statusMessage);
    
    %algorithms.invoke_the_dpm_system(list_of_characters, input_folder, output_folder, detection_results_folder, progressIndicator);
    algorithms.invoke_the_dpm_system(list_of_characters, input_folder, output_folder, detection_results_folder, 'regime1');
end

            
            
