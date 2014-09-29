classdef Algorithms  < handle
    %ALGORITHMS container for the CV algorithms
    %   Detailed explanation goes here
    
    properties
        
    end
    
    methods
        function obj = Algorithms()
            
        end
        
        function result = getAnswer(obj, answeredQuestions, key)
            result = '?';
            for i=1:length(answeredQuestions)
                aq = answeredQuestions(i);
                id = aq.questionID;
                answer = aq.answer;
                if (strcmp(key,id))
                    result = answer;
                    return;
                end
            end
        end
        function msg = getDisqualifyingMessageForCRF(obj, answeredQuestions)
            msg = '';
            if strcmp(obj.getAnswer(answeredQuestions,'CHARACTER_PROPERTY_OR_PART'),'parts')
                if or((strcmp(obj.getAnswer(answeredQuestions,'BACKGROUND_CLUTTER'),'clutter')),(strcmp(obj.getAnswer(answeredQuestions,'CHARACTER_PART_ARTICULATED'),'yes')))
                     msg = '';
                else
                    msg = 'For CRF, the question named BACKGROUND_CLUTTER needs to have the answer "clutter" or the question named CHARACTER_PART_ARTICULATED needs to have the answer "yes"';
                end
            else
                msg = 'For CRF, the question named CHARACTER_PROPERTY_OR_PART needs to have the answer "parts"';
            end
        end
     
        function msg = getDisqualifyingMessageForDPM(obj, answeredQuestions)
            msg = '';
            if strcmp(obj.getAnswer(answeredQuestions,'CHARACTER_PROPERTY_OR_PART'),'parts')
                if strcmp(obj.getAnswer(answeredQuestions,'ORGANISM_ORIENTATION'),'yes')
                    if strcmp(obj.getAnswer(answeredQuestions,'CHARACTER_PRESENCE'),'yes')
                        msg = '';
                    else
                        msg = 'For DPM, the question named CHARACTER_PRESENCE needs to have the answer "yes"';
                    end
                else
                    msg = 'For DPM, the question named ORGANISM_ORIENTATION needs to have the answer "yes"';
                end
            else
                msg = 'For DPM, the question named CHARACTER_PROPERTY_OR_PART needs to have the answer "parts"';
            end
        end
    
        % I will pass to shell list of simple-part char names, parent_path of input files, 
        % and parent_output_path - he doesn't need to know the character of interest. They will all get scored
        %
        %
        % list_of_characters will be the list of basic presence/absence parts
        % input_path will point to folder containing sorted_input_data_<charID>_<charName>.txt 
        %   which has 
        %       training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID
        %       or
        %       image_to_score:media/<name_of_mediafile>:taxonID 
        %
        % output_path will point to folder where these files should be put: sorted_output_data_<charID>_<charName>.txt  
        %   which has
        %       training_data:media/<name_of_mediafile> :char_state:annotation/<name_of_annotation_file> 
        %       or 
        %       image_scored:media/<name_of_mediafile> :char_state:detection_results/<name_of_annotation_file> 
        %       or 
        %       image_not_scored:media/<name_of_mediafile> 
        % detection_results_folder will point to folder where detection_results should be put (in same form as annotations
        
        function invoke_the_dpm_system(obj, list_of_characters, input_folder, output_folder, detection_results_folder, progress_indicator) 
            invoke_batskull_system(list_of_characters, input_folder, output_folder, detection_results_folder, progress_indicator);
        end
        
	
        %   inputPath   : path/.../sorted_input_data_<charID>_<charName>.txt
        %   outputPath  : path/.../sorted_output_data_<charID>_<charName>.txt
        function invoke_the_crf_system(obj, input_path, output_path, options) 
            invoke_crf_system( input_path, output_path, options )
        end
    end
    
end

