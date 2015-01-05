package edu.oregonstate.eecs.iis.avatolcv.algata;

public interface ImageSetSupplier {
    public ImageSet getTrainingImageSet();
    public ImageSet getScoredImageSet();
    public ImageSet getUnscoredImageSet();
    public String getTrainingTabTitle();
    public String getScoredTabTitle();
    public String getUnscoredTabTitle();
}
