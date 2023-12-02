import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/IssueTypeSelection.css";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { userState, IssueType, issueTypeState } from "../recoil/atoms";

const IssueTypeSelection = () => {
  const navigate = useNavigate();
  const user = useRecoilValue(userState);
  const setIssueType = useSetRecoilState(issueTypeState);

  useEffect(() => {
    if (user === null) {
      navigate("/login");
    }
  }, [user]);

  const handleIssueSelection = (issueType: IssueType) => {
    setIssueType(issueType);
    navigate("/dashboard");
    // Do something based on the selected role (e.g., navigate to a specific page)
  };

  return (
    <div className="issue-body">
      <div className="issue-container">
        <h2>What do you need help with?</h2>
        <button
          onClick={() => handleIssueSelection(IssueType.Bug)}
          className="btn"
        >
          A bug
        </button>
        <button
          onClick={() => handleIssueSelection(IssueType.ConceptualQuestion)}
          className="btn"
        >
          A conceptual question
        </button>
      </div>
    </div>
  );
};

export default IssueTypeSelection;
