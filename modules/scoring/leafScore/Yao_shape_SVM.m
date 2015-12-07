% Yao training SVM and test SVM for each shape character 
function [prob,pred] = Yao_shape_SVM(label,train_label,test_label,train_data,test_data)
display('Learning SVM');
% here using one-agaist-rest approach
numLabels = max(label);
model = cell(numLabels,1);
for k=1:numLabels
    model{k} = svmtrain(double(train_label==k), double(train_data), '-c 1 -b 1 -t 0 -q');
end

display('Testing ...');
%# get probability estimates of test instances using each model
numTest = size(test_data,1);
prob = zeros(numTest,numLabels);
for k=1:numLabels
    [~,~,p] = svmpredict(double(test_label==k), double(test_data), model{k}, '-b 1');
    prob(:,k) = p(:,model{k}.Label==1);    %# probability of class==k
end

%# predict the class with the highest probability
[~,pred] = max(prob,[],2);
% acc = sum(pred == test_label) ./ numel(test_label);    %# accuracy
% C = confusionmat(test_label, pred);                   %# confusion matrix

% C_normal = C/sum(sum(C));
% figure;
% imshow(C_normal,'InitialMagnification',5000);
% colormap(jet);
end
