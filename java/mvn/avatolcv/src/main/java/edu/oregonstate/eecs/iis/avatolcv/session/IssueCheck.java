package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public interface IssueCheck {
	List<DataIssue> runIssueCheck()  throws AvatolCVException;
}
