classdef Algorithms  < handle
    %ALGORITHMS container for the CV algorithms
    %   Detailed explanation goes here
    
    properties
        
    end
    
    methods
        function obj = Algorithms()
            
        end
        
        % input_path will point to sorted_input_data_<charID>_<charName>.txt 
        %   which has 
        %       training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID
        %       or
        %       image_to_score:media/<name_of_mediafile>:taxonID 
        %
        % output_path will point to sorted_output_data_<charID>_<charName>.txt  
        %   which has
        %       training_data:media/<name_of_mediafile> :char_state:annotation/<name_of_annotation_file> 
        %       or 
        %       image_scored:media/<name_of_mediafile> :char_state:annotation/<name_of_annotation_file> 
        %       or 
        %       image_not_scored:media/<name_of_mediafile> 

        function invoke_batskull_system(obj, input_path, output_path) 
            
        end
        
        
        % same conventions as mentioned above invoke_batskull_system
        function invoke_crf_system(obj, input_path, output_path) 
            
        end
    end
    
end

