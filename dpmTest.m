function crfTest
    if ispc
        javaaddpath('.\\java\\lib');
        javaaddpath('.\\java\\bin');
    else
        javaaddpath('java/bin');
        javaaddpath('java/lib');
    end
    
    import edu.oregonstate.eecs.iis.avatolcv.*
    md = MorphobankData(java.lang.String('C:\\avatol\\git\\avatol_cv\\matrix_downloads'));
    md.loadMatrix('BOGUS');
    
    algorithms = Algorithms();
    list_of_characters = {};
    list_of_characters = [ list_of_characters , 'c427749' ];
    list_of_characters = [ list_of_characters , 'c427753' ];
    list_of_characters = [ list_of_characters , 'c427754' ];
    list_of_characters = [ list_of_characters , 'c427760' ];
    input_folder = 'C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input\\DPM\\c427749\\v3540';
    output_folder = 'C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output\\DPM\\c427749\\v3540';
    detection_results_folder = 'C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\detection_results\\DPM\\c427749\\v3540';

    H.statusMessage = uicontrol('style', 'text' ,...
                                     'Units', 'normalized',...
                                     'position', [0 0 1 1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','messageText' ,...
                                     'Background',[1 1 1],...
                                     'String', startingAlgorithmString,...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
		
    H.progressIndicator = ProgressIndicator(H.statusMessage);
    
    algorithms.invoke_the_dpm_system(list_of_characters, input_folder, output_folder, detection_results_folder, H.progressIndicator);
end

            
            
