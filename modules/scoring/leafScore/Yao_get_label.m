% this function is intend to find the corresponding colomn of 
% lables given:
%               1: annotation --> annotation matrix
%               2: trainlist(testlist)  --> corresponding training list image names
%               3: idx_col    --> which colomn(feature annotations) is going to extract

function train_label = Yao_get_label(annotation,trainlist,idx_col)

L = length(trainlist);
train_label = zeros(L,1);
for i = 1:1:L
    img_name = trainlist{i,1};
    recog = ismember(annotation(:,1),img_name);
    if sum(recog) == 0
        fprintf('This leaf has no annotation:%s!\n',img_name);
        return
    elseif sum(recog) > 1
        fprintf('This leaf:%s has multiple annotations!\n',img_name);
        return
    end
%     idx = find( recog );
%     train_label(i,1) = annotation{idx,idx_col};
    train_label(i,1) = annotation{recog,idx_col};
    
end


