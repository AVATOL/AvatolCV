function crfTest
    inputFilePathname = 'C:\avatol\git\avatol_cv\matrix_downloads\BOGUS\input\sorted_input_data_c521244_Premaxilla body presence.txt';
    outputFilePathname = 'C:\avatol\git\avatol_cv\matrix_downloads\BOGUS\input\sorted_output_data_c521244_Premaxilla body presence.txt'
    options = struct;
            
    options.DETECTION_RESULTS_FOLDER = 'C:\avatol\git\avatol_cv\matrix_downloads\BOGUS\detection_results\';
    dataset_path = 'C:\avatol\git\avatol_cv\matrix_downloads\BOGUS\';
    options.DATASET_PATH = dataset_path;
    
    crf_temp_path = sprintf('%stemp_crf',dataset_path);
    mkdir(crf_temp_path);
    options.TEMP_PATH = crf_temp_path;
    
    hcsearch_dir =  'C:\avatol\git\nematocyst\';
    options.BASE_PATH = hcsearch_dir
    algorithms = Algorithms();
    algorithms.invoke_the_crf_system(inputFilePathname, outputFilePathname, options);
end

            
            
            
            
            
            
            
            
            