function sliderTest()


    function slider_callback1(src,eventdata,arg1)

        val = get(src,'Value');

        set(arg1,'Position',[0 -val 1 2])
    end


    figure(1);

    panel1 = uipanel('Parent',1);

    panel2 = uipanel('Parent',panel1);

    set(panel1,'Position',[0 0 0.95 1]);

    set(panel2,'Position',[0 -1 1 2]);

    h = image;

    set(gca,'Parent',panel2);

    s = uicontrol('Style','Slider','Parent',1,...
          'Units','normalized','Position',[0.95 0 0.05 1],...
          'Value',1,'Callback',{@slider_callback1,panel2});

end


