package edu.oregonstate.eecs.iis.avatolcv.segmentation;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;

public interface SegmentationToolHarness {
    public String getSegmentationLabelDir();
    public List<ImageInfo> getSegmentationCandidateImages();
}
